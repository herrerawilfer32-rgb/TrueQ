package persistence;

import model.ConfiguracionGlobal;

import java.io.*;

/**
 * Repositorio para gestionar la configuración global del sistema.
 */
public class ConfiguracionRepository {

    private static final String ARCHIVO_CONFIG = "data/config.dat";
    private ConfiguracionGlobal configuracion;

    public ConfiguracionRepository() {
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        try {
            Object obj = Persistencia.cargarObjeto(ARCHIVO_CONFIG);
            if (obj instanceof ConfiguracionGlobal) {
                this.configuracion = (ConfiguracionGlobal) obj;
            } else {
                // Crear configuración por defecto
                this.configuracion = new ConfiguracionGlobal();
                guardarEnArchivo();
            }
        } catch (IOException | ClassNotFoundException e) {
            // Crear configuración por defecto si no existe
            this.configuracion = new ConfiguracionGlobal();
            guardarEnArchivo();
        }
    }

    private void guardarEnArchivo() {
        try {
            Persistencia.guardarObjeto(ARCHIVO_CONFIG, configuracion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la configuración actual
     */
    public ConfiguracionGlobal obtenerConfiguracion() {
        return configuracion;
    }

    /**
     * Actualiza la configuración
     */
    public void actualizarConfiguracion(ConfiguracionGlobal nuevaConfig) {
        if (nuevaConfig == null) {
            throw new IllegalArgumentException("La configuración no puede ser nula");
        }
        this.configuracion = nuevaConfig;
        guardarEnArchivo();
    }
}
