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
        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel Usuarios
        tabbedPane.addTab("Gestión de Usuarios", new PanelGestionUsuarios(adminController, adminUser));

        // Panel Reportes
        tabbedPane.addTab("Gestión de Reportes", new PanelGestionReportes(reporteController, adminUser));

        // Panel Estadísticas (Simple por ahora)
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

        tabbedPane.addTab("Estadísticas", panelStats);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
