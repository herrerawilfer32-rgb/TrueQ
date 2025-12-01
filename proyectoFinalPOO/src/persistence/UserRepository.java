package persistence;

import model.User;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class UserRepository {
    // La "Base de Datos" simulada: Key: nombreUsuario (String), Value: Objeto User
    private static Map<String, User> baseDeDatos = new HashMap<>();
    private static final String RUTA_ARCHIVO = "users.dat";

    // Bloque estático con datos de prueba
    static {
        try {
            // Intentamos cargar los datos del archivo
            @SuppressWarnings("unchecked")
            Map<String, User> loaded = (Map<String, User>) Persistencia.cargarObjeto(RUTA_ARCHIVO);
            baseDeDatos = loaded;
        } catch (Exception e) {
            // Si falla (ej. primera vez), inicializamos y cargamos datos dummy
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

            // Guardamos los datos iniciales
            try {
                Persistencia.guardarObjeto(RUTA_ARCHIVO, baseDeDatos);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public UserRepository() {
        if (baseDeDatos.isEmpty()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, User> loaded = (Map<String, User>) Persistencia.cargarObjeto(RUTA_ARCHIVO);
                if (loaded != null)
                    baseDeDatos = loaded;
            } catch (Exception e) {
                // Ignorar si falla carga inicial
            }
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