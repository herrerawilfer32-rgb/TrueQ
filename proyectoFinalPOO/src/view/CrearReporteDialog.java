/*
 * Clase: CrearReporteDialog
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.ReporteController;
import model.User;
import util.TipoReporte;

import javax.swing.*;
import java.awt.*;

public class CrearReporteDialog extends JDialog {

    private final ReporteController reporteController;
    private final User reportante;
    private final String idObjetoReportado;
    private final TipoReporte tipoReportePredefinido;

    private JComboBox<TipoReporte> comboTipo;
    private JTextField txtMotivo;
    private JTextArea txtDescripcion;

    public CrearReporteDialog(Frame owner, ReporteController reporteController, User reportante,
            String idObjetoReportado, TipoReporte tipoPredefinido) {
        super(owner, "Crear Reporte", true);
        this.reporteController = reporteController;
        this.reportante = reportante;
        this.idObjetoReportado = idObjetoReportado;
        this.tipoReportePredefinido = tipoPredefinido;

        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        JPanel panelForm = new JPanel(new GridLayout(0, 1, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Tipo de Reporte:"));
        comboTipo = new JComboBox<>(TipoReporte.values());
        if (tipoReportePredefinido != null) {
            comboTipo.setSelectedItem(tipoReportePredefinido);
            comboTipo.setEnabled(false);
        }
        panelForm.add(comboTipo);

        panelForm.add(new JLabel("Motivo (Corto):"));
        txtMotivo = new JTextField();
        panelForm.add(txtMotivo);

        panelForm.add(new JLabel("Descripción Detallada:"));
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        panelForm.add(new JScrollPane(txtDescripcion));

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnEnviar = new JButton("Enviar Reporte");

        btnCancelar.addActionListener(e -> dispose());
        btnEnviar.addActionListener(e -> enviarReporte());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnEnviar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void enviarReporte() {
        String motivo = txtMotivo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        TipoReporte tipo = (TipoReporte) comboTipo.getSelectedItem();

        if (motivo.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.");
            return;
        }

        if (reporteController.crearReporte(tipo, reportante.getId(), idObjetoReportado, motivo, descripcion) != null) {
            JOptionPane.showMessageDialog(this, "Reporte enviado correctamente. Los administradores lo revisarán.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al enviar el reporte.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
