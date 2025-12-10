/**
 * Clase: Oferta
 * Modelo base de una publicación.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.1
 */

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

	// Nuevos campos: categoría y condición
	private String categoria;
	private util.CondicionArticulo condicion;

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

	/**
     * Constructor base para toda publicación.
     *
     * @param idArticulo       Identificador único asignado al artículo/publicación.
     * @param titulo           Título de la publicación mostrado al usuario.
     * @param descripcion      Descripción detallada del artículo o servicio.
     * @param idVendedor       ID del usuario que creó la publicación.
     * @param fotosPaths       Lista de rutas hacia las imágenes asociadas.
     * @param tipoPublicacion  Tipo específico de publicación (venta, trueque, subasta, etc.).
     */
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

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public util.CondicionArticulo getCondicion() {
		return condicion;
	}

	public void setCondicion(util.CondicionArticulo condicion) {
		this.condicion = condicion;
	}

	@Override
	public String toString() {
		return this.titulo;
	}

}
