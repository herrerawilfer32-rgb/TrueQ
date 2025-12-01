package model.chat;

import java.time.LocalDateTime;
import model.User;

/**
 * Representa un mensaje enviado dentro de un chat entre dos usuarios.
 */
public class Mensaje implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos principales
    private final String identificadorMensaje;
    private final User usuarioRemitente;
    private final String contenidoMensaje;
    private final LocalDateTime fechaHoraEnvio;
    private java.util.List<String> imagesPaths; // Rutas de imágenes adjuntas (opcional)

    /**
     * Constructor principal de la clase Mensaje.
     *
     * @param identificadorMensaje Identificador único del mensaje.
     * @param usuarioRemitente     Usuario que envía el mensaje.
     * @param contenidoMensaje     Contenido textual del mensaje.
     * @param fechaHoraEnvio       Fecha y hora en la que se envía el mensaje.
     */
    public Mensaje(String identificadorMensaje, User usuarioRemitente, String contenidoMensaje,
            LocalDateTime fechaHoraEnvio) {

        // Validaciones mínimas
        if (identificadorMensaje == null || identificadorMensaje.isBlank()) {
            throw new IllegalArgumentException("El identificador del mensaje no puede ser nulo ni vacío.");
        }
        if (usuarioRemitente == null) {
            throw new IllegalArgumentException("El usuario remitente no puede ser nulo.");
        }
        if (contenidoMensaje == null || contenidoMensaje.isBlank()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede ser nulo ni vacío.");
        }
        if (fechaHoraEnvio == null) {
            throw new IllegalArgumentException("La fecha y hora de envío no pueden ser nulas.");
        }

        this.identificadorMensaje = identificadorMensaje.trim();
        this.usuarioRemitente = usuarioRemitente;
        this.contenidoMensaje = contenidoMensaje.trim();
        this.fechaHoraEnvio = fechaHoraEnvio;
        this.imagesPaths = null; // Por defecto sin imágenes
    }

    // Getters

    public String getIdentificadorMensaje() {
        return identificadorMensaje;
    }

    public User getUsuarioRemitente() {
        return usuarioRemitente;
    }

    public String getContenidoMensaje() {
        return contenidoMensaje;
    }

    public LocalDateTime getFechaHoraEnvio() {
        return fechaHoraEnvio;
    }

    public java.util.List<String> getImagesPaths() {
        return imagesPaths;
    }

    public void setImagesPaths(java.util.List<String> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    public boolean tieneImagenes() {
        return imagesPaths != null && !imagesPaths.isEmpty();
    }
}
