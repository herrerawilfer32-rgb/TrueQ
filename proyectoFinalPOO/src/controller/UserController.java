/**
 * Clase: UserController
 * Controlador encargado de gestionar las operaciones relacionadas con los usuarios, tales como obtener información de ellos y calificarlos
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package controller;

import model.User;
import service.UserService;
import javax.swing.JOptionPane;

public class UserController {

    private final UserService userService;
    
     /**
     * Constructor del controlador de usuarios.
     *
     * @param userService Servicio que contiene la lógica de negocio relacionada con usuarios.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

     /**
     * Obtiene un usuario a partir de su identificador único.
     *
     * @param idUsuario ID del usuario buscado.
     * @return El objeto User correspondiente, o null si no existe.
     */
    public User obtenerUsuario(String idUsuario) {
        return userService.buscarUsuarioPorId(idUsuario);
    }

    /**
     * Permite calificar a un usuario después de completar una transacción
     *
     * El proceso realiza:
     * 1. Actualizar la reputación del usuario calificado.
     * 2. Registrar que el usuario calificador ya calificó la publicación correspondiente
     *
     * @param idUsuarioCalificado ID del usuario que recibirá la calificación.
     * @param estrellas Valor de la calificación (1–5 estrellas).
     * @param idPublicacion ID de la publicación asociada a la transacción.
     * @param idUsuarioCalificador ID del usuario que emite la calificación.
     * @return true si la operación se realizó correctamente; false si hbo un error.
     */
    public boolean calificarUsuario(String idUsuarioCalificado, int estrellas, String idPublicacion,
            String idUsuarioCalificador) {
        try {
            User usuarioCalificado = userService.buscarUsuarioPorId(idUsuarioCalificado);
            if (usuarioCalificado == null) {
                JOptionPane.showMessageDialog(null, "Usuario a calificar no encontrado.");
                return false;
            }

            // 1. Actualizar reputación del calificado
            usuarioCalificado.calificar(estrellas);
            userService.actualizarUsuario(usuarioCalificado);

            // 2. Registrar que el calificador ya calificó esta publicación
            if (idUsuarioCalificador != null && idPublicacion != null) {
                User usuarioCalificador = userService.buscarUsuarioPorId(idUsuarioCalificador);
                if (usuarioCalificador != null) {
                    usuarioCalificador.agregarPublicacionCalificada(idPublicacion);
                    userService.actualizarUsuario(usuarioCalificador);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al calificar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el perfil del usuario (nombre, apellido, email, ubicación,
     * contraseña).
     * No permite modificar username, ID, rol o reputación.
     */
    public boolean actualizarPerfil(User usuario) {
        try {
            userService.actualizarUsuario(usuario);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
