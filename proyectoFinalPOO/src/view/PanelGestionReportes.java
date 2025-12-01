package view;

import controller.ReporteController;
import model.Reporte;
import model.User;
import util.EstadoReporte;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelGestionReportes extends JPanel {

    private final ReporteController reporteController;
    private final User adminUser;
    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;
    private JComboBox<EstadoReporte> comboFiltroEstado;

    public PanelGestionReportes(ReporteController reporteController, User adminUser) {
        this.reporteController = reporteController;
        this.adminUser = adminUser;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        cargarReportes();
    }

    private void initUI() {
        // --- TOOLBAR ---
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnVerDetalle = new JButton("Ver Detalle / Resolver");

        comboFiltroEstado = new JComboBox<>(EstadoReporte.values());
        comboFiltroEstado.insertItemAt(null, 0); // Opción para "Todos"
        comboFiltroEstado.setSelectedIndex(0);
        comboFiltroEstado.addActionListener(e -> cargarReportes());

        btnRefrescar.addActionListener(e -> cargarReportes());
        btnVerDetalle.addActionListener(e -> verDetalleReporte());

        toolbar.add(new JLabel("Filtrar: "));
        toolbar.add(comboFiltroEstado);
        toolbar.addSeparator();
        toolbar.add(btnRefrescar);
        toolbar.add(btnVerDetalle);

        add(toolbar, BorderLayout.NORTH);

        // --- TABLA ---
        String[] columnas = { "ID", "Tipo", "Estado", "Reportante", "Motivo", "Fecha" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaReportes = new JTable(modeloTabla);
        tablaReportes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaReportes);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarReportes() {
        modeloTabla.setRowCount(0);
        EstadoReporte estadoFiltro = (EstadoReporte) comboFiltroEstado.getSelectedItem();

        List<Reporte> reportes;
        if (estadoFiltro == null) {
            reportes = reporteController.listarTodosLosReportes(adminUser.getId());
        } else {
            reportes = reporteController.listarReportesPorEstado(estadoFiltro, adminUser.getId());
        }

        for (Reporte r : reportes) {
            Object[] fila = {
                    r.getIdReporte(),
                    r.getTipo(),
                    r.getEstado(),
                    r.getIdReportante(),
                    r.getMotivo(),
                    r.getFechaCreacion()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void verDetalleReporte() {
        int fila = tablaReportes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un reporte.");
            return;
        }

        String idReporte = (String) modeloTabla.getValueAt(fila, 0);
        Reporte reporte = reporteController.buscarReportePorId(idReporte);

        if (reporte == null)
            return;

        JTextArea txtDescripcion = new JTextArea(reporte.getDescripcion());
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);

        JPanel panelDetalle = new JPanel(new BorderLayout(10, 10));
        panelDetalle.add(new JLabel("Descripción del reporte:"), BorderLayout.NORTH);
        panelDetalle.add(new JScrollPane(txtDescripcion), BorderLayout.CENTER);
        panelDetalle.setPreferredSize(new Dimension(400, 200));

        Object[] options = { "Resolver (Aceptar)", "Rechazar", "Cerrar" };
        int result = JOptionPane.showOptionDialog(this, panelDetalle,
                "Detalle Reporte: " + reporte.getMotivo(),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[2]);

        if (result == 0) { // Resolver
            String resolucion = JOptionPane.showInputDialog(this, "Ingrese la resolución / acción tomada:");
            if (resolucion != null && !resolucion.trim().isEmpty()) {
                if (reporteController.resolverReporte(idReporte, resolucion, adminUser.getId())) {
                    JOptionPane.showMessageDialog(this, "Reporte resuelto.");
                    cargarReportes();
                }
            }
        } else if (result == 1) { // Rechazar
            String motivoRechazo = JOptionPane.showInputDialog(this, "Ingrese motivo del rechazo:");
            if (motivoRechazo != null && !motivoRechazo.trim().isEmpty()) {
                if (reporteController.rechazarReporte(idReporte, motivoRechazo, adminUser.getId())) {
                    JOptionPane.showMessageDialog(this, "Reporte rechazado.");
                    cargarReportes();
                }
            }
        }
    }
}
