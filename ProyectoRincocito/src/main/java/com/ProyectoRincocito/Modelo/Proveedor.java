package com.ProyectoRincocito.Modelo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Validación del nombre de la empresa
    @NotBlank(message = "El nombre de la empresa no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String empresa;

    // Validación del RUC
    @NotBlank(message = "El RUC no puede estar vacío")
    @Column (nullable = false, length = 50)
    private String ruc;

    // Validación del contacto
    @NotBlank(message = "El contacto no puede estar vacío")
    @Column (length = 120)
    private String contacto;

    // Validación del teléfono
    @NotBlank (message = "El telefono no puede estar vacio")
    @Column (length = 100)
    private String telefono;

    // Validación del correo
    @NotBlank(message = "El correo no puede estar vacío")
    @Column (length = 120)
    private String correo;

public Proveedor() {
    }

    public Long getId() {
        return id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}

