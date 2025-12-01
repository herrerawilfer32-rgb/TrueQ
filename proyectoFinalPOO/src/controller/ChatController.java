package controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import model.User;
import model.chat.Chat;
import model.chat.Mensaje;
import persistence.ChatRepository;

/**
 * Controlador responsable de gestionar la lógica de negocio
 * relacionada con el sistema de chat y mensajería entre usuarios.
 */
public class ChatController {

    // Dependencia al repositorio de persistencia
    private final ChatRepository chatRepository;
    private final persistence.OfertaRepository ofertaRepository;
    private final persistence.PublicacionRepository publicacionRepository;

    /**
     * Constructor principal del controlador de chat.
     *
     * @param chatRepository        Repositorio a utilizar para almacenar y
     *                              recuperar chats.
     * @param ofertaRepository      Repositorio de ofertas.
     * @param publicacionRepository Repositorio de publicaciones.
     */
    public ChatController(ChatRepository chatRepository,
            persistence.OfertaRepository ofertaRepository,
            persistence.PublicacionRepository publicacionRepository) {
        if (chatRepository == null || ofertaRepository == null || publicacionRepository == null) {
            throw new IllegalArgumentException("Los repositorios no pueden ser nulos.");
        }
        this.chatRepository = chatRepository;
        this.ofertaRepository = ofertaRepository;
        this.publicacionRepository = publicacionRepository;
    }

    /**
     * Obtiene un chat existente entre dos usuarios o crea uno nuevo si no existe.
     *
     * @param usuarioA Primer usuario participante.
     * @param usuarioB Segundo usuario participante.
     * @return Chat existente o uno nuevo si no se encontró.
     */
    public Chat obtenerOCrearChat(User usuarioA, User usuarioB) {
        validarUsuarios(usuarioA, usuarioB);

        Chat chatExistente = chatRepository.buscarChatEntreUsuarios(usuarioA, usuarioB);
        if (chatExistente != null) {
            return chatExistente;
        }

        String identificadorChat = UUID.randomUUID().toString();
        Chat nuevoChat = new Chat(identificadorChat, usuarioA, usuarioB);
        chatRepository.guardarChat(nuevoChat);
        return nuevoChat;
    }

    /**
     * Envía un nuevo mensaje dentro de un chat existente.
     *
     * @param chat             Chat en el cual se envía el mensaje.
     * @param remitente        Usuario que envía el mensaje.
     * @param contenidoMensaje Contenido textual del mensaje.
     */
    public void enviarMensaje(Chat chat, User remitente, String contenidoMensaje) {
        if (chat == null) {
            throw new IllegalArgumentException("El chat no puede ser nulo.");
        }
        if (remitente == null) {
            throw new IllegalArgumentException("El remitente no puede ser nulo.");
        }
        if (contenidoMensaje == null || contenidoMensaje.isBlank()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede ser nulo ni vacío.");
        }

        String identificadorMensaje = UUID.randomUUID().toString();
        Mensaje mensaje = new Mensaje(
                identificadorMensaje,
                remitente,
                contenidoMensaje.trim(),
                LocalDateTime.now());

        chat.agregarMensaje(mensaje);
        chatRepository.guardarChat(chat);
    }

    /**
     * Lista todos los chats en los que participa un usuario.
     *
     * @param usuario Usuario del cual se desean obtener los chats.
     * @return Lista de chats asociados al usuario.
     */
    public List<Chat> listarChatsDeUsuario(User usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        return chatRepository.listarChatsDeUsuario(usuario);
    }

    /**
     * Retorna la lista de mensajes del chat.
     */
    public List<Mensaje> obtenerMensajes(Chat chat) {
        if (chat == null) {
            throw new IllegalArgumentException("El chat no puede ser nulo.");
        }
        return chat.getListaMensajes();
    }

    /**
     * Marca un chat como leído.
     */
    public void marcarChatComoLeido(Chat chat) {
        if (chat == null)
            return;
        chat.marcarMensajesComoLeidos();
        chatRepository.guardarChat(chat);
    }

    /**
     * Obtiene las ofertas relacionadas entre los dos usuarios del chat.
     * Busca ofertas donde:
     * 1. El emisor del chat es el ofertante y el receptor es el dueño de la
     * publicación.
     * 2. El receptor del chat es el ofertante y el emisor es el dueño de la
     * publicación.
     */
    public List<model.Oferta> obtenerOfertasRelacionadas(Chat chat) {
        if (chat == null) {
            return new ArrayList<>();
        }

        User u1 = chat.getUsuarioEmisor();
        User u2 = chat.getUsuarioReceptor();

        List<model.Oferta> todasLasOfertas = new ArrayList<>();

        // Ofertas de U1
        List<model.Oferta> ofertasU1 = ofertaRepository.buscarOfertasPorOfertante(u1.getId());
        for (model.Oferta o : ofertasU1) {
            model.Publicacion p = publicacionRepository.buscarPorIdArticulo(o.getIdPublicacion());
            if (p != null && p.getIdVendedor().equals(u2.getId())) {
                todasLasOfertas.add(o);
            }
        }

        // Ofertas de U2
        List<model.Oferta> ofertasU2 = ofertaRepository.buscarOfertasPorOfertante(u2.getId());
        for (model.Oferta o : ofertasU2) {
            model.Publicacion p = publicacionRepository.buscarPorIdArticulo(o.getIdPublicacion());
            if (p != null && p.getIdVendedor().equals(u1.getId())) {
                todasLasOfertas.add(o);
            }
        }

        return todasLasOfertas;
    }

    /**
     * Obtiene todos los chats de un usuario.
     */
    public List<Chat> obtenerChatsDeUsuario(User usuario) {
        return listarChatsDeUsuario(usuario);
    }

    // Métodos privados de validación

    private void validarUsuarios(User usuarioA, User usuarioB) {
        if (usuarioA == null || usuarioB == null) {
            throw new IllegalArgumentException("Los usuarios no pueden ser nulos.");
        }
        if (usuarioA.equals(usuarioB)) {
            throw new IllegalArgumentException("No se puede crear un chat entre el mismo usuario.");
        }
    }
}
