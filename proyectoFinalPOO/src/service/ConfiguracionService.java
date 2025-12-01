package service;

import model.ConfiguracionGlobal;
import model.User;
import persistence.ConfiguracionRepository;
import persistence.UserRepository;

/**
 * Servicio para gestionar la configuración global del sistema.
 */
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;
    private final UserRepository userRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository, UserRepository userRepository) {
        this.configuracionRepository = configuracionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene la configuración actual
     */
    public ConfiguracionGlobal obtenerConfiguracion() {
        return configuracionRepository.obtenerConfiguracion();
    }

    /**
     * Actualiza la configuración (solo admin)
     */
    public boolean actualizarConfiguracion(ConfiguracionGlobal nuevaConfig, String idAdmin) {
        verificarAdmin(idAdmin);

        if (nuevaConfig == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula");
        }

        configuracionRepository.actualizarConfiguracion(nuevaConfig);
        return true;
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
}
