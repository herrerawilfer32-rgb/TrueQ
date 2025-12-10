/*
 * Clase: PublicacionTrueque
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Modelo de publicaciÃ³n tipo trueque.
 */

package model;

import java.util.List;

import util.TipoPublicacion;

public class PublicacionTrueque extends Publicacion {

	private String objetosDeseados;

	public PublicacionTrueque(String idArticulo, String titulo, String descripcion,
			String idVendedor, List<String> fotosPaths,
			String objetosDeseados) {
		super(idArticulo, titulo, descripcion, idVendedor, fotosPaths, TipoPublicacion.TRUEQUE);
		this.objetosDeseados = objetosDeseados;
	}

	/*
	 * Método para encontrar Match entre objetos deseados y ofrecidos
	 */
	public boolean hayIntereses(String descripcionTrueque) {
		// Chequeamos si la descripcion del trueque contiene alguno de los objetos
		// deseados
		String[] deseosArray = objetosDeseados.toLowerCase().split(","); // Suponemos que los objetos deseados están
																			// separados por comas
		String descripcionLower = descripcionTrueque.toLowerCase();

		for (String deseo : deseosArray) {
			if (descripcionLower.contains(deseo.trim())) {
				return true; // Hay al menos un objeto deseado en la descripción del trueque
			}
		}
		return false; // No se encontraron coincidencias
	}

	// Getters y setters
	public String getObjetosDeseados() {
		return objetosDeseados;
	}

	public void setObjetosDeseados(String objetosDeseados) {
		this.objetosDeseados = objetosDeseados;
	}

}
