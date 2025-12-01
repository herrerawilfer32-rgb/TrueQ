package persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.User;
import model.chat.Chat;

/**
 * Implementación del repositorio de chats utilizando serialización de objetos
 * en archivo.
 */
public class ChatFileRepository implements ChatRepository {

    private static final String RUTA_ARCHIVO = "chats.dat";
    private List<Chat> listaChats;

    /**
     * Constructor principal. Carga los chats desde el archivo.
     */
    public ChatFileRepository() {
        this.listaChats = new ArrayList<>();
        cargarChats();
    }

    @SuppressWarnings("unchecked")
    private void cargarChats() {
        File archivo = new File(RUTA_ARCHIVO);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                Object objeto = ois.readObject();
                if (objeto instanceof List<?>) {
                    this.listaChats = (List<Chat>) objeto;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                // Si hay error, iniciamos con lista vacía
                this.listaChats = new ArrayList<>();
            }
        }
    }

    private void guardarChatsEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(this.listaChats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void guardarChat(Chat chat) {
        if (chat == null) {
            return;
        }

        // Verificar si ya existe un chat con el mismo identificador
        Chat chatExistente = buscarChatPorIdentificador(chat.getIdentificadorChat());

        if (chatExistente == null) {
            listaChats.add(chat);
        } else {
            // Si ya existe, actualizamos la referencia en la lista (aunque al ser objetos
            // mutables en memoria,
            // a veces no es necesario, pero para asegurar consistencia en persistencia lo
            // hacemos explícito)
            int index = listaChats.indexOf(chatExistente);
            if (index != -1) {
                listaChats.set(index, chat);
            }
        }

        guardarChatsEnArchivo();
    }

    @Override
    public synchronized Chat buscarChatEntreUsuarios(User usuarioA, User usuarioB) {
        if (usuarioA == null || usuarioB == null) {
            return null;
        }

        for (Chat chat : listaChats) {
            boolean coincideMismaPareja = (chat.getUsuarioEmisor().equals(usuarioA)
                    && chat.getUsuarioReceptor().equals(usuarioB))
                    || (chat.getUsuarioEmisor().equals(usuarioB) && chat.getUsuarioReceptor().equals(usuarioA));
            if (coincideMismaPareja) {
                return chat;
            }
        }

        return null;
    }

    @Override
    public synchronized List<Chat> listarChatsDeUsuario(User usuario) {
        List<Chat> resultado = new ArrayList<>();

        if (usuario == null) {
            return resultado;
        }

        for (Chat chat : listaChats) {
            boolean usuarioParticipa = chat.getUsuarioEmisor().equals(usuario)
                    || chat.getUsuarioReceptor().equals(usuario);
            if (usuarioParticipa) {
                resultado.add(chat);
            }
        }

        return resultado;
    }

    /**
     * Método privado de apoyo para buscar un chat por su identificador.
     *
     * @param identificadorChat Identificador único del chat.
     * @return Chat encontrado o null si no existe.
     */
    private Chat buscarChatPorIdentificador(String identificadorChat) {
        if (identificadorChat == null || identificadorChat.isBlank()) {
            return null;
        }

        for (Chat chat : listaChats) {
            if (chat.getIdentificadorChat().equals(identificadorChat)) {
                return chat;
            }
        }
        return null;
    }

    /**
     * Retorna una copia de todos los chats.
     * Útil para depuración o funciones administrativas.
     */
    public List<Chat> obtenerTodos() {
        return new ArrayList<>(listaChats);
    }
}
