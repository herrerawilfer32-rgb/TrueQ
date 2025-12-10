/**
 * Clase: AnalyticsService
 * Servicio de lógica de negocio.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.0
 */
package service;

import model.*;
import persistence.*;
import util.EstadoOferta;
import util.EstadoPublicacion;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para generar analíticas y reportes del sistema.
 */
public class AnalyticsService {

    private final PublicacionRepository publicacionRepo;
    private final OfertaRepository ofertaRepo;
    private final UserRepository userRepo;

    public AnalyticsService(PublicacionRepository publicacionRepo, OfertaRepository ofertaRepo,
            UserRepository userRepo) {
        this.publicacionRepo = publicacionRepo;
        this.ofertaRepo = ofertaRepo;
        this.userRepo = userRepo;
    }

    /**
     * Obtiene las categorías más activas con su conteo de publicaciones.
     * 
     * @return Map con categoría como clave y número de publicaciones como valor
     */
    public Map<String, Integer> getCategoriasMasActivas() {
        List<Publicacion> todas = publicacionRepo.buscarTodasLasPublicaciones();
        Map<String, Integer> conteo = new HashMap<>();

        for (Publicacion pub : todas) {
            String categoria = pub.getCategoria();
            if (categoria != null && !categoria.isEmpty()) {
                conteo.put(categoria, conteo.getOrDefault(categoria, 0) + 1);
            }
        }

        // Ordenar por valor descendente y retornar top 10
        return conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Obtiene el número de intercambios completados por ciudad.
     * 
     * @return Map con ciudad como clave y número de intercambios como valor
     */
    public Map<String, Integer> getIntercambiosPorCiudad() {
        List<Publicacion> finalizadas = publicacionRepo.buscarTodasLasPublicaciones().stream()
                .filter(p -> p.getEstado() == EstadoPublicacion.FINALIZADA)
                .collect(Collectors.toList());

        Map<String, Integer> conteo = new HashMap<>();

        for (Publicacion pub : finalizadas) {
            User vendedor = userRepo.buscarPorId(pub.getIdVendedor());
            if (vendedor != null && vendedor.getUbicacion() != null) {
                String ciudad = vendedor.getUbicacion();
                conteo.put(ciudad, conteo.getOrDefault(ciudad, 0) + 1);
            }
        }

        return conteo.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Calcula la tasa de satisfacción basada en las calificaciones de usuarios.
     * 
     * @return Promedio de calificaciones (0.0 a 5.0)
     */
    public double getTasaSatisfaccionPorCalificaciones() {
        List<User> usuarios = userRepo.listarTodos();
        double sumaCalificaciones = 0.0;
        int totalUsuariosCalificados = 0;

        for (User user : usuarios) {
            if (user.getNumeroCalificaciones() > 0) {
                sumaCalificaciones += user.getReputacion();
                totalUsuariosCalificados++;
            }
        }

        return totalUsuariosCalificados > 0 ? sumaCalificaciones / totalUsuariosCalificados : 0.0;
    }

    /**
     * Obtiene el total de transacciones completadas (publicaciones finalizadas).
     * 
     * @return Número de transacciones completadas
     */
    public int getTotalTransaccionesCompletadas() {
        return (int) publicacionRepo.buscarTodasLasPublicaciones().stream()
                .filter(p -> p.getEstado() == EstadoPublicacion.FINALIZADA)
                .count();
    }

    /**
     * Obtiene transacciones pendientes de pago (subastas cerradas con ganador pero
     * no finalizadas).
     * 
     * @return Lista de publicaciones pendientes de pago
     */
    public List<Publicacion> getTransaccionesPendientesPago() {
        List<Publicacion> subastasCerradas = publicacionRepo.buscarTodasLasPublicaciones().stream()
                .filter(p -> p instanceof PublicacionSubasta)
                .filter(p -> p.getEstado() == EstadoPublicacion.CERRADA)
                .collect(Collectors.toList());

        List<Publicacion> pendientes = new ArrayList<>();

        for (Publicacion pub : subastasCerradas) {
            List<Oferta> ofertas = ofertaRepo.buscarPorPublicacion(pub.getIdArticulo());
            boolean tieneOfertaAceptada = ofertas.stream()
                    .anyMatch(o -> o.getEstadoOferta() == EstadoOferta.ACEPTADA);

            if (tieneOfertaAceptada) {
                pendientes.add(pub);
            }
        }

        return pendientes;
    }

    /**
     * Obtiene transacciones pendientes de intercambio (trueques con oferta
     * aceptada pero no finalizados).
     * 
     * @return Lista de publicaciones pendientes de intercambio
     */
    public List<Publicacion> getTransaccionesPendientesIntercambio() {
        List<Publicacion> trueques = publicacionRepo.buscarTodasLasPublicaciones().stream()
                .filter(p -> p instanceof PublicacionTrueque)
                .filter(p -> p.getEstado() != EstadoPublicacion.FINALIZADA)
                .collect(Collectors.toList());

        List<Publicacion> pendientes = new ArrayList<>();

        for (Publicacion pub : trueques) {
            List<Oferta> ofertas = ofertaRepo.buscarPorPublicacion(pub.getIdArticulo());
            boolean tieneOfertaAceptada = ofertas.stream()
                    .anyMatch(o -> o.getEstadoOferta() == EstadoOferta.ACEPTADA);

            if (tieneOfertaAceptada) {
                pendientes.add(pub);
            }
        }

        return pendientes;
    }
}
