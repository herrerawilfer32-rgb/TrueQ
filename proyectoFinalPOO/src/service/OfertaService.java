package service;

import java.util.Date;
import java.util.List;

import persistence.OfertaRepository;
import model.Oferta;
import model.Publicacion;
import model.PublicacionSubasta;
import model.PublicacionTrueque;
import model.User;
import util.EstadoOferta;
import util.TipoPublicacion;

public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UserService userService;
    private final PublicacionService publicacionService;

    public OfertaService(OfertaRepository ofertaRepository, UserService userService,
                         PublicacionService publicacionService) {
        this.ofertaRepository = ofertaRepository;
        this.userService = userService;
        this.publicacionService = publicacionService;
    }

    public List<Oferta> obtenerOfertasPorPublicacion(String idPublicacion) {
        return ofertaRepository.buscarPorPublicacion(idPublicacion);
    }

    /**
     * Registra una nueva oferta sobre una publicación.
     *
     * - Si la publicación es SUBASTA (PublicacionSubasta) se valida:
     *   monto > 0, subasta vigente, monto superior a la mejor oferta o al precio mínimo.
     *
     * - Si la publicación es TRUEQUE (PublicacionTrueque) se valida:
     *   descripción no vacía.
     *
     * Se usan instanceof en lugar de castear a ciegas para evitar ClassCastException
     * cuando el tipo guardado en archivos está inconsistente.
     */
    public Oferta realizarNuevaOferta(String idOferta,
                                      String idPublicacion,
                                      String idOfertante,
                                      Date fechaOferta,
                                      double montoOferta,
                                      String descripcionTrueque,
                                      List<String> rutasImagenes) {

        // 1. Validar que el ofertante exista
        User ofertante = userService.buscarUsuarioPorId(idOfertante);
        if (ofertante == null) {
            throw new IllegalArgumentException("El ofertante no existe.");
        }

        // 2. Validar que la publicación exista
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
        if (publicacion == null) {
            throw new IllegalArgumentException("La publicación no existe.");
        }

        // 3. Validar que la publicación esté activa
        if (publicacion.getEstado() != util.EstadoPublicacion.ACTIVA) {
            throw new IllegalArgumentException("La publicación no está activa.");
        }

        // -----------------------------------------------------------------
        // Diferenciar entre SUBASTA y TRUEQUE usando la CLASE REAL del obj.
        // -----------------------------------------------------------------

        if (publicacion instanceof PublicacionSubasta) {
            // ----------- LÓGICA PARA SUBASTAS -----------
            PublicacionSubasta subasta = (PublicacionSubasta) publicacion;

            // (opcional) chequeo de coherencia con el enum
            if (publicacion.getTipoPublicacion() != TipoPublicacion.SUBASTA) {
                System.out.println("[ADVERTENCIA] PublicacionSubasta con tipo != SUBASTA en enum.");
            }

            // Validar fecha de vencimiento
            if (new Date().after(subasta.getFechaCierre())) {
                throw new IllegalArgumentException("La subasta ha finalizado.");
            }

            // Tiene que haber un monto sí o sí
            if (montoOferta <= 0) {
                throw new IllegalArgumentException("En una subasta el monto debe ser positivo.");
            }

            // Validar que sea un monto superior a la mayor oferta actual (o precio mínimo)
            List<Oferta> ofertasPorPublicacion = ofertaRepository.buscarPorPublicacion(idPublicacion);
            double mayorOferta = subasta.getPrecioMinimo(); // Empezamos con el precio mínimo

            for (Oferta oferta : ofertasPorPublicacion) {
                if (oferta.getMontoOferta() > mayorOferta) {
                    mayorOferta = oferta.getMontoOferta();
                }
            }

            if (montoOferta <= mayorOferta) {
                throw new IllegalArgumentException(
                        "El monto debe ser superior a la mayor oferta actual (o precio mínimo) de $" + mayorOferta);
            }

        } else if (publicacion instanceof PublicacionTrueque) {
            // ----------- LÓGICA PARA TRUEQUES -----------
            PublicacionTrueque trueque = (PublicacionTrueque) publicacion;

            // (opcional) chequeo de coherencia con el enum
            if (publicacion.getTipoPublicacion() != TipoPublicacion.TRUEQUE) {
                System.out.println("[ADVERTENCIA] PublicacionTrueque con tipo != TRUEQUE en enum.");
            }

            // Tiene que haber una descripción del trueque
            if (descripcionTrueque == null || descripcionTrueque.isBlank()) {
                throw new IllegalArgumentException(
                        "En un trueque la descripción del trueque no puede estar vacía.");
            }

        } else {
            // Algún tipo raro/inconsistente
            throw new IllegalStateException(
                    "Tipo de publicación no soportado para ofertas: "
                            + publicacion.getClass().getSimpleName());
        }

        // 4. Crear la nueva oferta
        Oferta nuevaOferta = new Oferta(
                idOferta,
                idPublicacion,
                idOfertante,
                new java.util.Date(),
                montoOferta,
                descripcionTrueque,
                EstadoOferta.PENDIENTE
        );

        if (rutasImagenes != null) {
            nuevaOferta.setRutasImagenes(rutasImagenes);
        }

        // 5. Guardar la oferta en el repositorio
        ofertaRepository.guardar(nuevaOferta);

        System.out.println("Oferta realizada exitosamente por el usuario "
                + ofertante.getNombre() + " en la publicación " + publicacion.getTitulo() + ".");
        return nuevaOferta;
    }

    // Método para aceptar una oferta
    public boolean aceptarOferta(String idOferta, String idVendedorQueAcepta) {
        // Buscar la oferta
        Oferta oferta = ofertaRepository.buscarPorIdOferta(idOferta);
        // 1. Validar que la oferta exista.
        if (oferta == null) {
            throw new IllegalArgumentException("La oferta no existe.");
        }

        // 2. Buscar la publicación asociada a la oferta.
        Publicacion publicacion = publicacionService.buscarPublicacionPorId(oferta.getIdPublicacion());

        // 3. Validamos que quien intenta aceptar la oferta sea el dueño de la publicación
        if (!publicacion.getIdVendedor().equals(idVendedorQueAcepta)) {
            throw new IllegalArgumentException("Solo el dueño de la publicación puede aceptar una oferta.");
        }

        // 4. Validamos que la publicación siga abierta.
        if (publicacion.getEstado() != util.EstadoPublicacion.ACTIVA) {
            throw new IllegalArgumentException("La publicación no está activa.");
        }

        // 5. LÓGICA DE CIERRE

        // A. Cambiamos estado de la oferta a ACEPTADA
        oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
        ofertaRepository.guardar(oferta); // Actualizamos en BD

        // B. Cerramos la publicación
        publicacionService.cerrarPublicacion(publicacion.getIdArticulo());

        System.out.println("¡Felicidades! Oferta aceptada. La publicación ha sido cerrada.");
        return true;
    }

    public boolean rechazarOferta(String idOferta, String idVendedor) {
        Oferta oferta = ofertaRepository.buscarPorIdOferta(idOferta);
        if (oferta == null)
            throw new IllegalArgumentException("La oferta no existe.");

        Publicacion publicacion = publicacionService.buscarPublicacionPorId(oferta.getIdPublicacion());
        if (!publicacion.getIdVendedor().equals(idVendedor)) {
            throw new IllegalArgumentException("Solo el dueño de la publicación puede rechazar ofertas.");
        }

        oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
        ofertaRepository.guardar(oferta);
        return true;
    }

    public boolean eliminarOferta(String idOferta, String idSolicitante) {
        Oferta oferta = ofertaRepository.buscarPorIdOferta(idOferta);
        if (oferta == null)
            throw new IllegalArgumentException("La oferta no existe.");

        Publicacion publicacion = publicacionService.buscarPublicacionPorId(oferta.getIdPublicacion());

        // Permitir eliminar si es el dueño de la publicación O el dueño de la oferta
        if (!publicacion.getIdVendedor().equals(idSolicitante)
                && !oferta.getIdOfertante().equals(idSolicitante)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta oferta.");
        }

        ofertaRepository.eliminar(idOferta);
        return true;
    }
    /**
     * Calcula la puja actual para una subasta:
     * parte del precio mínimo y si hay ofertas, usa la mayor oferta.
     *
     * @param idPublicacion Id de la publicación de subasta.
     * @param precioMinimo  Precio mínimo inicial definido en la subasta.
     * @return Monto actual de la puja.
     */
    public double calcularPujaActualSubasta(String idPublicacion, double precioMinimo) {
        double mayor = precioMinimo;

        List<Oferta> ofertas = obtenerOfertasPorPublicacion(idPublicacion);
        if (ofertas != null) {
            for (Oferta oferta : ofertas) {
                if (oferta.getMontoOferta() > mayor) {
                    mayor = oferta.getMontoOferta();
                }
            }
        }
        return mayor;
    }

    /**
     * Calcula el incremento rápido: 10% del precio mínimo inicial.
     *
     * @param precioMinimo Precio mínimo de la subasta.
     * @return Valor del incremento (no suma, solo el 10%).
     */
    public double calcularIncrementoRapidoSubasta(double precioMinimo) {
        if (precioMinimo <= 0) {
            return 0;
        }
        return precioMinimo * 0.10;
    }
}

