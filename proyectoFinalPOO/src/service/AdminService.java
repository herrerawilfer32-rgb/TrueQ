package service;

import model.User;
import model.Publicacion;
import model.Oferta;
import persistence.UserRepository;
import persistence.PublicacionRepository;
import persistence.OfertaRepository;
import util.RolUsuario;

import java.util.List;

/**
 * Servicio para operaciones administrativas del sistema.
 * Solo accesible por usuarios con rol ADMIN.
 */
public class AdminService {

    private final UserRepository userRepository;
    private final PublicacionRepository publicacionRepository;
    private final OfertaRepository ofertaRepository;

    public AdminService(UserRepository userRepository, PublicacionRepository publicacionRepository,
            OfertaRepository ofertaRepository) {
        this.userRepository = userRepository;
        this.publicacionRepository = publicacionRepository;
        this.ofertaRepository = ofertaRepository;
    }

    /**
     * Verifica que el usuario sea administrador
     */
    private void verificarAdmin(String idAdmin) {
        User admin = userRepository.buscarPorId(idAdmin);
        if (admin == null || !admin.isAdmin()) {
            throw new SecurityException("Acceso denegado: Se requieren permisos de administrador");
        }
    }

    // ==================== GESTIÓN DE USUARIOS ====================

    /**
     * Lista todos los usuarios del sistema
     */
    public List<User> listarTodosLosUsuarios(String idAdmin) {
        verificarAdmin(idAdmin);
        return userRepository.listarTodos();
    }

    /**
     * Busca un usuario por ID
     */
    public User buscarUsuarioPorId(String idUsuario, String idAdmin) {
        verificarAdmin(idAdmin);
        return userRepository.buscarPorId(idUsuario);
    }

    /**
     * Actualiza la información de un usuario
     */
    public boolean actualizarUsuario(User usuario, String idAdmin) {
        verificarAdmin(idAdmin);
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        return userRepository.actualizar(usuario);
    }

    /**
     * Elimina un usuario del sistema
     */
    public boolean eliminarUsuario(String idUsuario, String idAdmin) {
        verificarAdmin(idAdmin);

        // No permitir que el admin se elimine a sí mismo
        if (idUsuario.equals(idAdmin)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo");
        }

        return userRepository.eliminar(idUsuario);
    }

    /**
     * Cambia el rol de un usuario
     */
    public boolean cambiarRolUsuario(String idUsuario, RolUsuario nuevoRol, String idAdmin) {
        verificarAdmin(idAdmin);

        User usuario = userRepository.buscarPorId(idUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // No permitir que el admin cambie su propio rol
        if (idUsuario.equals(idAdmin)) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol");
        }

        usuario.setRol(nuevoRol);
        return userRepository.actualizar(usuario);
    }

    // ==================== GESTIÓN DE CONTENIDO ====================

    /**
     * Lista todas las publicaciones del sistema
     */
    public List<Publicacion> listarTodasLasPublicaciones(String idAdmin) {
        verificarAdmin(idAdmin);
        return publicacionRepository.buscarTodasLasPublicaciones();
    }

    /**
     * Elimina una publicación (moderación)
     */
    public boolean eliminarPublicacion(String idPublicacion, String idAdmin) {
        verificarAdmin(idAdmin);
        publicacionRepository.eliminar(idPublicacion);
        return true;
    }

    // ==================== GESTIÓN DE OFERTAS ====================

    /**
     * Lista todas las ofertas del sistema
     */
    public List<Oferta> listarTodasLasOfertas(String idAdmin) {
        verificarAdmin(idAdmin);
        return ofertaRepository.buscarTodasLasOfertas();
    }

    /**
     * Elimina una oferta (moderación)
     */
    public boolean eliminarOferta(String idOferta, String idAdmin) {
        verificarAdmin(idAdmin);
        ofertaRepository.eliminar(idOferta);
        return true;
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * Obtiene el número total de usuarios
     */
    public int contarUsuarios(String idAdmin) {
        verificarAdmin(idAdmin);
        return userRepository.listarTodos().size();
    }

    /**
     * Obtiene el número total de publicaciones
     */
    public int contarPublicaciones(String idAdmin) {
        verificarAdmin(idAdmin);
        return publicacionRepository.buscarTodasLasPublicaciones().size();
    }

    /**
     * Obtiene el número total de ofertas
     */
    public int contarOfertas(String idAdmin) {
        verificarAdmin(idAdmin);
        return ofertaRepository.buscarTodasLasOfertas().size();
    }
}
