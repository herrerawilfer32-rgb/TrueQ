/**
 * Clase:PublicacionController
 * Controlador encargado de manejar todas las operaciones relacionadas con las publicaciones (subastas y trueques)
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.0
 */

package controller;

import java.util.Date;
import java.util.List;

import model.Oferta;
import model.Publicacion;
import model.PublicacionSubasta;
import model.PublicacionTrueque;
import model.User;
import model.chat.Chat;
import model.chat.Mensaje;
import service.OfertaService;
import service.PublicacionService;
import controller.UserController;
import view.PerfilUsuarioView;

public class PublicacionController {

    private final PublicacionService publicacionService;
    private final OfertaService ofertaService;
    private final ChatController chatController;
    
    /**
     * Constructor principal del controlador.
     * 
     * @param publicacionService servicio para manejar publicaciones.
     * @param ofertaService      servicio para manejar ofertas.
     * @param chatController     controlador del sistema de chat.
     */
    public PublicacionController(PublicacionService publicacionService, OfertaService ofertaService,
            ChatController chatController) {
        this.publicacionService = publicacionService;
        this.ofertaService = ofertaService;
        this.chatController = chatController;
    }
    
 /**
 *@return lista de todas las publicaciones activas en el sistema.
 */
    public List<Publicacion> obtenerPublicacionesActivas() {
        return publicacionService.buscarPublicacionesActivas();
    }
    
     /**
     * Obtiene todas las publicaciones realizadas por un vendedor específico.
     */
    public List<Publicacion> obtenerPublicacionesPorVendedor(String idVendedor) {
        return publicacionService.obtenerPublicacionesPorVendedor(idVendedor);
    }
     /** Obtiene el usuario/vendedor dueño de una publicación. */
    public User obtenerVendedor(String idPublicacion) {
        return publicacionService.obtenerVendedorDePublicacion(idPublicacion);
    }
    
     /**
     * Crea una nueva publicación tipo Subasta.
     */
    public boolean crearSubasta(String titulo, String descripcion, User vendedor, double precioMinimo,
            int diasDuracion, List<String> fotosPaths, String categoria, util.CondicionArticulo condicion) {
        try {
            Date fechaVencimiento = new Date(System.currentTimeMillis() + (diasDuracion * 86400000L));

            PublicacionSubasta subasta = new PublicacionSubasta(
                    generarId(), titulo, descripcion, vendedor.getId(), fotosPaths, precioMinimo, fechaVencimiento,
                    fechaVencimiento);
            subasta.setCategoria(categoria);
            subasta.setCondicion(condicion);

            publicacionService.guardarPublicacion(subasta);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
     /**
     * Crea una nueva publicación tipo Trueque.
     */
    public boolean crearTrueque(String titulo, String descripcion, User vendedor, String objetosDeseados,
            List<String> fotosPaths, String categoria, util.CondicionArticulo condicion) {
        try {
            PublicacionTrueque trueque = new PublicacionTrueque(
                    generarId(), titulo, descripcion, vendedor.getId(), fotosPaths, objetosDeseados);
            trueque.setCategoria(categoria);
            trueque.setCondicion(condicion);

            publicacionService.guardarPublicacion(trueque);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPublicacion(String idPublicacion, String idUsuarioSolicitante) {
        return publicacionService.eliminarPublicacion(idPublicacion, idUsuarioSolicitante);
    }

    public boolean actualizarPublicacion(Publicacion publicacion, String idUsuarioSolicitante) {
        return publicacionService.actualizarPublicacion(publicacion, idUsuarioSolicitante);
    }

    // --- Métodos para Ofertas ---
    
    /**
     * Realiza una nueva oferta sobre una publicación, también envía un mensaje automático al chat entre ofertante y vendedor.
     */
    public boolean ofertar(String idPublicacion, String idOfertante, double monto, String descripcionTrueque,
            List<String> imagenes) {
        try {
            Oferta nuevaOferta = ofertaService.realizarNuevaOferta(generarIdOferta(), idPublicacion, idOfertante,
                    new Date(), monto,
                    descripcionTrueque, imagenes);

            // Enviar mensaje automático al chat
            if (chatController != null) {
                User vendedor = obtenerVendedor(idPublicacion);
                User ofertante = publicacionService.obtenerUsuarioPorId(idOfertante);

                if (ofertante != null && vendedor != null) {
                    Chat chat = chatController.obtenerOCrearChat(ofertante, vendedor);

                    // Construir mensaje con detalles de la oferta
                    StringBuilder mensaje = new StringBuilder("¡Hola! He realizado una oferta por tu publicación.\n");

                    if (nuevaOferta.getDescripcionTrueque() != null && !nuevaOferta.getDescripcionTrueque().isEmpty()) {
                        mensaje.append("Ofrezco: ").append(nuevaOferta.getDescripcionTrueque()).append("\n");
                    }
                    if (nuevaOferta.getMontoOferta() > 0) {
                        mensaje.append("Monto: $").append(nuevaOferta.getMontoOferta()).append("\n");
                    }
                    if (nuevaOferta.getRutasImagenes() != null && !nuevaOferta.getRutasImagenes().isEmpty()) {
                        mensaje.append("📷 Imágenes adjuntas: ").append(nuevaOferta.getRutasImagenes().size())
                                .append(" (haz clic en 'Ver Ofertas Relacionadas' para verlas)");
                    }

                    // Enviar mensaje normal
                    chatController.enviarMensaje(chat, ofertante, mensaje.toString());
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            String mensajeError = e.getMessage() != null ? e.getMessage() : "Error desconocido al ofertar";
            javax.swing.JOptionPane.showMessageDialog(null, mensajeError, "Error al ofertar",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Oferta> obtenerOfertas(String idPublicacion) {
        return ofertaService.obtenerOfertasPorPublicacion(idPublicacion);
    }

    /**
     * Aceptar oferta:
     * - Usa la lógica existente de OfertaService.
     * - Si la publicación es de TRUEQUE, se envía mensaje al intercambiador
     * elegido:
     * "¡Felicidades, he elegido hacer un trato contigo! ¿deseas continuar con el
     * trato?"
     * con tipo BOTON_CONFIRMAR_TRUEQUE y publicación asociada.
     */
    public boolean aceptarOferta(String idOferta, String idVendedor) {
        try {
            boolean resultado = ofertaService.aceptarOferta(idOferta, idVendedor);

            if (resultado && chatController != null) {
                Oferta ofertaAceptada = ofertaService.obtenerOfertaPorId(idOferta);
                if (ofertaAceptada != null) {
                    Publicacion publicacion = publicacionService
                            .buscarPublicacionPorId(ofertaAceptada.getIdPublicacion());

                    if (publicacion instanceof PublicacionTrueque) {
                        User vendedor = publicacionService.obtenerUsuarioPorId(idVendedor);
                        User ofertanteElegido = publicacionService
                                .obtenerUsuarioPorId(ofertaAceptada.getIdOfertante());

                        if (vendedor != null && ofertanteElegido != null) {
                            Chat chat = chatController.obtenerOCrearChat(ofertanteElegido, vendedor);
                            String mensaje = "¡Felicidades, he elegido hacer un trato contigo! ¿Deseas continuar con el trato?";
                            // MENSAJE ESPECIAL: botón Sí/No + referencia a la publicación
                            chatController.enviarMensaje(
                                    chat,
                                    vendedor,
                                    mensaje,
                                    Mensaje.TipoMensaje.BOTON_CONFIRMAR_TRUEQUE,
                                    publicacion.getIdArticulo());
                        }
                    }
                }
            }

            return resultado;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    public boolean rechazarOferta(String idOferta, String idVendedor) {
        try {
            return ofertaService.rechazarOferta(idOferta, idVendedor);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    public boolean eliminarOferta(String idOferta, String idSolicitante) {
        try {
            return ofertaService.eliminarOferta(idOferta, idSolicitante);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
            return false;
        }
    }

    /**
     * Cerrar subasta:
     * - Determina ganador (mejor oferta) y le envía mensaje:
     * "¡Felicidades, eres el ganador de la subasta!, realiza tu pago aquí ..."
     * con tipo BOTON_PAGAR_SUBASTA y referencia a la publicación.
     * - A cada participante que pujó pero no ganó:
     * "La subasta ha cerrado, en caso de no concretar un trato podrías ser el
     * próximo adjudicatario."
     * - Luego cierra la subasta en el servicio.
     */
    public void cerrarSubasta(String idPublicacion, String idVendedor) {
        try {
            // Validar que la publicación exista y sea una subasta
            Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
            if (publicacion == null) {
                throw new IllegalArgumentException("La publicación no existe.");
            }
            if (!(publicacion instanceof PublicacionSubasta)) {
                throw new IllegalArgumentException("La publicación seleccionada no es una subasta.");
            }
            if (!publicacion.getIdVendedor().equals(idVendedor)) {
                throw new IllegalArgumentException("Solo el dueño de la publicación puede cerrar la subasta.");
            }

            // Obtener la mejor oferta (ganador) si existe
            Oferta mejorOferta = ofertaService.obtenerMejorOfertaSubasta(idPublicacion);

            if (mejorOferta != null && chatController != null) {
                User vendedor = publicacionService.obtenerUsuarioPorId(idVendedor);
                User ganador = publicacionService.obtenerUsuarioPorId(mejorOferta.getIdOfertante());

                // 1) Mensaje al ganador (con botón Pagar)
                if (vendedor != null && ganador != null) {
                    Chat chatGanador = chatController.obtenerOCrearChat(ganador, vendedor);
                    String mensajeGanador = "¡Felicidades, eres el ganador de la subasta! "
                            + "Realiza tu pago aquí desde este chat.";
                    chatController.enviarMensaje(
                            chatGanador,
                            vendedor,
                            mensajeGanador,
                            Mensaje.TipoMensaje.BOTON_PAGAR_SUBASTA,
                            publicacion.getIdArticulo());
                }

                // 2) Mensajes a los que pujarón pero no ganaron (mensaje normal)
                List<Oferta> ofertasNoGanadoras = ofertaService.obtenerOfertasNoGanadorasSubasta(idPublicacion);
                if (ofertasNoGanadoras != null && !ofertasNoGanadoras.isEmpty() && vendedor != null) {
                    for (Oferta oferta : ofertasNoGanadoras) {
                        User pujador = publicacionService.obtenerUsuarioPorId(oferta.getIdOfertante());
                        if (pujador != null) {
                            Chat chatPerdedor = chatController.obtenerOCrearChat(pujador, vendedor);
                            String mensajePerdedor = "La subasta ha cerrado, en caso de no concretar un trato "
                                    + "podrías ser el próximo adjudicatario.";
                            chatController.enviarMensaje(chatPerdedor, vendedor, mensajePerdedor);
                        }
                    }
                }
            }

            // 3) Cerrar subasta normalmente
            publicacionService.cerrarSubasta(idPublicacion, idVendedor);
            javax.swing.JOptionPane.showMessageDialog(null, "Subasta cerrada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cerrar subasta: " + e.getMessage());
        }
    }

     /** Genera un ID único para una publicación. */
    private String generarId() {
        return "PUB-" + System.currentTimeMillis();
    }

     /** Genera un ID único para una oferta. */
    private String generarIdOferta() {
        return "OFE-" + System.currentTimeMillis();
    }
    
    /** Obtiene el valor actual de la puja más alta en una subasta. */
    public double obtenerPujaActualSubasta(PublicacionSubasta subasta) {
        if (subasta == null) {
            throw new IllegalArgumentException("La subasta no puede ser nula.");
        }
        return ofertaService.calcularPujaActualSubasta(
                subasta.getIdArticulo(),
                subasta.getPrecioMinimo());
    }

    public double calcularIncrementoRapidoSubasta(PublicacionSubasta subasta) {
        if (subasta == null) {
            throw new IllegalArgumentException("La subasta no puede ser nula.");
        }
        return ofertaService.calcularIncrementoRapidoSubasta(subasta.getPrecioMinimo());
    }

    public List<Publicacion> recomendarTrueques(String idPublicacion) {
        return publicacionService.recomendarTrueques(idPublicacion);
    }

    // ============================================================
    // NUEVO: CONCRETAR INTERCAMBIO Y FINALIZAR SUBASTA CON PAGO
    // ============================================================

    /**
     * Concreta un intercambio (TRUEQUE):
     * - Solo puede llamarse por el dueño o por el ofertante aceptado.
     * - Elimina la publicación como se haría normalmente.
     * - Notifica a los demás ofertantes:
     * "El intercambio ha sido concretado con otro usuario."
     */
    public void concretarIntercambio(String idPublicacion, String idUsuarioQueConfirma) {
        try {
            Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
            if (publicacion == null) {
                throw new IllegalArgumentException("La publicación no existe.");
            }
            if (!(publicacion instanceof PublicacionTrueque)) {
                throw new IllegalArgumentException("La publicación seleccionada no es un trueque.");
            }

            PublicacionTrueque trueque = (PublicacionTrueque) publicacion;

            Oferta ofertaAceptada = ofertaService.obtenerOfertaAceptadaTrueque(idPublicacion);
            if (ofertaAceptada == null) {
                throw new IllegalStateException("No hay una oferta aceptada para este trueque.");
            }

            boolean esDueno = trueque.getIdVendedor().equals(idUsuarioQueConfirma);
            boolean esOfertanteElegido = ofertaAceptada.getIdOfertante().equals(idUsuarioQueConfirma);
            if (!esDueno && !esOfertanteElegido) {
                throw new IllegalArgumentException(
                        "Solo el dueño o el usuario seleccionado pueden concretar el intercambio.");
            }

            User vendedor = publicacionService.obtenerUsuarioPorId(trueque.getIdVendedor());

            // Notificar a los demás ofertantes que el intercambio se concretó con otro
            // usuario
            List<Oferta> ofertasNoAceptadas = ofertaService.obtenerOfertasNoAceptadasTrueque(idPublicacion);
            if (chatController != null && vendedor != null && ofertasNoAceptadas != null) {
                for (Oferta oferta : ofertasNoAceptadas) {
                    User otroOfertante = publicacionService.obtenerUsuarioPorId(oferta.getIdOfertante());
                    if (otroOfertante != null) {
                        Chat chat = chatController.obtenerOCrearChat(otroOfertante, vendedor);
                        String mensaje = "El intercambio ha sido concretado con otro usuario.";
                        chatController.enviarMensaje(chat, vendedor, mensaje);
                    }
                }
            }

            // Marcar publicación como FINALIZADA
            publicacionService.finalizarPublicacion(idPublicacion);
            javax.swing.JOptionPane.showMessageDialog(null, "Intercambio concretado exitosamente.");

            // LOGICA CALIFICACION
            int calificar = javax.swing.JOptionPane.showConfirmDialog(null,
                    "¿Deseas calificar al usuario con quien intercambiaste?", "Calificar Usuario",
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (calificar == javax.swing.JOptionPane.YES_OPTION) {
                User usuarioACalificar = esDueno
                        ? publicacionService.obtenerUsuarioPorId(ofertaAceptada.getIdOfertante())
                        : vendedor;

                User usuarioCalificador = publicacionService.obtenerUsuarioPorId(idUsuarioQueConfirma);

                if (usuarioACalificar != null) {
                    new view.PerfilUsuarioView(null, usuarioACalificar,
                            new UserController(publicacionService.getUserService()), true, idPublicacion,
                            usuarioCalificador).setVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error al concretar intercambio: " + e.getMessage());
        }
    }
    
    /**
     * Finaliza una subasta luego de que el ganador realiza su pago.
     * - Solo el que la creó puede finalizarla.
     * -Se finaliza la publicación.
     * - Permite calificar al vendedor.
     */
    public void finalizarSubastaConPago(String idPublicacion, String idComprador) {
        try {
            Publicacion publicacion = publicacionService.buscarPublicacionPorId(idPublicacion);
            if (publicacion == null) {
                throw new IllegalArgumentException("La publicación no existe.");
            }
            if (!(publicacion instanceof PublicacionSubasta)) {
                throw new IllegalArgumentException("La publicación seleccionada no es una subasta.");
            }

            Oferta mejorOferta = ofertaService.obtenerMejorOfertaSubasta(idPublicacion);
            if (mejorOferta == null) {
                throw new IllegalStateException("No hay una oferta ganadora para esta subasta.");
            }
            if (!mejorOferta.getIdOfertante().equals(idComprador)) {
                throw new IllegalArgumentException("Solo el adjudicatario puede realizar el pago.");
            }

            javax.swing.JOptionPane.showMessageDialog(null, "¡Felicidades eres el adjudicatario!");

            // Marcar la publicación como FINALIZADA
            publicacionService.finalizarPublicacion(idPublicacion);

            // LOGICA CALIFICACION
            int calificar = javax.swing.JOptionPane.showConfirmDialog(null,
                    "¿Deseas calificar al vendedor?", "Calificar Usuario", javax.swing.JOptionPane.YES_NO_OPTION);

            if (calificar == javax.swing.JOptionPane.YES_OPTION) {
                User vendedor = publicacionService.obtenerUsuarioPorId(publicacion.getIdVendedor());
                User comprador = publicacionService.obtenerUsuarioPorId(idComprador);

                if (vendedor != null) {
                    new view.PerfilUsuarioView(null, vendedor, new UserController(publicacionService.getUserService()),
                            true, idPublicacion, comprador).setVisible(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error al finalizar la subasta: " + e.getMessage());
        }
    }

    public List<model.Publicacion> listarPublicacionesConFiltros(String ciudad, String tipo, Double minPrecio,
            Double maxPrecio, String categoria, util.CondicionArticulo condicion) {
        return publicacionService.listarPublicacionesConFiltros(ciudad, tipo, minPrecio, maxPrecio, categoria,
                condicion);
    }

    public service.UserService getUserService() {
        return publicacionService.getUserService();
    }
}
