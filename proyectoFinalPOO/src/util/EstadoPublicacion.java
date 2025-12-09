package util;

public enum EstadoPublicacion {
	ACTIVA, // El articulo está en venta o subasta
	CERRADA, // La transacción ha finalizado
	FINALIZADA, // La transacción se completó exitosamente (pagada/intercambiada)
	PAUSADA, // El vendedor la retiró termporalmente (no sé si vamos a usar esto)
	ELIMINADA // El vendedor eliminó la publicación (solo puede ver el admin)
}
