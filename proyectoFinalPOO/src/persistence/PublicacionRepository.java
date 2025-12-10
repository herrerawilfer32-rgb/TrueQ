/*
 * Clase: PublicacionRepository
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Repositorio de persistencia de publicaciones.
 */

package persistence;

import model.Publicacion;
import util.EstadoPublicacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PublicacionRepository {

	// Simulación de la "Base de Datos": Key: idArticulo (String), Value: Objeto
	// Publicacion
	private static Map<String, Publicacion> baseDeDatos = new HashMap<>();
	private static final String RUTA_ARCHIVO = "data/publicaciones.dat";

	public PublicacionRepository() {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Publicacion> loaded = (Map<String, Publicacion>) Persistencia.cargarObjeto(RUTA_ARCHIVO);
			if (loaded != null)
				baseDeDatos = loaded;
		} catch (Exception e) {
			baseDeDatos = new HashMap<>();
		}
	}

	/**
	 * Guarda o actualiza un objeto Publicacion, usando el idArticulo como clave.
	 */
	public void guardar(Publicacion publicacion) {
		baseDeDatos.put(publicacion.getIdArticulo(), publicacion);
		guardarCambios();
	}

	/**
	 * Elimina una publicación por su ID.
	 */
	public void eliminar(String idArticulo) {
		baseDeDatos.remove(idArticulo);
		guardarCambios();
	}

	private void guardarCambios() {
		try {
			Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Busca una publicación por su ID de Artículo.
	 */
	public Publicacion buscarPorIdArticulo(String idArticulo) {
		return baseDeDatos.get(idArticulo);
	}

	/**
	 * Busca todas las publicaciones con un estado activo.
	 */
	public List<Publicacion> buscarPublicacionesActivas() {
		return baseDeDatos.values().stream()
				.filter(pub -> pub.getEstado() == EstadoPublicacion.ACTIVA)
				.collect(Collectors.toList());
	}

	// Buscar todas las publicaciones
	public List<Publicacion> buscarTodasLasPublicaciones() {
		return new ArrayList<>(baseDeDatos.values());
	}

	/**
	 * Busca todas las publicaciones de un vendedor específico.
	 */
	public List<Publicacion> buscarPublicacionesPorVendedor(String idVendedor) {
		return baseDeDatos.values().stream()
				.filter(pub -> pub.getIdVendedor().equals(idVendedor))
				.collect(Collectors.toList());
	}

}
