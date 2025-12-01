package controller;

import model.Publicacion;
import model.PublicacionSubasta;
import model.PublicacionTrueque;
import service.PublicacionService;
import service.OfertaService;
import model.User;
import model.Oferta;

import java.util.Date;
import java.util.List;

public class PublicacionController {

    private final PublicacionService publicacionService;
    private final OfertaService ofertaService;
    private final ChatController chatController;

    public PublicacionController(PublicacionService publicacionService, OfertaService ofertaService,
            ChatController chatController) {
        this.publicacionService = publicacionService;
        this.ofertaService = ofertaService;
        this.chatController = chatController;
    }

    public List<Publicacion> obtenerPublicacionesActivas() {
        return publicacionService.buscarPublicacionesActivas();
    }

    public List<Publicacion> obtenerPublicacionesPorVendedor(String idVendedor) {
        return publicacionService.obtenerPublicacionesPorVendedor(idVendedor);
    }

    public User obtenerVendedor(String idPublicacion) {
        return publicacionService.obtenerVendedorDePublicacion(idPublicacion);
    }

    public boolean crearSubasta(String titulo, String descripcion, User vendedor, double precioMinimo,
            int diasDuracion, List<String> fotosPaths) {
        try {
            Date fechaVencimiento = new Date(System.currentTimeMillis() + (diasDuracion * 86400000L));

            PublicacionSubasta subasta = new PublicacionSubasta(
                    generarId(), titulo, descripcion, vendedor.getId(), fotosPaths, precioMinimo, fechaVencimiento,
                    fechaVencimiento);

            publicacionService.guardarPublicacion(subasta);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean crearTrueque(String titulo, String descripcion, User vendedor, String objetosDeseados,
            List<String> fotosPaths) {
        try {
            PublicacionTrueque trueque = new PublicacionTrueque(
                    generarId(), titulo, descripcion, vendedor.getId(), fotosPaths, objetosDeseados);

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
                    model.chat.Chat chat = chatController.obtenerOCrearChat(ofertante, vendedor);

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

                    // Enviar mensaje
                    chatController.enviarMensaje(chat, ofertante, mensaje.toString());
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Mostrar solo el mensaje de error, no el nombre de la excepción
            String mensajeError = e.getMessage() != null ? e.getMessage() : "Error desconocido al ofertar";
            javax.swing.JOptionPane.showMessageDialog(null, mensajeError, "Error al ofertar",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Oferta> obtenerOfertas(String idPublicacion) {
        return ofertaService.obtenerOfertasPorPublicacion(idPublicacion);
    }

    public boolean aceptarOferta(String idOferta, String idVendedor) {
        try {
            return ofertaService.aceptarOferta(idOferta, idVendedor);
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

    public void cerrarSubasta(String idPublicacion, String idVendedor) {
        try {
            publicacionService.cerrarSubasta(idPublicacion, idVendedor);
            javax.swing.JOptionPane.showMessageDialog(null, "Subasta cerrada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error al cerrar subasta: " + e.getMessage());
        }
    }

    private String generarId() {
        return "PUB-" + System.currentTimeMillis();
    }

    private String generarIdOferta() {
        return "OFE-" + System.currentTimeMillis();
    }
}