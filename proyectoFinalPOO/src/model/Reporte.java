package model;

import util.TipoReporte;
import util.EstadoReporte;
import java.util.Date;

/**
 * Representa un reporte o ticket creado por un usuario sobre una publicación o
 * usuario.
 */
public class Reporte implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String idReporte;
    private String idReportante;
    private TipoReporte tipo;
    private String idObjetoReportado;
    private String motivo;
    private String descripcion;
    private EstadoReporte estado;
    private Date fechaCreacion;
    private String idAdminAsignado;
    private String resolucion;

    public Reporte(String idReporte, String idReportante, TipoReporte tipo, String idObjetoReportado,
            String motivo, String descripcion) {
        this.idReporte = idReporte;
        this.idReportante = idReportante;
        this.tipo = tipo;
        this.idObjetoReportado = idObjetoReportado;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.estado = EstadoReporte.PENDIENTE;
        this.fechaCreacion = new Date();
        this.idAdminAsignado = null;
        this.resolucion = null;
    }

    // Getters
    public String getIdReporte() {
        return idReporte;
    }

    public String getIdReportante() {
        return idReportante;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getIdObjetoReportado() {
        return idObjetoReportado;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public String getIdAdminAsignado() {
        return idAdminAsignado;
    }

    public String getResolucion() {
        return resolucion;
    }

    // Setters
    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public void setIdAdminAsignado(String idAdminAsignado) {
        this.idAdminAsignado = idAdminAsignado;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }
}
