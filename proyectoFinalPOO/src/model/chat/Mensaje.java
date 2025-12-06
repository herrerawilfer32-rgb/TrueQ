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

    
    // Atributos principales
   
    private final String identificadorMensaje;
    private final User usuarioRemitente;
    private final String contenidoMensaje;
    private final LocalDateTime fechaHoraEnvio;

    // Lista de imágenes adjuntas (opcional)
    private List<String> imagesPaths;

    /**
     * Constructor principal de la clase Mensaje.
     *
     * @param identificadorMensaje Identificador único del mensaje.
     * @param usuarioRemitente     Usuario que envía el mensaje.
     * @param contenidoMensaje     Contenido textual del mensaje.
     * @param fechaHoraEnvio       Fecha y hora en la que se envía el mensaje.
     */
    public Mensaje(
            String identificadorMensaje,
            User usuarioRemitente,
            String contenidoMensaje,
            LocalDateTime fechaHoraEnvio) {

        // Validaciones necesarias
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
        this.imagesPaths = new ArrayList<>(); // lista inicial vacía
    }

    
    // Métodos de negocio
    

    /**
     * Agrega una imagen al mensaje.
     */
    public void agregarImagen(String rutaImagen) {
        if (rutaImagen != null && !rutaImagen.isBlank()) {
            imagesPaths.add(rutaImagen.trim());
        }
    }

    /**
     * Permite asignar una lista completa de imágenes sin exponer referencias externas.
     */
    public void setImagesPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            this.imagesPaths = new ArrayList<>();
        } else {
            this.imagesPaths = new ArrayList<>(paths);
        }
    }

    /**
     * Retorna una lista inmutable para evitar modificaciones externas.
     */
    public List<String> getImagesPaths() {
        return Collections.unmodifiableList(imagesPaths);
    }

    /**
     * Indica si el mensaje contiene imágenes adjuntas.
     */
    public boolean tieneImagenes() {
        return imagesPaths != null && !imagesPaths.isEmpty();
    }

    /**
     * Retorna la fecha formateada para mostrar en la interfaz.
     */
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
}
