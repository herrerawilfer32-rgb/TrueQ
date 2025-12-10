/*
 * Clase: Chat
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Modelo de datos.
 */

package model.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.User;

/**
 * Representa una conversación (chat) entre dos usuarios dentro de la
 * plataforma.
 * Contiene la lista de mensajes intercambiados y banderas de estado
 * como la existencia de mensajes no leídos.
 */
public class Chat implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos principales
    private String identificadorChat;
    private User usuarioEmisor;
    private User usuarioReceptor;
    private List<Mensaje> listaMensajes;
    private boolean tieneMensajesNoLeidos;

    /**
     * Constructor principal de la clase Chat.
     *
     * @param identificadorChat Identificador único del chat.
     * @param usuarioEmisor     Usuario que inicia o crea el chat.
     * @param usuarioReceptor   Usuario con quien se establece la conversación.
     */
    public Chat(String identificadorChat, User usuarioEmisor, User usuarioReceptor) {

        if (identificadorChat == null || identificadorChat.isBlank()) {
            throw new IllegalArgumentException("El identificador del chat no puede ser nulo ni vacío.");
        }
        if (usuarioEmisor == null || usuarioReceptor == null) {
            throw new IllegalArgumentException("Los usuarios del chat no pueden ser nulos.");
        }
        if (usuarioEmisor.equals(usuarioReceptor)) {
            throw new IllegalArgumentException("Un chat debe ser entre dos usuarios diferentes.");
        }

        this.identificadorChat = identificadorChat.trim();
        this.usuarioEmisor = usuarioEmisor;
        this.usuarioReceptor = usuarioReceptor;
        this.listaMensajes = new ArrayList<>();
        this.tieneMensajesNoLeidos = false;
    }

 
    // MÉTODOS DE NEGOCIO
    

    /**
     * Agrega un nuevo mensaje al chat.
     * Marca el chat como que tiene mensajes no leídos.
     *
     * @param mensaje Mensaje a agregar al chat.
     */
    public void agregarMensaje(Mensaje mensaje) {
        if (mensaje == null)
            return;
        this.listaMensajes.add(mensaje);
        this.tieneMensajesNoLeidos = true;
    }

    /**
     * Marca el chat como leído, reseteando la bandera de mensajes no leídos.
     */
    public void marcarMensajesComoLeidos() {
        this.tieneMensajesNoLeidos = false;
    }

    /**
     * Retorna el último mensaje del chat (o null si no hay mensajes).
     */
    public Mensaje getUltimoMensaje() {
        if (listaMensajes.isEmpty()) {
            return null;
        }
        return listaMensajes.get(listaMensajes.size() - 1);
    }

    /**
     * Retorna el nombre del usuario con quien se conversa.
     * Útil para mostrar en listas de chats.
     */
    public String getNombreOtroUsuario(User usuarioActual) {
        User otro = obtenerOtroUsuario(usuarioActual);
        return (otro != null) ? otro.getNombre() : "Desconocido";
    }

    /**
     * Dado un usuario, retorna la contraparte del chat.
     * Si el usuario no pertenece al chat, retorna null.
     */
    public User obtenerOtroUsuario(User usuarioActual) {
        if (usuarioActual == null)
            return null;

        if (usuarioActual.equals(usuarioEmisor)) {
            return usuarioReceptor;
        }
        if (usuarioActual.equals(usuarioReceptor)) {
            return usuarioEmisor;
        }
        return null; // No pertenece al chat
    }

    /**
     * Obtiene una copia inmutable de la lista de mensajes del chat.
     *
     * @return Lista de mensajes (no modificable externamente).
     */
    public List<Mensaje> getListaMensajes() {
        return Collections.unmodifiableList(listaMensajes);
    }


    // GETTERS
   

    public String getIdentificadorChat() {
        return identificadorChat;
    }

    public User getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public User getUsuarioReceptor() {
        return usuarioReceptor;
    }

    public boolean isTieneMensajesNoLeidos() {
        return tieneMensajesNoLeidos;
    }

    public void marcarComoLeido() {
        this.tieneMensajesNoLeidos = false;
    }

    public boolean tieneMensajesNoLeidos() {
        return this.tieneMensajesNoLeidos;
    }

    
    /*
     * Devuelve una vista previa (preview) del último mensaje,
     * útil para mostrar en la lista de chats.
     */
    public String getPreviewUltimoMensaje() {
        Mensaje m = getUltimoMensaje();
        if (m == null)
            return "(Sin mensajes)";
        String contenido = m.getContenidoMensaje();
        return contenido.length() > 30 ? contenido.substring(0, 30) + "..." : contenido;
    }
}
