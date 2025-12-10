/*
 * Clase: UserRepository
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Repositorio de persistencia de usuarios.
 */

package persistence;

import model.User;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class UserRepository {
    // La "Base de Datos" simulada: Key: nombreUsuario (String), Value: Objeto User
    private static Map<String, User> baseDeDatos = new HashMap<>();
    private static final String RUTA_ARCHIVO = "data/users.dat";

    public UserRepository() {
        // Solo inicializar si el mapa está vacío (primera vez que se crea el
        // repositorio)
        if (baseDeDatos.isEmpty()) {
            try {
                // Intentar cargar datos existentes del archivo
                @SuppressWarnings("unchecked")
                Map<String, User> loaded = (Map<String, User>) Persistencia.cargarObjeto(RUTA_ARCHIVO);

                if (loaded != null && !loaded.isEmpty()) {
                    // Datos cargados exitosamente
                    baseDeDatos = loaded;
                    System.out.println("✓ Datos de usuarios cargados desde archivo: " + loaded.size() + " usuarios");
                } else {
                    // Archivo existe pero está vacío, crear datos de prueba
                    crearDatosDePrueba();
                }
            } catch (Exception e) {
                // El archivo no existe o hay error de deserialización
                // Solo crear datos de prueba si el archivo NO existe
                java.io.File archivo = new java.io.File(RUTA_ARCHIVO);
                if (!archivo.exists()) {
                    System.out.println("⚠ Archivo de usuarios no encontrado. Creando datos de prueba...");
                    crearDatosDePrueba();
                } else {
                    // El archivo existe pero no se puede deserializar
                    // NO sobrescribir, solo mostrar advertencia
                    System.err.println("⚠ ERROR: No se pudo cargar el archivo de usuarios existente.");
                    System.err.println("  Archivo: " + RUTA_ARCHIVO);
                    System.err.println("  Razón: " + e.getMessage());
                    System.err.println("  El archivo NO será sobrescrito. Revise la compatibilidad de versiones.");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Crea y guarda los datos de prueba iniciales (admin y vendedor)
     */
    private void crearDatosDePrueba() {
        baseDeDatos = new HashMap<>();

        // Key: "admin" / ID (Cédula): "10000000"
        User adminUser = new User(
                "admin", "Juan", "Pérez", "admin@marketplace.com",
                "12345", "10000000", "Bogotá");
        adminUser.setRol(util.RolUsuario.ADMIN); // Asignar rol de ADMIN
        baseDeDatos.put("admin", adminUser);

        // Key: "vendedor" / ID (Cédula): "20000000"
        baseDeDatos.put("vendedor", new User(
                "vendedor", "María", "Gómez", "vendedor@email.com",
                "pass", "20000000", "Medellín"));

        // Guardar los datos iniciales
        try {
            Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
            System.out.println("✓ Datos de prueba creados y guardados: " + baseDeDatos.size() + " usuarios");
        } catch (IOException ex) {
            System.err.println("⚠ ERROR: No se pudieron guardar los datos de prueba");
            ex.printStackTrace();
        }
    }

    /**
     * Guarda o actualiza un objeto Usuario, usando el nombreUsuario como clave.
     */
    public void guardar(User usuario) {
        baseDeDatos.put(usuario.getNombreUsuario(), usuario);
        try {
            Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca un usuario usando su NOMBRE DE USUARIO (búsqueda rápida O(1)).
     */
    public User buscarPorNombreUsuario(String username) {
        return baseDeDatos.get(username);
    }

    /**
     * Busca un usuario usando su ID (Cédula). Requiere iterar (O(n)).
     */
    public User buscarPorId(String id) {
        for (User user : baseDeDatos.values()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Lista todos los usuarios del sistema
     */
    public java.util.List<User> listarTodos() {
        return new java.util.ArrayList<>(baseDeDatos.values());
    }

    /**
     * Actualiza un usuario existente
     */
    public boolean actualizar(User usuario) {
        if (usuario == null || !baseDeDatos.containsKey(usuario.getNombreUsuario())) {
            return false;
        }
        guardar(usuario);
        return true;
    }

    /**
     * Elimina un usuario por su ID
     */
    public boolean eliminar(String id) {
        User usuario = buscarPorId(id);
        if (usuario == null) {
            return false;
        }
        baseDeDatos.remove(usuario.getNombreUsuario());
        try {
            Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}