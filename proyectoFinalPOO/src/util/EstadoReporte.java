/*
 * Clase: EstadoReporte
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Clase utilidad.
 */

package util;

/**
 * Enumeración que define los estados posibles de un reporte.
 */
public enum EstadoReporte {
    /**
     * Reporte recién creado, esperando revisión
     */
    PENDIENTE,

    /**
     * Reporte asignado a un admin y en proceso de revisión
     */
    EN_REVISION,

    /**
     * Reporte resuelto satisfactoriamente
     */
    RESUELTO,

    /**
     * Reporte rechazado (no procede)
     */
    RECHAZADO
}
