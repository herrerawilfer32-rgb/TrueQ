package service;

import persistence.UserRepository;
import model.User;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Método login (Búsqueda por Username)
    public User iniciarSesion(String username, String password) {

        User user = userRepository.buscarPorNombreUsuario(username);

        // Verificación (usando equals para comparar Strings)
        if (user != null && user.getContraseñaHash().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public User buscarUsuarioPorId(String id) {
        // Llama al método existente en el repositorio.
        return userRepository.buscarPorId(id);
    }

    /**
     * Registra un nuevo usuario. Valida que el USERNAME y el ID (Cédula) sean
     * únicos.
     */
    public boolean registrarUsuario(String id, String username, String password, String correo,
            String nombre, String apellido, String ubicacion) {

        // 1. Validar unicidad del USERNAME
        if (userRepository.buscarPorNombreUsuario(username) != null) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        // 2. Validar unicidad del ID (Cédula)
        if (userRepository.buscarPorId(id) != null) {
            throw new IllegalArgumentException("El ID ya está registrado.");
        }

        // 3. Validar formato de correo
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Correo electrónico inválido.");
        }

        // 4. Validar fortaleza de contraseña
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        // 5. Crear y guardar el nuevo usuario
        User nuevoUsuario = new User(
                username,
                nombre,
                apellido,
                correo,
                password,
                id,
                ubicacion);

        userRepository.guardar(nuevoUsuario);
        return true;
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    public void actualizarUsuario(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario no válido para actualizar.");
        }
        // Validar que exista
        if (userRepository.buscarPorId(user.getId()) == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }
        userRepository.guardar(user);
    }
}
