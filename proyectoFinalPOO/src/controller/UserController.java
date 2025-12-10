/*
 * Clase: UserController
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Controlador del sistema.
 */

package controller;

import model.User;
import service.UserService;
import javax.swing.JOptionPane;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        // Si UserService no expone guardarUsuario, deberíamos ver cómo persistir el
        // cambio de calificación.
        // Asumiremos por ahora que userService tiene acceso al repo o similar.
    }

    public User obtenerUsuario(String idUsuario) {
        return userService.buscarUsuarioPorId(idUsuario);
    }

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
