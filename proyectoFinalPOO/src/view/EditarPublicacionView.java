/*
 * Clase: EditarPublicacionView
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.PublicacionController;
import model.Publicacion;
import model.User;

import javax.swing.*;
import java.awt.*;

public class EditarPublicacionView extends JFrame {

    private final PublicacionController pubController;
    private final User usuario;
    private final MainWindow mainWindow;
    private final Publicacion publicacion;

    private JTextField txtTitulo;
    private JTextArea txtDescripcion;

    public EditarPublicacionView(PublicacionController pubController, User usuario, MainWindow mainWindow,
            Publicacion publicacion) {
        this.pubController = pubController;
        this.usuario = usuario;
        this.mainWindow = mainWindow;
        this.publicacion = publicacion;

        setTitle("Editar Publicación");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(mainWindow);
        getContentPane().setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(new Color(106, 153, 149));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Título:");
        label.setBackground(new Color(106, 153, 149));
        formPanel.add(label);
        txtTitulo = new JTextField(publicacion.getTitulo());
        txtTitulo.setBackground(new Color(206, 244, 253));
        formPanel.add(txtTitulo);

        JLabel label_1 = new JLabel("Descripción:");
        label_1.setBackground(new Color(106, 153, 149));
        formPanel.add(label_1);
        txtDescripcion = new JTextArea(publicacion.getDescripcion());
        txtDescripcion.setBackground(new Color(206, 244, 253));
        txtDescripcion.setLineWrap(true);
        formPanel.add(new JScrollPane(txtDescripcion));

        getContentPane().add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(106, 153, 149));
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(29, 145, 169));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(29, 145, 169));

        btnGuardar.addActionListener(e -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void guardarCambios() {
        String nuevoTitulo = txtTitulo.getText();
        String nuevaDesc = txtDescripcion.getText();

        if (nuevoTitulo.isEmpty() || nuevaDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los campos no pueden estar vacíos.");
            return;
        }

        // Actualizamos el objeto localmente
        publicacion.setTitulo(nuevoTitulo);
        publicacion.setDescripcion(nuevaDesc);

        // Enviamos al controlador para persistir
        boolean exito = pubController.actualizarPublicacion(publicacion, usuario.getId());

        if (exito) {
            JOptionPane.showMessageDialog(this, "Publicación actualizada.");
            mainWindow.cargarPublicaciones(); // Refrescar lista principal
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
