/*
 * Clase: AuthController
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * Descripción: Controlador que maneja la autenticación de usuarios.
 * Centraliza la lógica de login y registro, delegando al UserService.
 */

package controller;

import model.User;
import service.UserService;

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Maneja el inicio de sesión del usuario
    public User manejarLogin(String username, String password) {
        return userService.iniciarSesion(username, password);
    }

    // Maneja el registro de un nuevo usuario en el sistema
    public boolean manejarRegistro(String id, String username, String password, String correo, String nombre,
            String apellido, String ubicacion) {
        return userService.registrarUsuario(id, username, password, correo, nombre, apellido, ubicacion);
    }

}