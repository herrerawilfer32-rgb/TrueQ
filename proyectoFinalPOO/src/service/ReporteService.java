/*
 * Clase: ReporteService
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Servicio de lÃ³gica de negocio.
 */

package service;

import model.Reporte;
import model.User;
import persistence.ReporteRepository;
import persistence.UserRepository;
import util.TipoReporte;
import util.EstadoReporte;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar reportes/tickets de usuarios.
 */
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UserRepository userRepository;

    public ReporteService(ReporteRepository reporteRepository, UserRepository userRepository) {
        this.reporteRepository = reporteRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea un nuevo reporte
     */
    public Reporte crearReporte(TipoReporte tipo, String idReportante, String idObjetoReportado,
            String motivo, String descripcion) {
        // Validaciones
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de reporte no puede ser nulo");
        }
        if (idReportante == null || idReportante.isEmpty()) {
            throw new IllegalArgumentException("El ID del reportante no puede estar vacío");
        }
        if (idObjetoReportado == null || idObjetoReportado.isEmpty()) {
            throw new IllegalArgumentException("El ID del objeto reportado no puede estar vacío");
        }
        if (motivo == null || motivo.isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede estar vacío");
        }

        // Verificar que el reportante existe
        User reportante = userRepository.buscarPorId(idReportante);
        if (reportante == null) {
            throw new IllegalArgumentException("El usuario reportante no existe");
        }

        // Crear el reporte
        String idReporte = "REP-" + UUID.randomUUID().toString().substring(0, 8);
        Reporte reporte = new Reporte(idReporte, idReportante, tipo, idObjetoReportado, motivo, descripcion);

        reporteRepository.guardarReporte(reporte);
        return reporte;
    }

    /**
     * Lista todos los reportes pendientes
     */
    public List<Reporte> listarReportesPendientes() {
        return reporteRepository.listarPorEstado(EstadoReporte.PENDIENTE);
    }

    /**
     * Lista todos los reportes
     */
    public List<Reporte> listarTodosLosReportes(String idAdmin) {
        verificarAdmin(idAdmin);
        return reporteRepository.listarTodos();
    }

    /**
     * Lista reportes por estado
     */
    public List<Reporte> listarReportesPorEstado(EstadoReporte estado, String idAdmin) {
        verificarAdmin(idAdmin);
        return reporteRepository.listarPorEstado(estado);
    }

    /**
     * Lista reportes de un usuario
     */
    public List<Reporte> listarReportesPorUsuario(String idUsuario) {
        return reporteRepository.listarPorReportante(idUsuario);
    }

    /**
     * Asigna un reporte a un administrador
     */
    public boolean asignarReporte(String idReporte, String idAdmin) {
        verificarAdmin(idAdmin);

        Reporte reporte = reporteRepository.buscarPorId(idReporte);
        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }

        reporte.setIdAdminAsignado(idAdmin);
        reporte.setEstado(EstadoReporte.EN_REVISION);

        return reporteRepository.actualizarReporte(reporte);
    }

    /**
     * Resuelve un reporte
     */
    public boolean resolverReporte(String idReporte, String resolucion, String idAdmin) {
        verificarAdmin(idAdmin);

        Reporte reporte = reporteRepository.buscarPorId(idReporte);
        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }

        if (resolucion == null || resolucion.isEmpty()) {
            throw new IllegalArgumentException("La resolución no puede estar vacía");
        }

        reporte.setResolucion(resolucion);
        reporte.setEstado(EstadoReporte.RESUELTO);

        return reporteRepository.actualizarReporte(reporte);
    }

    /**
     * Rechaza un reporte
     */
    public boolean rechazarReporte(String idReporte, String motivo, String idAdmin) {
        verificarAdmin(idAdmin);

        Reporte reporte = reporteRepository.buscarPorId(idReporte);
        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }

        reporte.setResolucion("Rechazado: " + motivo);
        reporte.setEstado(EstadoReporte.RECHAZADO);

        return reporteRepository.actualizarReporte(reporte);
    }

    /**
     * Busca un reporte por ID
     */
    public Reporte buscarReportePorId(String idReporte) {
        return reporteRepository.buscarPorId(idReporte);
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
