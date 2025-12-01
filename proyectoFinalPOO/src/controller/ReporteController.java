package controller;

import model.Reporte;
import service.ReporteService;
import util.EstadoReporte;
import util.TipoReporte;

import java.util.List;

public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    public Reporte crearReporte(TipoReporte tipo, String idReportante, String idObjetoReportado,
            String motivo, String descripcion) {
        try {
            return reporteService.crearReporte(tipo, idReportante, idObjetoReportado, motivo, descripcion);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Reporte> listarReportesPendientes() {
        return reporteService.listarReportesPendientes();
    }

    public List<Reporte> listarTodosLosReportes(String idAdmin) {
        return reporteService.listarTodosLosReportes(idAdmin);
    }

    public List<Reporte> listarReportesPorEstado(EstadoReporte estado, String idAdmin) {
        return reporteService.listarReportesPorEstado(estado, idAdmin);
    }

    public List<Reporte> listarReportesPorUsuario(String idUsuario) {
        return reporteService.listarReportesPorUsuario(idUsuario);
    }

    public boolean asignarReporte(String idReporte, String idAdmin) {
        try {
            return reporteService.asignarReporte(idReporte, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resolverReporte(String idReporte, String resolucion, String idAdmin) {
        try {
            return reporteService.resolverReporte(idReporte, resolucion, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rechazarReporte(String idReporte, String motivo, String idAdmin) {
        try {
            return reporteService.rechazarReporte(idReporte, motivo, idAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Reporte buscarReportePorId(String idReporte) {
        return reporteService.buscarReportePorId(idReporte);
    }
}
