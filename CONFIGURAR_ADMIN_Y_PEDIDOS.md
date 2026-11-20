# Configurar administrador y compras web

## Crear el primer administrador

La cuenta existente de la captura tiene rol `CLIENTE`; no permite entrar a la intranet. El proyecto puede crear una cuenta independiente con rol `ADMIN` al arrancar.

1. Ejecuta una vez `ProyectoRincocito/src/main/resources/sql/crear_tablas_seguridad.sql` en la base `tu_rinconcito` si las tablas `usuarios` y `clientes` aún no existen.
2. Copia `.env.example` a `.env` en la carpeta raíz del proyecto, ábrelo y define un usuario administrador distinto al correo del cliente y una contraseña privada de al menos 12 caracteres. VS Code ya carga ese archivo al iniciar Spring. El `.env` está excluido de Git:

   ```powershell
   Copy-Item .env.example .env
   ```

3. Desde VS Code inicia la aplicación. Si la inicias con Maven en otra terminal, exporta las variables `APP_BOOTSTRAP_ADMIN_USERNAME` y `APP_BOOTSTRAP_ADMIN_PASSWORD` antes de ejecutar `mvn spring-boot:run`. El primer arranque crea la cuenta con rol `ADMIN`, activa y con contraseña BCrypt.
4. Inicia sesión en `/login` con esas credenciales. Cuando ya exista el usuario, el bootstrap no modifica su contraseña ni su rol en los siguientes arranques.

No guardes la contraseña en este archivo ni en Git. Si cambias de terminal, tendrás que volver a configurar las dos variables antes de iniciar la app.

## Flujo de compra

El catálogo público toma nombre, precio, categoría y stock de `productos`. El carrito mantiene solo los IDs y cantidades en el navegador; al confirmar, el servidor vuelve a leer precios y stock desde MySQL, guarda la venta y sus detalles, descuenta inventario y crea movimientos de salida en una transacción.

El comprador debe iniciar sesión con una cuenta `CLIENTE` vinculada a un registro en `clientes` con el mismo correo que `usuarios.username`. El nombre aparece en la página principal y sus pedidos se muestran en “Mis compras”. `ddl-auto=none` no crea ni actualiza tablas. Si faltan, corre `crear_tablas_seguridad.sql`; con `productos` ya creada, también puedes correr `crear_tablas_pedidos.sql` y `crear_tabla_movimientos.sql`. Los scripts `IF NOT EXISTS` crean tablas faltantes, pero no corrigen tablas preexistentes con columnas diferentes.

## Crear vendedores y preparar pedidos

Inicia sesión como administrador y abre `/usuarios`. El formulario “Agregar vendedor” crea una cuenta activa con rol `VENDEDOR` y contraseña BCrypt. Al iniciar sesión, el vendedor entra directamente a `/vendedor/pedidos`; allí puede revisar cliente, artículos, fecha y total, y marcar pedidos web como armados. Los vendedores no pueden entrar a las pantallas administrativas.

Antes de arrancar la aplicación con esta versión, ejecuta `ProyectoRincocito/src/main/resources/sql/migrar_estado_pedidos.sql` con `tu_rinconcito` seleccionada. La migración agrega `ventas.estado` si aún no existe. Los pedidos nuevos de la tienda quedan `PENDIENTE`; las ventas manuales de intranet quedan `COMPLETADO`. Después de marcar un pedido como armado, el cliente verá el estado en “Mis compras”.
