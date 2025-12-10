/**
 * Clase: EstadoOferta
 *Representa los diferentes estados posibles de una oferta dentro del sistema de trueque o subastas.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package util;

public enum EstadoOferta {
	PENDIENTE, // La oferta ha sido realizada pero no ha sido aceptada ni rechazada
	ACEPTADA, // La oferta ha sido aceptada por el vendedor
	RECHAZADA, // La oferta ha sido rechazada por el vendedor
	CANCELADA, // La oferta ha sido cancelada por el comprador antes de ser aceptada o rechazada
	GANADORA // La oferta ha ganado una subasta
}
