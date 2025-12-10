/**
 * Clase: PublicacionService
 * Servicio de lógica de publicaciones.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.0
 */

package service;

import java.util.List;

import model.Publicacion;
import model.PublicacionTrueque;
import model.User;
import persistence.OfertaRepository;
import persistence.PublicacionRepository;
import util.EstadoOferta;
import util.EstadoPublicacion;
import util.TipoPublicacion;

/**
 * Servicio de negocio para manejar publicaciones.
 */
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final UserService userService;
    private final OfertaRepository ofertaRepository;

    public PublicacionService(PublicacionRepository publicacionRepository,
            UserService userService,
            OfertaRepository ofertaRepository) {
        this.publicacionRepository = publicacionRepository;
        this.userService = userService;
        this.ofertaRepository = ofertaRepository;
    }

    /**
     * Guarda una publicación nueva.
     */
    public void guardarPublicacion(Publicacion publicacion) {
        if (publicacion == null) {
            throw new IllegalArgumentException("La publicación no puede ser nula.");
        }
        publicacionRepository.guardar(publicacion);
    }

    /**
     * Busca todas las publicaciones activas.
     */
    public List<Publicacion> buscarPublicacionesActivas() {
        return publicacionRepository.buscarPublicacionesActivas();
    }

    /**
     * Devuelve todas las publicaciones de un vendedor.
     */
    public List<Publicacion> obtenerPublicacionesPorVendedor(String idVendedor) {
        return publicacionRepository.buscarPublicacionesPorVendedor(idVendedor);
    }

    /**
     * Busca una publicación por su ID.
     */
    public Publicacion buscarPublicacionPorId(String idPublicacion) {
        if (idPublicacion == null || idPublicacion.isBlank()) {
            throw new IllegalArgumentException("El id de la publicación no puede ser nulo o vacío.");
        }
        return publicacionRepository.buscarPorIdArticulo(idPublicacion);
    }

    /**
     * Devuelve el usuario vendedor (dueño) de una publicación.
     */
    public User obtenerVendedorDePublicacion(String idPublicacion) {
        Publicacion publicacion = buscarPublicacionPorId(idPublicacion);
        if (publicacion == null) {
            throw new IllegalArgumentException("La publicación no existe.");
        }
        return userService.buscarUsuarioPorId(publicacion.getIdVendedor());
    }

    /**
     * Elimina una publicación si el usuario solicitante es el dueño.
     * En lugar de borrarla físicamente, marca su estado como ELIMINADA.
     */
    public boolean eliminarPublicacion(String idPublicacion, String idUsuarioSolicitante) {
        if (idPublicacion == null || idPublicacion.isBlank()) {
            throw new IllegalArgumentException("El id de la publicación no puede ser nulo ni vacío.");
        }
        if (idUsuarioSolicitante == null || idUsuarioSolicitante.isBlank()) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo ni vacío.");
        }

        Publicacion pub = publicacionRepository.buscarPorIdArticulo(idPublicacion);
        if (pub != null && pub.getIdVendedor().equals(idUsuarioSolicitante)) {
            pub.setEstado(EstadoPublicacion.ELIMINADA);
            publicacionRepository.guardar(pub);
            return true;
        }
        return false;
    }

    /**
     * Cierra una subasta: valida dueño y tipo SUBASTA, busca la mejor oferta,
     * la marca como ACEPTADA y cambia el estado de la publicación a CERRADA.
     */
    public void cerrarSubasta(String idPublicacion, String idVendedor) {
        Publicacion publicacion = publicacionRepository.buscarPorIdArticulo(idPublicacion);

        if (publicacion == null || !publicacion.getIdVendedor().equals(idVendedor)) {
            throw new IllegalArgumentException("Publicación no encontrada o no pertenece al vendedor.");
        }

        if (publicacion.getTipoPublicacion() != TipoPublicacion.SUBASTA) {
            throw new IllegalArgumentException("Esta publicación no es una subasta.");
        }

        // Buscar la mejor oferta (mayor monto)
        List<model.Oferta> ofertas = ofertaRepository.buscarPorPublicacion(idPublicacion);
        model.Oferta mejorOferta = null;

        for (model.Oferta oferta : ofertas) {
            if (mejorOferta == null || oferta.getMontoOferta() > mejorOferta.getMontoOferta()) {
                mejorOferta = oferta;
            }
        }

        if (mejorOferta != null) {
            mejorOferta.setEstadoOferta(EstadoOferta.ACEPTADA);
            ofertaRepository.guardar(mejorOferta);
            System.out.println("Subasta cerrada. Ganador: " + mejorOferta.getIdOfertante());
        } else {
            System.out.println("Subasta cerrada sin ofertas.");
        }

        publicacion.setEstado(EstadoPublicacion.CERRADA);
        publicacionRepository.guardar(publicacion);
    }

    /**
     * Recomienda posibles trueques para una publicación de tipo TRUEQUE.
     */
    /**
     * Recomienda posibles trueques para una publicación de tipo TRUEQUE.
     * Utiliza coincidencia de palabras clave entre 'objetosDeseados' y
     * título/categoría de otras publicaciones.
     */
    public List<Publicacion> recomendarTrueques(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.buscarPorIdArticulo(idPublicacion);
        if (publicacion == null || publicacion.getTipoPublicacion() != TipoPublicacion.TRUEQUE) {
            return java.util.Collections.emptyList();
        }

        PublicacionTrueque trueque = (PublicacionTrueque) publicacion;
        String deseos = trueque.getObjetosDeseados().toLowerCase();

        // Separar deseos por comas o espacios para buscar palabras clave
        String[] palabrasClave = deseos.split("[,\\s]+");

        List<Publicacion> activas = publicacionRepository.buscarPublicacionesActivas();
        List<Publicacion> recomendaciones = new java.util.ArrayList<>();

        for (Publicacion p : activas) {
            if (p.getIdArticulo().equals(idPublicacion)) {
                continue; // No recomendarse a sí misma
            }
            if (p.getIdVendedor().equals(publicacion.getIdVendedor())) {
                continue; // No recomendar publicaciones del mismo usuario
            }

            String titulo = p.getTitulo().toLowerCase();
            String categoria = p.getCategoria() != null ? p.getCategoria().toLowerCase() : "";

            boolean hayCoincidencia = false;
            for (String palabra : palabrasClave) {
                if (palabra.length() > 3) { // Ignorar palabras muy cortas (de, la, el...)
                    if (titulo.contains(palabra) || categoria.contains(palabra)) {
                        hayCoincidencia = true;
                        break;
                    }
                }
            }

            if (hayCoincidencia) {
                recomendaciones.add(p);
            }
        }

        return recomendaciones;
    }

    /**
     * Cierra una publicación cambiando su estado a CERRADA (no solo subastas).
     */
    public void cerrarPublicacion(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.buscarPorIdArticulo(idPublicacion);

        if (publicacion != null) {
            publicacion.setEstado(EstadoPublicacion.CERRADA);
            publicacionRepository.guardar(publicacion);
        }
    }

    /**
     * Marca una publicación como FINALIZADA (transacción completada exitosamente).
     */
    public void finalizarPublicacion(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.buscarPorIdArticulo(idPublicacion);
        if (publicacion != null) {
            publicacion.setEstado(EstadoPublicacion.FINALIZADA);
            publicacionRepository.guardar(publicacion);
        }
    }

    /**
     * Actualiza una publicación si el usuario solicitante es el dueño.
     */
    public boolean actualizarPublicacion(Publicacion publicacion, String idUsuarioSolicitante) {
        if (publicacion == null) {
            throw new IllegalArgumentException("La publicación no puede ser nula.");
        }

        Publicacion pubExistente = publicacionRepository.buscarPorIdArticulo(publicacion.getIdArticulo());

        if (pubExistente != null && pubExistente.getIdVendedor().equals(idUsuarioSolicitante)) {
            publicacionRepository.guardar(publicacion);
            return true;
        }
        return false;
    }

    /**
     * Obtiene un usuario por su ID.
     */
    public User obtenerUsuarioPorId(String idUsuario) {
        return userService.buscarUsuarioPorId(idUsuario);
    }

    /**
     * Busca publicaciones filtradas por la ciudad del vendedor.
     */
    /**
     * Busca publicaciones con filtros avanzados: Ciudad, Tipo, Rango de Precio,
     * Categoría y Condición.
     */
    public List<Publicacion> listarPublicacionesConFiltros(String ciudadQuery, String tipo, Double minPrecio,
            Double maxPrecio, String categoria, util.CondicionArticulo condicion) {
        List<Publicacion> todas = buscarPublicacionesActivas();
        List<Publicacion> filtradas = new java.util.ArrayList<>();

        String ciudadBusqueda = (ciudadQuery != null && !ciudadQuery.isBlank()) ? normalizarTexto(ciudadQuery) : null;

        for (Publicacion p : todas) {
            boolean coincide = true;

            // 1. Filtro por Ciudad
            if (ciudadBusqueda != null) {
                User vendedor = userService.buscarUsuarioPorId(p.getIdVendedor());
                if (vendedor == null || vendedor.getUbicacion() == null) {
                    coincide = false;
                } else {
                    String ubicacionVendedor = normalizarTexto(vendedor.getUbicacion());
                    if (!ubicacionVendedor.contains(ciudadBusqueda)) {
                        coincide = false;
                    }
                }
            }

            // 2. Filtro por Tipo
            if (coincide && tipo != null && !tipo.equals("TODOS")) {
                if (!p.getTipoPublicacion().toString().equals(tipo)) {
                    coincide = false;
                }
            }

            // 3. Filtro por Precio (Solo aplica a Subastas)
            if (coincide && (minPrecio != null || maxPrecio != null)) {
                if (p instanceof model.PublicacionSubasta) {
                    double precio = ((model.PublicacionSubasta) p).getPrecioMinimo();

                    if (minPrecio != null && precio < minPrecio) {
                        coincide = false;
                    }
                    if (maxPrecio != null && precio > maxPrecio) {
                        coincide = false;
                    }
                }
            }

            // 4. Filtro por Categoría
            if (coincide && categoria != null && !categoria.equals("TODAS")) {
                if (p.getCategoria() == null || !p.getCategoria().equals(categoria)) {
                    coincide = false;
                }
            }

            // 5. Filtro por Condición
            if (coincide && condicion != null) {
                if (p.getCondicion() == null || !p.getCondicion().equals(condicion)) {
                    coincide = false;
                }
            }

            if (coincide) {
                filtradas.add(p);
            }
        }
        return filtradas;
    }

    /**
     * Normaliza un texto para búsqueda: minúsculas, trim y sin acentos.
     */
    private String normalizarTexto(String input) {
        if (input == null)
            return "";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", ""); // Quita marcas diacríticas (tildes)
        return normalized.toLowerCase().trim();
    }

    public UserService getUserService() {
        return userService;
    }
}
