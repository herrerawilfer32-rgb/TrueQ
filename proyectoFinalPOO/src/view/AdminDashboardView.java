/*
 * Clase: AdminDashboardView
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
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
        // ---------- PESTAÑAS PRINCIPALES ----------
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel Analíticas (NUEVO)
        try {
            service.AnalyticsService analyticsService = new service.AnalyticsService(
                    new persistence.PublicacionRepository(),
                    new persistence.OfertaRepository(),
                    new persistence.UserRepository());
            tabbedPane.addTab("📊 Analíticas",
                    new PanelAnaliticas(adminController, analyticsService, adminUser.getId()));
        } catch (Exception e) {
            System.err.println("Error creando panel de analíticas: " + e.getMessage());
        }

        // Panel Gestión de Publicaciones (NUEVO)
        try {
            service.AdminService adminService = new service.AdminService(
                    new persistence.UserRepository(),
                    new persistence.PublicacionRepository(),
                    new persistence.OfertaRepository());
            tabbedPane.addTab("📝 Publicaciones",
                    new PanelGestionPublicaciones(adminController, adminService, adminUser.getId()));
        } catch (Exception e) {
            System.err.println("Error creando panel de publicaciones: " + e.getMessage());
        }

        // Panel Usuarios
        tabbedPane.addTab("👥 Usuarios", new PanelGestionUsuarios(adminController, adminUser));

        // Panel Reportes
        tabbedPane.addTab("🚨 Reportes", new PanelGestionReportes(reporteController, adminUser));

        // Panel Estadísticas Básicas
        JPanel panelStats = new JPanel(new GridLayout(3, 1, 10, 10));
        panelStats.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUsuarios = new JLabel("Total Usuarios: " + adminController.contarUsuarios(adminUser.getId()));
        lblUsuarios.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblPublicaciones = new JLabel(
                "Total Publicaciones: " + adminController.contarPublicaciones(adminUser.getId()));
        lblPublicaciones.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblOfertas = new JLabel("Total Ofertas: " + adminController.contarOfertas(adminUser.getId()));
        lblOfertas.setFont(new Font("Arial", Font.BOLD, 24));

        panelStats.add(lblUsuarios);
        panelStats.add(lblPublicaciones);
        panelStats.add(lblOfertas);

        tabbedPane.addTab("📈 Estadísticas", panelStats);

        // Añadimos las pestañas al centro de la ventana
        add(tabbedPane, BorderLayout.CENTER);

        // ---------- BOTÓN INFERIOR "CERRAR" ----------
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose()); // Solo cierra esta ventana de admin

        panelInferior.add(btnCerrar);

        add(panelInferior, BorderLayout.SOUTH);
    }
}
