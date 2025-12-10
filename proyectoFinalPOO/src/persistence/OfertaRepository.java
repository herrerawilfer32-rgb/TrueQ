/**
 * Clase:  OfertaRepository
 * Gestionaa la persistencia de ofertas realizadas por usuarios dentro del sistema.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.1
 */

package persistence;

import model.Oferta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OfertaRepository {

	// 1. Base de datos principal (busqueda por ID de oferta)
	private static Map<String, Oferta> baseDeDatos = new HashMap<>();
	private static final String RUTA_ARCHIVO = "data/ofertas.dat";

	// 2. Indice secundario (Busqueda por publicacion)
	// HashMap<String, List<Oferta>
	private static Map<String, List<Oferta>> indicePorPublicacion = new HashMap<>();

	public OfertaRepository() {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Oferta> loaded = (Map<String, Oferta>) Persistencia.cargarObjeto(RUTA_ARCHIVO);
			if (loaded != null)
				baseDeDatos = loaded;
			// Reconstruir índice secundario
			reconstruirIndice();
		} catch (Exception e) {
			baseDeDatos = new HashMap<>();
			indicePorPublicacion = new HashMap<>();
		}
	}

	private static void reconstruirIndice() {
		indicePorPublicacion = new HashMap<>();
		for (Oferta oferta : baseDeDatos.values()) {
			String idPub = oferta.getIdPublicacion();
			indicePorPublicacion.putIfAbsent(idPub, new ArrayList<>());
			indicePorPublicacion.get(idPub).add(oferta);
		}
	}

	/**
	 * Guarda o actualiza un objeto Oferta.
	 */
	public void guardar(Oferta oferta) {
		// Guardar en la estructura principal
		baseDeDatos.put(oferta.getIdOferta(), oferta);

		// Actualizar el indice secundario
		String idPub = oferta.getIdPublicacion();

		// Si no existe la lista para esa publicacion, la creamos
		indicePorPublicacion.putIfAbsent(idPub, new ArrayList<>());

		// Obtenemos la lista y agregamos la oferta
		List<Oferta> ofertasDeLaPublicacion = indicePorPublicacion.get(idPub);

		// Opcional para evitar duplicados
		if (!ofertasDeLaPublicacion.contains(oferta)) {
			ofertasDeLaPublicacion.add(oferta);
		}

		try {
			Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Busca una oferta por su ID de Oferta.
	 */
	public Oferta buscarPorIdOferta(String idOferta) {
		return baseDeDatos.get(idOferta);
	}

	/**
	 * Busca todas las ofertas realizadas por un ofertante específico.
	 */
	public List<Oferta> buscarOfertasPorOfertante(String idOfertante) {
		return baseDeDatos.values().stream()
				.filter(oferta -> oferta.getIdOfertante().equals(idOfertante))
				.collect(Collectors.toList());
	}

	// Buscar todas las ofertas
	public List<Oferta> buscarTodasLasOfertas() {
		return new ArrayList<>(baseDeDatos.values());
	}

	// Buscar ofertas por idPublicacion
	public List<Oferta> buscarPorPublicacion(String idPublicacion) {
		return indicePorPublicacion.getOrDefault(idPublicacion, new ArrayList<>());
	}

	public void eliminar(String idOferta) {
		Oferta oferta = baseDeDatos.remove(idOferta);
		if (oferta != null) {
			// Eliminar del índice secundario
			String idPub = oferta.getIdPublicacion();
			if (indicePorPublicacion.containsKey(idPub)) {
				indicePorPublicacion.get(idPub).remove(oferta);
			}
			// Guardar cambios
			try {
				Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
