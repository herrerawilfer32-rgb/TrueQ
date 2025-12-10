/**
 * Clase: ChatRepository
 * Repositorio de persistencia.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package persistence;

import java.util.List;

import model.User;
import model.chat.Chat;

/**
 * Define las operaciones de persistencia para los chats de la plataforma.
 * 
 * La implementación concreta puede usar archivos, bases de datos o 
 * estructuras en memoria. Esta interfaz permite desacoplar la lógica 
 * de negocio de la forma en que se almacenan los datos.
 */
public interface ChatRepository {
    
    /**
     * Guarda o actualiza un chat en el medio de persistencia.
     *
     * @param chat Chat a guardar.
     */
    void guardarChat(Chat chat);
    
    /**
     * Busca un chat existente entre dos usuarios específicos.
     *
     * @param usuarioA Primer usuario participante.
     * @param usuarioB Segundo usuario participante.
     * @return Chat encontrado o null si no existe.
     */
    Chat buscarChatEntreUsuarios(User usuarioA, User usuarioB);
    
    /**
     * Lista todos los chats asociados a un usuario.
     *
     * @param usuario Usuario del cual se quieren obtener los chats.
     * @return Lista de chats en los que participa el usuario.
     */
    List<Chat> listarChatsDeUsuario(User usuario);
}

