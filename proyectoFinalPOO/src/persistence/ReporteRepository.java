/*
 * Clase: ReporteRepository
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Repositorio de persistencia.
 */

package persistence;

import model.Reporte;
import util.EstadoReporte;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio para gestionar la persistencia de reportes/tickets.
 */
public class ReporteRepository {

    private static final String ARCHIVO_REPORTES = "data/reportes.dat";
    private List<Reporte> reportes;

    public ReporteRepository() {
        cargarReportes();
    }

    @SuppressWarnings("unchecked")
    private void cargarReportes() {
        try {
            Object obj = Persistencia.cargarObjeto(ARCHIVO_REPORTES);
            if (obj instanceof List<?>) {
                this.reportes = (List<Reporte>) obj;
            } else {
                this.reportes = new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            this.reportes = new ArrayList<>();
        }
    }

    private void guardarEnArchivo() {
        try {
            Persistencia.guardarObjeto(ARCHIVO_REPORTES, reportes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda un nuevo reporte
     */
    public void guardarReporte(Reporte reporte) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }
        reportes.add(reporte);
        guardarEnArchivo();
    }

    /**
     * Busca un reporte por su ID
     */
    public Reporte buscarPorId(String idReporte) {
        return reportes.stream()
                .filter(r -> r.getIdReporte().equals(idReporte))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lista todos los reportes
     */
    public List<Reporte> listarTodos() {
        return new ArrayList<>(reportes);
    }

    /**
     * Lista reportes por estado
     */
    public List<Reporte> listarPorEstado(EstadoReporte estado) {
        return reportes.stream()
                .filter(r -> r.getEstado() == estado)
                .collect(Collectors.toList());
    }

    /**
     * Lista reportes de un usuario específico
     */
    public List<Reporte> listarPorReportante(String idReportante) {
        return reportes.stream()
                .filter(r -> r.getIdReportante().equals(idReportante))
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un reporte existente
     */
    public boolean actualizarReporte(Reporte reporte) {
        if (reporte == null) {
            return false;
        }

        for (int i = 0; i < reportes.size(); i++) {
            if (reportes.get(i).getIdReporte().equals(reporte.getIdReporte())) {
                reportes.set(i, reporte);
                guardarEnArchivo();
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un reporte
     */
    public boolean eliminarReporte(String idReporte) {
        boolean eliminado = reportes.removeIf(r -> r.getIdReporte().equals(idReporte));
        if (eliminado) {
            guardarEnArchivo();
        }
        return eliminado;
    }
}
