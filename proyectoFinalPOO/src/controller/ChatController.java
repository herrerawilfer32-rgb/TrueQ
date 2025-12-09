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
     * Envía un nuevo mensaje NORMAL dentro de un chat existente.
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
     * Envía un mensaje indicando explícitamente el tipo (NORMAL, BOTON_PAGAR_SUBASTA, BOTON_CONFIRMAR_TRUEQUE),
     * sin publicación asociada.
     */
    public void enviarMensaje(Chat chat, User remitente, String contenidoMensaje, Mensaje.TipoMensaje tipoMensaje) {
        enviarMensaje(chat, remitente, contenidoMensaje, tipoMensaje, null);
    }

    /**
     * Envía un mensaje indicando tipo y id de publicación asociada (para subastas/trueques).
     */
    public void enviarMensaje(Chat chat, User remitente, String contenidoMensaje,
                              Mensaje.TipoMensaje tipoMensaje,
                              String idPublicacionAsociada) {
        if (chat == null) {
            throw new IllegalArgumentException("El chat no puede ser nulo.");
        }
        if (remitente == null) {
            throw new IllegalArgumentException("El remitente no puede ser nulo.");
        }
        if (contenidoMensaje == null || contenidoMensaje.isBlank()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede ser nulo ni vacío.");
        }
        if (tipoMensaje == null) {
            throw new IllegalArgumentException("El tipo de mensaje no puede ser nulo.");
        }

        String identificadorMensaje = UUID.randomUUID().toString();
        Mensaje mensaje = new Mensaje(
                identificadorMensaje,
                remitente,
                contenidoMensaje.trim(),
                LocalDateTime.now(),
                tipoMensaje,
                idPublicacionAsociada);

        chat.agregarMensaje(mensaje);
        chatRepository.guardarChat(chat);
    }

    public List<Chat> listarChatsDeUsuario(User usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        return chatRepository.listarChatsDeUsuario(usuario);
    }

    public List<Mensaje> obtenerMensajes(Chat chat) {
        if (chat == null) {
            throw new IllegalArgumentException("El chat no puede ser nulo.");
        }
        return chat.getListaMensajes();
    }

    public void marcarChatComoLeido(Chat chat) {
        if (chat == null)
            return;
        chat.marcarMensajesComoLeidos();
        chatRepository.guardarChat(chat);
    }

    public List<model.Oferta> obtenerOfertasRelacionadas(Chat chat) {
        if (chat == null) {
            return new ArrayList<>();
        }

        User u1 = chat.getUsuarioEmisor();
        User u2 = chat.getUsuarioReceptor();

        List<model.Oferta> todasLasOfertas = new ArrayList<>();

        List<model.Oferta> ofertasU1 = ofertaRepository.buscarOfertasPorOfertante(u1.getId());
        for (model.Oferta o : ofertasU1) {
            model.Publicacion p = publicacionRepository.buscarPorIdArticulo(o.getIdPublicacion());
            if (p != null && p.getIdVendedor().equals(u2.getId())) {
                todasLasOfertas.add(o);
            }
        }

        List<model.Oferta> ofertasU2 = ofertaRepository.buscarOfertasPorOfertante(u2.getId());
        for (model.Oferta o : ofertasU2) {
            model.Publicacion p = publicacionRepository.buscarPorIdArticulo(o.getIdPublicacion());
            if (p != null && p.getIdVendedor().equals(u1.getId())) {
                todasLasOfertas.add(o);
            }
        }

        return todasLasOfertas;
    }

    public List<Chat> obtenerChatsDeUsuario(User usuario) {
        return listarChatsDeUsuario(usuario);
    }

    private void validarUsuarios(User usuarioA, User usuarioB) {
        if (usuarioA == null || usuarioB == null) {
            throw new IllegalArgumentException("Los usuarios no pueden ser nulos.");
        }
        if (usuarioA.equals(usuarioB)) {
            throw new IllegalArgumentException("No se puede crear un chat entre el mismo usuario.");
        }
    }
}
