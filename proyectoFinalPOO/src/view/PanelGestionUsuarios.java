/**
 * Clase: PanelGestionUsuarios
 *  Vista de la interfaz.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.3
 */
package view;

import controller.AdminController;
import model.User;
import util.RolUsuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PanelGestionUsuarios representa la interfaz de administración encargada de gestionar los usuarios del sistema. 
 * Permite:
 * - Listar todos los usuarios registrados.
 * - Eliminar usuarios.
 * - Asignar o remover privilegios de administrador.
 */
public class PanelGestionUsuarios extends JPanel {

    private final AdminController adminController;
    private final User adminUser;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    public PanelGestionUsuarios(AdminController adminController, User adminUser) {
        this.adminController = adminController;
        this.adminUser = adminUser;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        cargarUsuarios();
    }

    private void initUI() {
        // --- TOOLBAR ---
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnEliminar = new JButton("Eliminar Usuario");
        JButton btnHacerAdmin = new JButton("Hacer Admin");
        JButton btnQuitarAdmin = new JButton("Quitar Admin");

        btnRefrescar.addActionListener(e -> cargarUsuarios());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnHacerAdmin.addActionListener(e -> cambiarRol(RolUsuario.ADMIN));
        btnQuitarAdmin.addActionListener(e -> cambiarRol(RolUsuario.USUARIO));

        toolbar.add(btnRefrescar);
        toolbar.addSeparator();
        toolbar.add(btnHacerAdmin);
        toolbar.add(btnQuitarAdmin);
        toolbar.addSeparator();
        toolbar.add(btnEliminar);

        add(toolbar, BorderLayout.NORTH);

        // --- TABLA ---
        String[] columnas = { "ID", "Nombre", "Email", "Rol" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        List<User> usuarios = adminController.listarTodosLosUsuarios(adminUser.getId());

        for (User u : usuarios) {
            Object[] fila = {
                    u.getId(),
                    u.getNombre(),
                    u.getCorreo(),
                    u.getRol()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void eliminarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario.");
            return;
        }

        String idUsuario = (String) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar al usuario '" + nombre + "'?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminController.eliminarUsuario(idUsuario, adminUser.getId())) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarRol(RolUsuario nuevoRol) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario.");
            return;
        }

        String idUsuario = (String) modeloTabla.getValueAt(fila, 0);

        if (adminController.cambiarRolUsuario(idUsuario, nuevoRol, adminUser.getId())) {
            JOptionPane.showMessageDialog(this, "Rol actualizado correctamente.");
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar rol.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
