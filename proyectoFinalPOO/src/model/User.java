package model;

import java.io.Serializable;
import java.util.ArrayList;
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

	// Rol del usuario (USUARIO o ADMIN)
	private RolUsuario rol;

	// Método constructor
	public User(String nombreUsuario, String nombre, String apellido, String correo, String contraseñaHash, String id,
			String ubicacion) {
		this.nombreUsuario = nombreUsuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.correo = correo;
		this.contraseñaHash = contraseñaHash;
		this.id = id;
		this.ubicacion = ubicacion;

		// Inicialización de nuevos atributos
		this.reputacion = 0.0;
		this.numeroCalificaciones = 0;
		this.historialTransacciones = new ArrayList<>();
		this.rol = RolUsuario.USUARIO; // Por defecto es usuario regular
	}

	// Métodos de Lógica de Negocio (Dominio)

	public void calificar(int estrellas) {
		if (estrellas < 1 || estrellas > 5)
			return;

		double totalPuntos = (this.reputacion * this.numeroCalificaciones) + estrellas;
		this.numeroCalificaciones++;
		this.reputacion = totalPuntos / this.numeroCalificaciones;
	}

	public void agregarTransaccion(String idPublicacion) {
		if (this.historialTransacciones == null) {
			this.historialTransacciones = new ArrayList<>();
		}
		this.historialTransacciones.add(idPublicacion);
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

	public List<String> getHistorialTransacciones() {
		return historialTransacciones;
	}

	// Métodos para rol
	public RolUsuario getRol() {
		return rol;
	}

	public void setRol(RolUsuario rol) {
		this.rol = rol;
	}

	public boolean isAdmin() {
		return this.rol == RolUsuario.ADMIN;
	}
}