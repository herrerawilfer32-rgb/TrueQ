/*
 * Clase: ConvertirAAdmin
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Clase utilidad.
 */

package util;

import model.User;
import persistence.UserRepository;

/**
 * Utilidad para convertir un usuario existente en administrador.
 * Ejecuta este programa una vez para dar permisos de admin al usuario "admin".
 */
public class ConvertirAAdmin {

    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();

        // Buscar el usuario "admin"
        User admin = userRepo.buscarPorNombreUsuario("admin");

        if (admin == null) {
            System.out.println("❌ Usuario 'admin' no encontrado.");
            System.out.println("Por favor, crea primero el usuario 'admin' con contraseña '12345'");
            return;
        }

        // Cambiar el rol a ADMIN
        admin.setRol(RolUsuario.ADMIN);

        // Guardar cambios
        boolean exito = userRepo.actualizar(admin);

        if (exito) {
            System.out.println("✅ Usuario 'admin' convertido a administrador exitosamente!");
            System.out.println("Ahora puedes iniciar sesión y ver el botón 'Panel Admin'");
        } else {
            System.out.println("❌ Error al actualizar el usuario");
        }
    }
}
