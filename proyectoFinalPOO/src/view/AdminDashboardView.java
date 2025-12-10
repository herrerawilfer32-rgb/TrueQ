/**
 * Clase: ConfiguracionGlobal
 * Vista de la interfaz.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package view;

import controller.AdminController;
import controller.ReporteController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardView extends JFrame {

    private final AdminController adminController;
    private final ReporteController reporteController;
    private final User adminUser;

    public AdminDashboardView(AdminController adminController, ReporteController reporteController, User adminUser) {
        this.adminController = adminController;
        this.reporteController = reporteController;
        this.adminUser = adminUser;

        setTitle("Panel de Administración - Mercado Local");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // No cerrar toda la app

        initUI();
    }

    private void initUI() {
        // Encabezado
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(util.UIConstants.MORADO_PRINCIPAL);
        panelEncabezado.setBorder(util.UIConstants.BORDE_VACIO_20);

        JLabel lblTitulo = new JLabel("🛠️ Panel de Administración");
        lblTitulo.setFont(util.UIConstants.FUENTE_TITULO);
        lblTitulo.setForeground(util.UIConstants.DORADO);
        panelEncabezado.add(lblTitulo);

        add(panelEncabezado, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel Analíticas
        try {
            service.AnalyticsService analyticsService = new service.AnalyticsService(
                    new persistence.PublicacionRepository(),
                    new persistence.OfertaRepository(),
                    new persistence.UserRepository());
            tabbedPane.addTab(" Analíticas",
                    new PanelAnaliticas(adminController, analyticsService, adminUser.getId()));
        } catch (Exception e) {
            System.err.println("Error creando panel de analíticas: " + e.getMessage());
        }

        // Panel Gestión de Publicaciones
        try {
            service.AdminService adminService = new service.AdminService(
                    new persistence.UserRepository(),
                    new persistence.PublicacionRepository(),
                    new persistence.OfertaRepository());
            tabbedPane.addTab(" Publicaciones",
                    new PanelGestionPublicaciones(adminController, adminService, adminUser.getId()));
        } catch (Exception e) {
            System.err.println("Error creando panel de publicaciones: " + e.getMessage());
        }

        // Panel Usuarios
        tabbedPane.addTab(" Usuarios", new PanelGestionUsuarios(adminController, adminUser));

        // Panel Reportes
        tabbedPane.addTab(" Reportes", new PanelGestionReportes(reporteController, adminUser));

        // Panel Estadísticas
        JPanel panelStats = new JPanel(new GridLayout(3, 1, 10, 10));
        panelStats.setBackground(util.UIConstants.BLANCO);
        panelStats.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUsuarios = new JLabel("Total Usuarios: " + adminController.contarUsuarios(adminUser.getId()));
        lblUsuarios.setFont(util.UIConstants.FUENTE_SUBTITULO);

        JLabel lblPublicaciones = new JLabel(
                "Total Publicaciones: " + adminController.contarPublicaciones(adminUser.getId()));
        lblPublicaciones.setFont(util.UIConstants.FUENTE_SUBTITULO);

        JLabel lblOfertas = new JLabel("Total Ofertas: " + adminController.contarOfertas(adminUser.getId()));
        lblOfertas.setFont(util.UIConstants.FUENTE_SUBTITULO);

        panelStats.add(lblUsuarios);
        panelStats.add(lblPublicaciones);
        panelStats.add(lblOfertas);

        tabbedPane.addTab(" Estadísticas", panelStats);

        add(tabbedPane, BorderLayout.CENTER);

        // Panel Inferior
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelInferior.setBackground(util.UIConstants.MORADO_PRINCIPAL);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(util.UIConstants.GRIS_NEUTRAL);
        btnCerrar.setForeground(util.UIConstants.NEGRO);
        btnCerrar.setFont(util.UIConstants.FUENTE_BOTON);
        btnCerrar.addActionListener(e -> dispose());

        panelInferior.add(btnCerrar);

        add(panelInferior, BorderLayout.SOUTH);
    }
}
