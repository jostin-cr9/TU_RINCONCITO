package com.ProyectoRincocito.Modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 10, message = "El código no puede superar 10 caracteres")
    @Column(name = "codigo", nullable = false, length = 10)
    private String codigo;

    // Validación del nombre del producto
    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    // Validación de la descripción del producto
    @NotBlank(message = "La descripción del producto no puede estar vacía")
    @Column(nullable = false, length = 100)
    private String descripcion;

    // Validación del precio del producto
    @NotNull(message = "El precio del producto no puede ser nulo")
    @Min(value = 1, message = "El precio del producto debe ser un valor positivo")
    @DecimalMin(value = "0.50", message = "El precio del producto debe ser mayor que los 0.50centimos")
    @DecimalMax(value = "500.00", message = "El precio del producto debe ser menor que los 500.00")
    @Column(nullable = false)
    private Double precio;

    // Validación del stock del producto
    @NotNull(message = "El stock del producto no puede ser nulo")
    @Min(value = 0, message = "El stock del producto debe ser un valor positivo")
    @Column(nullable = false)
    private Integer stock;

    // Validación de la categoría del producto
    @NotBlank(message = "La categoría del producto no puede estar vacía")
    @Column(nullable = false, length = 100)
    private String categoria;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}
