/**
 * Clase: ReporteController
 * Controlador encargado de manejar todas las operaciones relacionadas con los reportes.Actúa como intermediario entre la interfaz gráfica y la capa de servicios
 * Permite crear reportes, listarlos, asignar, resolver o rechazar reportes.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package controller;

import model.Reporte;
import service.ReporteService;
import util.EstadoReporte;
import util.TipoReporte;

import java.util.List;

public class ReporteController {

    private final ReporteService reporteService;
    
    /**
     * Constructor del controlador de reportes.
     *
     * @param reporteService Servicio encargado de la lógica de negocio de los reportes.
     */
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }
    
    /**
     * Crea un nuevo reporte en el sistema.
     *
     * @param tipo Tipo de reporte (usuario, publicación, oferta, etc.)
     * @param idReportante ID del usuario que realiza el reporte.
     * @param idObjetoReportado ID del usuario/objeto/reportado.
     * @param motivo Motivo resumido del reporte.
     * @param descripcion Descripción detallada del problema.
     * @return El reporte creado o null si ocurre un error.
     */
    public Reporte crearReporte(TipoReporte tipo, String idReportante, String idObjetoReportado,
            String motivo, String descripcion) {
        try {
            return reporteService.crearReporte(tipo, idReportante, idObjetoReportado, motivo, descripcion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lista todos los reportes que aún no han sido asignados a un administrador.
     *
     * @return Lista de reportes pendientes.
     */
    public List<Reporte> listarReportesPendientes() {
        return reporteService.listarReportesPendientes();
    }
    
    /**
     * Lista todos los reportes del sistema. Solo accesible por administradores.
     *
     * @param idAdmin ID del administrador que solicita la información.
     * @return Lista completa de reportes.
     */
    public List<Reporte> listarTodosLosReportes(String idAdmin) {
        return reporteService.listarTodosLosReportes(idAdmin);
    }

    /**
     * Lista reportes filtrados por su estado (PENDIENTE, ASIGNADO, RESUELTO, RECHAZADO).
     *
     * @param estado Estado por el cual filtrar.
     * @param idAdmin ID del administrador solicitante.
     * @return Lista de reportes en el estado solicitado.
     */
    public List<Reporte> listarReportesPorEstado(EstadoReporte estado, String idAdmin) {
        return reporteService.listarReportesPorEstado(estado, idAdmin);
    }

    /**
     * Lista todos los reportes hechos por un usuario específico.
     *
     * @param idUsuario ID del usuario que realizó los reportes.
     * @return Lista de reportes generados por ese usuario.
     */
    public List<Reporte> listarReportesPorUsuario(String idUsuario) {
        return reporteService.listarReportesPorUsuario(idUsuario);
    }
    
     /**
     * Asigna un reporte a un administrador para su revisión.
     *
     * @param idReporte ID del reporte a asignar.
     * @param idAdmin ID del administrador que toma el reporte.
     * @return true si se asignó correctamente, false en caso de error.
     */
    public boolean asignarReporte(String idReporte, String idAdmin) {
        try {
            return reporteService.asignarReporte(idReporte, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Marca un reporte como reselto, indicando la resolución aplicada.
     *
     * @param idReporte ID del reporte.
     * @param resolucion Descripción de la solución aplicada.
     * @param idAdmin ID del administrador responsable.
     * @return true si se resolvió correctamente, false en caso de fallo.
     */
    public boolean resolverReporte(String idReporte, String resolucion, String idAdmin) {
        try {
            return reporteService.resolverReporte(idReporte, resolucion, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Marca un reporte como rechazado, agregando el motivo del rechazo.
     *
     * @param idReporte ID del reporte a rechazar.
     * @param motivo Razón por la que se rechaza el reporte.
     * @param idAdmin Administrador responsable del rechazo.
     * @return true si se rechazó exitosamente, false si ocurre un error.
     */
    public boolean rechazarReporte(String idReporte, String motivo, String idAdmin) {
        try {
            return reporteService.rechazarReporte(idReporte, motivo, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un reporte por su id único.
     *
     * @param idReporte ID del reporte.
     * @return El reporte encontrado o null si no existe.
     */
    public Reporte buscarReportePorId(String idReporte) {
        return reporteService.buscarReportePorId(idReporte);
    }
}
