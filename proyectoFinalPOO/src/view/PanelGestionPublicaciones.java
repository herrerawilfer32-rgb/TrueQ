/*
 * Clase: PanelGestionPublicaciones
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.AdminController;
import model.Publicacion;
import model.User;
import service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel de gestión de publicaciones para administradores.
 * Permite ver, filtrar y gestionar todas las publicaciones del sistema.
 */
public class PanelGestionPublicaciones extends JPanel {

    private final AdminController adminController;
    private final AdminService adminService;
    private final String idAdmin;

    private JTable tablaPublicaciones;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> cmbPlantillas;

    public PanelGestionPublicaciones(AdminController adminController, AdminService adminService, String idAdmin) {
        this.adminController = adminController;
        this.adminService = adminService;
        this.idAdmin = idAdmin;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        cargarPublicaciones();
    }

    private void initUI() {
        // Panel superior: Búsqueda y acciones
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));

        // Barra de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        panelBusqueda.add(txtBuscar);
        JButton btnBuscar = new JButton("🔍");
        btnBuscar.addActionListener(e -> filtrarPublicaciones());
        panelBusqueda.add(btnBuscar);
        JButton btnLimpiar = new JButton("❌");
        btnLimpiar.addActionListener(e -> {
            txtBuscar.setText("");
            cargarPublicaciones();
        });
        panelBusqueda.add(btnLimpiar);

        panelSuperior.add(panelBusqueda, BorderLayout.WEST);

        // Acciones masivas
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnEliminar = new JButton("🗑️ Eliminar Seleccionadas");
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarSeleccionadas());
        panelAcciones.add(btnEliminar);

        // Plantillas de mensajes
        cmbPlantillas = new JComboBox<>(adminService.getPlantillasMensajes());
        cmbPlantillas.insertItemAt("-- Seleccionar plantilla --", 0);
        cmbPlantillas.setSelectedIndex(0);
        panelAcciones.add(cmbPlantillas);

        JButton btnEnviarAlerta = new JButton("📧 Enviar Alerta");
        btnEnviarAlerta.setBackground(new Color(243, 156, 18));
        btnEnviarAlerta.setForeground(Color.WHITE);
        btnEnviarAlerta.addActionListener(e -> enviarAlertaSeleccionadas());
        panelAcciones.add(btnEnviarAlerta);

        panelSuperior.add(panelAcciones, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // Tabla de publicaciones
        String[] columnas = { "ID", "Título", "Tipo", "Categoría", "Condición", "Vendedor", "Estado", "Fecha" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPublicaciones = new JTable(modeloTabla);
        tablaPublicaciones.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaPublicaciones.setRowHeight(25);
        tablaPublicaciones.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaPublicaciones.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaPublicaciones.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaPublicaciones.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaPublicaciones.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaPublicaciones.getColumnModel().getColumn(5).setPreferredWidth(150);
        tablaPublicaciones.getColumnModel().getColumn(6).setPreferredWidth(100);
        tablaPublicaciones.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tablaPublicaciones);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior: Información
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("Selecciona una o más publicaciones para realizar acciones");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        panelInferior.add(lblInfo);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarPublicaciones() {
        modeloTabla.setRowCount(0);
        List<Publicacion> publicaciones = adminController.listarTodasLasPublicaciones(idAdmin);

        for (Publicacion pub : publicaciones) {
            Object[] fila = new Object[8];
            fila[0] = pub.getIdArticulo();
            fila[1] = pub.getTitulo();
            fila[2] = pub.getTipoPublicacion();
            fila[3] = pub.getCategoria() != null ? pub.getCategoria() : "Sin categoría";
            fila[4] = pub.getCondicion() != null ? pub.getCondicion() : "Sin especificar";
            fila[5] = pub.getIdVendedor();
            fila[6] = pub.getEstado();
            fila[7] = pub.getFechaPublicacion() != null ? pub.getFechaPublicacion().toString() : "N/A";
            modeloTabla.addRow(fila);
        }
    }

    private void filtrarPublicaciones() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            cargarPublicaciones();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Publicacion> publicaciones = adminController.listarTodasLasPublicaciones(idAdmin);

        for (Publicacion pub : publicaciones) {
            if (pub.getTitulo().toLowerCase().contains(busqueda) ||
                    pub.getIdArticulo().toLowerCase().contains(busqueda) ||
                    (pub.getCategoria() != null && pub.getCategoria().toLowerCase().contains(busqueda))) {

                Object[] fila = new Object[8];
                fila[0] = pub.getIdArticulo();
                fila[1] = pub.getTitulo();
                fila[2] = pub.getTipoPublicacion();
                fila[3] = pub.getCategoria() != null ? pub.getCategoria() : "Sin categoría";
                fila[4] = pub.getCondicion() != null ? pub.getCondicion() : "Sin especificar";
                fila[5] = pub.getIdVendedor();
                fila[6] = pub.getEstado();
                fila[7] = pub.getFechaPublicacion() != null ? pub.getFechaPublicacion().toString() : "N/A";
                modeloTabla.addRow(fila);
            }
        }
    }

    private void eliminarSeleccionadas() {
        int[] filasSeleccionadas = tablaPublicaciones.getSelectedRows();
        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos una publicación", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar " + filasSeleccionadas.length + " publicación(es)?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            List<String> idsEliminar = new ArrayList<>();
            for (int fila : filasSeleccionadas) {
                String id = (String) modeloTabla.getValueAt(fila, 0);
                idsEliminar.add(id);
            }

            int eliminadas = 0;
            for (String id : idsEliminar) {
                if (adminController.eliminarPublicacion(id, idAdmin)) {
                    eliminadas++;
                }
            }

            JOptionPane.showMessageDialog(this, eliminadas + " publicación(es) eliminada(s)", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarPublicaciones();
        }
    }

    private void enviarAlertaSeleccionadas() {
        int[] filasSeleccionadas = tablaPublicaciones.getSelectedRows();
        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "Selecciona al menos una publicación", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int plantillaIndex = cmbPlantillas.getSelectedIndex();
        String mensaje;

        if (plantillaIndex == 0) {
            // Mensaje personalizado
            mensaje = JOptionPane.showInputDialog(this, "Escribe el mensaje de alerta:", "Mensaje Personalizado",
                    JOptionPane.PLAIN_MESSAGE);
            if (mensaje == null || mensaje.trim().isEmpty()) {
                return;
            }
        } else {
            mensaje = (String) cmbPlantillas.getSelectedItem();
        }

        // Obtener IDs de vendedores únicos
        java.util.Set<String> vendedores = new java.util.HashSet<>();
        for (int fila : filasSeleccionadas) {
            String idVendedor = (String) modeloTabla.getValueAt(fila, 5);
            vendedores.add(idVendedor);
        }

        int enviados = 0;
        for (String idVendedor : vendedores) {
            try {
                if (adminService.enviarAlertaUsuario(idVendedor, mensaje, idAdmin)) {
                    enviados++;
                }
            } catch (Exception e) {
                System.err.println("Error enviando alerta a " + idVendedor + ": " + e.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this,
                "Alerta enviada a " + enviados + " vendedor(es)",
                "Alertas Enviadas",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
