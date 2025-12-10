/**
 * Clase: ConfiguracionGlobal
 * Modelo de mensajes
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.3
 */

package model.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.User;

/**
 * Representa un mensaje enviado dentro de un chat entre dos usuarios.
 */
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Tipo de mensaje, para distinguir mensajes normales de los que
     * muestran botones especiales en la interfaz (pagar, sí/no, etc.).
     */
    public enum TipoMensaje {
        NORMAL,
        BOTON_PAGAR_SUBASTA,
        BOTON_CONFIRMAR_TRUEQUE,
        MENSAJE_ADMIN // Mensajes enviados por administradores
    }

    // Atributos principales
    private final String identificadorMensaje;
    private final User usuarioRemitente;
    private final String contenidoMensaje;
    private final LocalDateTime fechaHoraEnvio;

    // Tipo de mensaje
    private final TipoMensaje tipoMensaje;

    // Id de la publicación asociada (opcional, para subasta/trueque)
    private final String idPublicacionAsociada;

    // Lista de imágenes adjuntas (opcional)
    private List<String> imagesPaths;

    /**
     * Constructor "clásico" → mensaje NORMAL, sin publicación asociada.
     */
    public Mensaje(
            String identificadorMensaje,
            User usuarioRemitente,
            String contenidoMensaje,
            LocalDateTime fechaHoraEnvio) {
        this(identificadorMensaje, usuarioRemitente, contenidoMensaje, fechaHoraEnvio,
                TipoMensaje.NORMAL, null);
    }

    /**
     * Constructor extendido, permitiendo indicar tipo de mensaje y publicación
     * asociada.
     */
    public Mensaje(
            String identificadorMensaje,
            User usuarioRemitente,
            String contenidoMensaje,
            LocalDateTime fechaHoraEnvio,
            TipoMensaje tipoMensaje,
            String idPublicacionAsociada) {

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
        if (tipoMensaje == null) {
            throw new IllegalArgumentException("El tipo de mensaje no puede ser nulo.");
        }

        this.identificadorMensaje = identificadorMensaje.trim();
        this.usuarioRemitente = usuarioRemitente;
        this.contenidoMensaje = contenidoMensaje.trim();
        this.fechaHoraEnvio = fechaHoraEnvio;
        this.tipoMensaje = tipoMensaje;
        this.idPublicacionAsociada = (idPublicacionAsociada != null && !idPublicacionAsociada.isBlank())
                ? idPublicacionAsociada.trim()
                : null;
        this.imagesPaths = new ArrayList<>();
    }

    // Métodos de negocio

    public void agregarImagen(String rutaImagen) {
        if (rutaImagen != null && !rutaImagen.isBlank()) {
            imagesPaths.add(rutaImagen.trim());
        }
    }

    public void setImagesPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            this.imagesPaths = new ArrayList<>();
        } else {
            this.imagesPaths = new ArrayList<>(paths);
        }
    }

    public List<String> getImagesPaths() {
        return Collections.unmodifiableList(imagesPaths);
    }

    public boolean tieneImagenes() {
        return imagesPaths != null && !imagesPaths.isEmpty();
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaHoraEnvio.format(formatter);
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

    public TipoMensaje getTipoMensaje() {
        return tipoMensaje;
    }

    public String getIdPublicacionAsociada() {
        return idPublicacionAsociada;
    }
}
