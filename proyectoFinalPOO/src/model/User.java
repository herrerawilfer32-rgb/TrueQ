/*
 * Clase: User
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Modelo que representa un usuario del sistema.
 */

package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.RolUsuario;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreUsuario; // Identificador de Login
    private String nombre;
    private String apellido;
    private String correo;
    private String contraseñaHash;
    private String id; // Identificador Único (Cédula)
    private String ubicacion;

    // Nuevos atributos para Fase 3 (Social/Reputación)
    private double reputacion; // Promedio 0.0 - 5.0
    private int numeroCalificaciones;
    private List<String> historialTransacciones;
    private List<String> publicacionesCalificadas; // IDs de publicaciones que ESTE usuario ha calificado

    // Rol del usuario (USUARIO o ADMIN)
    private RolUsuario rol;

    // Constructor

    public User(String nombreUsuario, String nombre, String apellido, String correo,
            String contraseñaHash, String id, String ubicacion) {

        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contraseñaHash = contraseñaHash;
        this.id = id;
        this.ubicacion = ubicacion;

        // Inicialización
        this.reputacion = 0.0;
        this.numeroCalificaciones = 0;
        this.historialTransacciones = new ArrayList<>();
        this.publicacionesCalificadas = new ArrayList<>();
        this.rol = RolUsuario.USUARIO;
    }

    // Métodos de Lógica de Usuario

    /** Añade una calificación al usuario. */
    public void calificar(int estrellas) {
        if (estrellas < 1 || estrellas > 5)
            return;

        double totalPuntos = (this.reputacion * this.numeroCalificaciones) + estrellas;
        this.numeroCalificaciones++;
        this.reputacion = totalPuntos / this.numeroCalificaciones;
    }

    /** Agrega una transacción al historial. */
    public void agregarTransaccion(String idPublicacion) {
        if (this.historialTransacciones == null) {
            this.historialTransacciones = new ArrayList<>();
        }
        this.historialTransacciones.add(idPublicacion);
    }

    /** Registra que el usuario ha calificado una publicación. */
    public void agregarPublicacionCalificada(String idPublicacion) {
        if (this.publicacionesCalificadas == null) {
            this.publicacionesCalificadas = new ArrayList<>();
        }
        if (!this.publicacionesCalificadas.contains(idPublicacion)) {
            this.publicacionesCalificadas.add(idPublicacion);
        }
    }

    /** Verifica si el usuario ya calificó esta publicación. */
    public boolean haCalificadoPublicacion(String idPublicacion) {
        if (this.publicacionesCalificadas == null)
            return false;
        return this.publicacionesCalificadas.contains(idPublicacion);
    }

    public List<String> getPublicacionesCalificadas() {
        if (publicacionesCalificadas == null)
            return new ArrayList<>();
        return Collections.unmodifiableList(publicacionesCalificadas);
    }

    /** Devuelve nombre completo (útil en chat y UI). */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Retorna historial como lista inmutable para evitar manipulaciones externas.
     */
    public List<String> getHistorialTransacciones() {
        return Collections.unmodifiableList(historialTransacciones);
    }

    // Getters y Setters

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseñaHash() {
        return contraseñaHash;
    }

    public void setContraseñaHash(String contraseñaHash) {
        this.contraseñaHash = contraseñaHash;
    }

    // Alias para compatibilidad
    public String getEmail() {
        return getCorreo();
    }

    public void setEmail(String email) {
        setCorreo(email);
    }

    public void setPassword(String password) {
        setContraseñaHash(password);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getReputacion() {
        return reputacion;
    }

    public int getNumeroCalificaciones() {
        return numeroCalificaciones;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public boolean isAdmin() {
        return this.rol == RolUsuario.ADMIN;
    }

    // Métodos imprescindibles para Chat y persistencia

    /**
     * Basado estrictamente en ID para que ChatController pueda comparar usuarios.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return id != null && id.equals(other.id);
    }

    /** Debe acompañar siempre a equals. */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /** Para debug y logs. */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", usuario='" + nombreUsuario + '\'' +
                '}';
    }
}
