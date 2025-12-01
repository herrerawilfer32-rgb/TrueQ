package model;

import java.util.Date;
import java.util.List;

import java.io.Serializable;
import util.EstadoPublicacion;
import util.TipoPublicacion;

public abstract class Publicacion implements Serializable {

	private static final long serialVersionUID = 1L;

	// Atributos del artículo

	private String idArticulo;
	private String titulo;
	private String descripcion;

	/*
	 * Guardamos la clave del objeto User
	 * para luego obtener su usuario, su ubicacion y su id
	 */
	private String idVendedor;

	private Date fechaPublicacion;
	private EstadoPublicacion estado;
	protected TipoPublicacion tipoPublicacion;

	// Lista de rutas a las fotos
	private List<String> fotosPaths;

	// Getters y setters

	public String getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(String idArticulo) {
		this.idArticulo = idArticulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getIdVendedor() {
		return idVendedor;
	}

	public void setIdVendedor(String idVendedor) {
		this.idVendedor = idVendedor;
	}

	public Date getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(Date fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public EstadoPublicacion getEstado() {
		return estado;
	}

	public void setEstado(EstadoPublicacion estado) {
		this.estado = estado;
	}

	public List<String> getFotosPaths() {
		if (fotosPaths == null) {
			return new java.util.ArrayList<>();
		}
		return fotosPaths;
	}

	public void setFotosPaths(List<String> fotosPaths) {
		this.fotosPaths = fotosPaths;
	}

	// Método constructor

	public Publicacion(String idArticulo, String titulo, String descripcion, String idVendedor, List<String> fotosPaths,
			TipoPublicacion tipoPublicacion) {
		this.idArticulo = idArticulo;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.idVendedor = idVendedor;
		this.fechaPublicacion = new Date();
		this.fotosPaths = fotosPaths;
		this.tipoPublicacion = tipoPublicacion;
		this.estado = EstadoPublicacion.ACTIVA; // Empieza activa
	}

	public TipoPublicacion getTipoPublicacion() {
		return tipoPublicacion;
	}

	public void setTipoPublicacion(TipoPublicacion tipoPublicacion) {
		this.tipoPublicacion = tipoPublicacion;
	}

	@Override
	public String toString() {
		return this.titulo;
	}

}
