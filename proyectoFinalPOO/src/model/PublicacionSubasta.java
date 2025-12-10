/*
 * Clase: PublicacionSubasta
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Modelo de publicaciÃ³n tipo subasta.
 */

package model;

import java.util.Date;
import java.util.List;

import util.TipoPublicacion;

public class PublicacionSubasta extends Publicacion {
	
	private double precioMinimo;
	private Date fechaCierre;
	
	public PublicacionSubasta(String idArticulo, String titulo, String descripcion, 
            String idVendedor, List<String> fotosPaths, 
            double precioMinimo, Date fechaVencimiento, Date fechaCierre) {
		super(idArticulo, titulo, descripcion, idVendedor, fotosPaths, TipoPublicacion.SUBASTA);
		this.precioMinimo = precioMinimo;
		this.fechaCierre = fechaCierre;
	}

	/*
	 * Verifica si la subasta ha vencido
	 */
	public boolean estaVencida() {
		Date ahora = new Date();
		return ahora.after(fechaCierre);
	}
	
	//Getters y setters
	
	public double getPrecioMinimo() {
		return precioMinimo;
	}

	public void setPrecioMinimo(double precioMinimo) {
		this.precioMinimo = precioMinimo;
	}

	public Date getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}
}
