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
    public List<Publicacion> recomendarTrueques(String idPublicacion) {
        Publicacion publicacion = publicacionRepository.buscarPorIdArticulo(idPublicacion);
        if (publicacion == null || publicacion.getTipoPublicacion() != TipoPublicacion.TRUEQUE) {
            return java.util.Collections.emptyList();
        }

        PublicacionTrueque trueque = (PublicacionTrueque) publicacion;
        String deseos = trueque.getObjetosDeseados().toLowerCase();

        // Búsqueda simple: publicaciones activas cuyo título contenga el texto de
        // deseos
        List<Publicacion> activas = publicacionRepository.buscarPublicacionesActivas();
        List<Publicacion> recomendaciones = new java.util.ArrayList<>();

        for (Publicacion p : activas) {
            if (p.getIdArticulo().equals(idPublicacion)) {
                continue; // No recomendarse a sí misma
            }
            if (deseos.contains(p.getTitulo().toLowerCase())) {
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
}
