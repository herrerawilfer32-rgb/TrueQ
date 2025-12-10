/**
 * Clase: AuthController
 * Controlador que maneja la autenticación de usuarios, centraliza la lógica de login y registro, delegando al UserService.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2 
 */

package controller;

import model.User;
import service.UserService;

public class AuthController {

    private final UserService userService;
    /**
     * Constructor que recibe el servicio de usuarios, encargado de gestionar operaciones como inicio de sesión y registro.
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

   /**
     * Maneja el proceso de inicio de sesión del usuario.
     * @param username nombre de usuario ingresado.
     * @param password contraseña ingresada.
     * @return el usuario autenticado si las credenciales son correctas, 
     *         o null si el inicio de sesión falla.
     */
    public User manejarLogin(String username, String password) {
        return userService.iniciarSesion(username, password);
    }

   /**
     * Maneja el proceso de registro de nuevos usuarios, envía los datos al servicio para crear un nuevo usuario en el sistema.
     * 
     * @return true si el registro fue exitoso o false si hubo algún error
     */
    public boolean manejarRegistro(String id, String username, String password, String correo, String nombre,
            String apellido, String ubicacion) {
        return userService.registrarUsuario(id, username, password, correo, nombre, apellido, ubicacion);
    }

}
