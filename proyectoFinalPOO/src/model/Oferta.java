/*
 * Nombre de la clase: Oferta
 * Autor: Angel 
 * Descripción: Se crea una oferta realizada sobre una publicación, con información como identificador de la oferta, el usuario ofertante, la fecha 
 * ,monto ofrecido, la descripción del trueque, las imágenes adjuntas y el estado actual de la oferta
 */

package model;

import util.EstadoOferta;

import java.util.Date;

import java.io.Serializable;

public class Oferta implements Serializable {

	private static final long serialVersionUID = 1L;

	private String idOferta;
	private String idPublicacion;
	private String idOfertante;
	private Date fechaOferta;

	private double montoOferta; // Monto en dinero ofrecido
	private String descripcionTrueque; // Descripción del trueque ofrecido
	private java.util.List<String> rutasImagenes; // Imágenes adjuntas (para trueques)

	private util.EstadoOferta estadoOferta;

	public Oferta(String idOferta, String idPublicacion, String idOfertante,
			Date fechaOferta, double montoOferta,
			String descripcionTrueque, EstadoOferta estadoOferta) {
		this.idOferta = idOferta;
		this.idPublicacion = idPublicacion;
		this.idOfertante = idOfertante;
		this.fechaOferta = fechaOferta;
		this.montoOferta = montoOferta;
		this.descripcionTrueque = descripcionTrueque;
		this.estadoOferta = estadoOferta;
		this.rutasImagenes = new java.util.ArrayList<>();
	}

	// Getters y setters

	public String getIdOferta() {
		return idOferta;
	}

	public void setIdOferta(String idOferta) {
		this.idOferta = idOferta;
	}

	public String getIdPublicacion() {
		return idPublicacion;
	}

	public void setIdPublicacion(String idPublicacion) {
		this.idPublicacion = idPublicacion;
	}

	public String getIdOfertante() {
		return idOfertante;
	}

	public void setIdOfertante(String idOfertante) {
		this.idOfertante = idOfertante;
	}

	public Date getFechaOferta() {
		return fechaOferta;
	}

	public void setFechaOferta(Date fechaOferta) {
		this.fechaOferta = fechaOferta;
	}

	public double getMontoOferta() {
		return montoOferta;
	}

	public void setMontoOferta(double montoOferta) {
		this.montoOferta = montoOferta;
	}

	public String getDescripcionTrueque() {
		return descripcionTrueque;
	}

	public void setDescripcionTrueque(String descripcionTrueque) {
		this.descripcionTrueque = descripcionTrueque;
	}

	public util.EstadoOferta getEstadoOferta() {
		if (estadoOferta == null) {
			return util.EstadoOferta.PENDIENTE;
		}
		return estadoOferta;
	}

	public void setEstadoOferta(util.EstadoOferta estadoOferta) {
		this.estadoOferta = estadoOferta;
	}

	public java.util.List<String> getRutasImagenes() {
		if (rutasImagenes == null) {
			rutasImagenes = new java.util.ArrayList<>();
		}
		return rutasImagenes;
	}

	public void setRutasImagenes(java.util.List<String> rutasImagenes) {
		this.rutasImagenes = rutasImagenes;
	}

}
