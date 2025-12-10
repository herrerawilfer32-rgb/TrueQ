/*
 * Clase: PanelAnaliticas
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.AdminController;
import service.AnalyticsService;
import model.Publicacion;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

/**
 * Panel de analíticas para el dashboard de administración.
 * Muestra métricas clave del sistema.
 */
public class PanelAnaliticas extends JPanel {

    private final AdminController adminController;
    private final AnalyticsService analyticsService;
    private final String idAdmin;

    // Componentes UI
    private JLabel lblSatisfaccion;
    private JLabel lblTransaccionesCompletadas;
    private JLabel lblPendientesPago;
    private JLabel lblPendientesIntercambio;
    private JTextArea txtCategoriasActivas;
    private JTextArea txtIntercambiosCiudad;

    public PanelAnaliticas(AdminController adminController, AnalyticsService analyticsService, String idAdmin) {
        this.adminController = adminController;
        this.analyticsService = analyticsService;
        this.idAdmin = idAdmin;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // Panel superior: Métricas principales
        JPanel panelMetricas = new JPanel(new GridLayout(2, 2, 15, 15));
        panelMetricas.setBorder(BorderFactory.createTitledBorder("Métricas Principales"));

        // Tasa de satisfacción
        JPanel panelSatisfaccion = crearPanelMetrica("Tasa de Satisfacción", "0.0");
        lblSatisfaccion = (JLabel) panelSatisfaccion.getComponent(1);
        panelMetricas.add(panelSatisfaccion);

        // Transacciones completadas
        JPanel panelCompletadas = crearPanelMetrica("Transacciones Completadas", "0");
        lblTransaccionesCompletadas = (JLabel) panelCompletadas.getComponent(1);
        panelMetricas.add(panelCompletadas);

        // Pendientes de pago
        JPanel panelPendientesPago = crearPanelMetrica("Pendientes de Pago", "0");
        lblPendientesPago = (JLabel) panelPendientesPago.getComponent(1);
        panelMetricas.add(panelPendientesPago);

        // Pendientes de intercambio
        JPanel panelPendientesIntercambio = crearPanelMetrica("Pendientes de Intercambio", "0");
        lblPendientesIntercambio = (JLabel) panelPendientesIntercambio.getComponent(1);
        panelMetricas.add(panelPendientesIntercambio);

        add(panelMetricas, BorderLayout.NORTH);

        // Panel central: Categorías y ciudades
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 15));

        // Categorías más activas
        JPanel panelCategorias = new JPanel(new BorderLayout());
        panelCategorias.setBorder(BorderFactory.createTitledBorder("Top 10 Categorías Más Activas"));
        txtCategoriasActivas = new JTextArea();
        txtCategoriasActivas.setEditable(false);
        txtCategoriasActivas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollCategorias = new JScrollPane(txtCategoriasActivas);
        panelCategorias.add(scrollCategorias, BorderLayout.CENTER);
        panelCentral.add(panelCategorias);

        // Intercambios por ciudad
        JPanel panelCiudades = new JPanel(new BorderLayout());
        panelCiudades.setBorder(BorderFactory.createTitledBorder("Intercambios por Ciudad"));
        txtIntercambiosCiudad = new JTextArea();
        txtIntercambiosCiudad.setEditable(false);
        txtIntercambiosCiudad.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollCiudades = new JScrollPane(txtIntercambiosCiudad);
        panelCiudades.add(scrollCiudades, BorderLayout.CENTER);
        panelCentral.add(panelCiudades);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior: Botón de actualizar
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("🔄 Actualizar Datos");
        btnActualizar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnActualizar.setBackground(new Color(46, 204, 113));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(e -> cargarDatos());
        panelInferior.add(btnActualizar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelMetrica(String titulo, String valorInicial) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }

    private void cargarDatos() {
        // Tasa de satisfacción
        double satisfaccion = analyticsService.getTasaSatisfaccionPorCalificaciones();
        lblSatisfaccion.setText(String.format("%.2f / 5.0", satisfaccion));

        // Color coding
        if (satisfaccion >= 4.0) {
            lblSatisfaccion.setForeground(new Color(46, 204, 113)); // Verde
        } else if (satisfaccion >= 3.0) {
            lblSatisfaccion.setForeground(new Color(241, 196, 15)); // Amarillo
        } else {
            lblSatisfaccion.setForeground(new Color(231, 76, 60)); // Rojo
        }

        // Transacciones completadas
        int completadas = analyticsService.getTotalTransaccionesCompletadas();
        lblTransaccionesCompletadas.setText(String.valueOf(completadas));

        // Pendientes de pago
        List<Publicacion> pendientesPago = analyticsService.getTransaccionesPendientesPago();
        lblPendientesPago.setText(String.valueOf(pendientesPago.size()));
        if (pendientesPago.size() > 0) {
            lblPendientesPago.setForeground(new Color(231, 76, 60));
        }

        // Pendientes de intercambio
        List<Publicacion> pendientesIntercambio = analyticsService.getTransaccionesPendientesIntercambio();
        lblPendientesIntercambio.setText(String.valueOf(pendientesIntercambio.size()));
        if (pendientesIntercambio.size() > 0) {
            lblPendientesIntercambio.setForeground(new Color(231, 76, 60));
        }

        // Categorías más activas
        Map<String, Integer> categorias = analyticsService.getCategoriasMasActivas();
        StringBuilder sbCategorias = new StringBuilder();
        sbCategorias.append(String.format("%-30s %10s\n", "Categoría", "Publicaciones"));
        sbCategorias.append("-".repeat(42)).append("\n");
        for (Map.Entry<String, Integer> entry : categorias.entrySet()) {
            sbCategorias.append(String.format("%-30s %10d\n", entry.getKey(), entry.getValue()));
        }
        txtCategoriasActivas.setText(sbCategorias.toString());

        // Intercambios por ciudad
        Map<String, Integer> ciudades = analyticsService.getIntercambiosPorCiudad();
        StringBuilder sbCiudades = new StringBuilder();
        sbCiudades.append(String.format("%-30s %10s\n", "Ciudad", "Intercambios"));
        sbCiudades.append("-".repeat(42)).append("\n");
        for (Map.Entry<String, Integer> entry : ciudades.entrySet()) {
            sbCiudades.append(String.format("%-30s %10d\n", entry.getKey(), entry.getValue()));
        }
        txtIntercambiosCiudad.setText(sbCiudades.toString());
    }
}
