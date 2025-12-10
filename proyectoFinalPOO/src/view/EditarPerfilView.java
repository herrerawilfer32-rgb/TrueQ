/*
 * Clase: EditarPerfilView
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.UserController;
import model.User;
import util.ValidationUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Vista para editar el perfil del usuario actual.
 * Permite modificar: nombre, apellido, email, ubicación y contraseña.
 * NO permite modificar: username, ID, rol, reputación (por seguridad).
 */
public class EditarPerfilView extends JDialog {

    private User usuario;
    private UserController userController;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtUbicacion;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;

    public EditarPerfilView(Frame parent, User usuario, UserController userController) {
        super(parent, "Editar Mi Perfil", true);
        this.usuario = usuario;
        this.userController = userController;

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(Color.WHITE);

        // Título
        JLabel lblTitulo = new JLabel("✏️ Editar Perfil");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Sección: Información no editable
        panelPrincipal.add(crearSeccion("📋 Información de la Cuenta (No Editable)"));
        panelPrincipal.add(crearCampoSoloLectura("Nombre de Usuario:", usuario.getNombreUsuario()));
        panelPrincipal.add(crearCampoSoloLectura("Cédula/ID:", usuario.getId()));

        String reputacionTexto = usuario.getNumeroCalificaciones() == 0
                ? "Sin calificaciones"
                : String.format("%.1f ⭐ (%d calificaciones)", usuario.getReputacion(),
                        usuario.getNumeroCalificaciones());
        panelPrincipal.add(crearCampoSoloLectura("Reputación:", reputacionTexto));
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Sección: Información personal editable
        panelPrincipal.add(crearSeccion("👤 Información Personal"));

        txtNombre = new JTextField(usuario.getNombre() != null ? usuario.getNombre() : "", 20);
        panelPrincipal.add(crearCampoEditable("Nombre:", txtNombre));

        txtApellido = new JTextField(usuario.getApellido() != null ? usuario.getApellido() : "", 20);
        panelPrincipal.add(crearCampoEditable("Apellido:", txtApellido));

        txtEmail = new JTextField(usuario.getEmail() != null ? usuario.getEmail() : "", 20);
        panelPrincipal.add(crearCampoEditable("Email:", txtEmail));

        txtUbicacion = new JTextField(usuario.getUbicacion() != null ? usuario.getUbicacion() : "", 20);
        panelPrincipal.add(crearCampoEditable("Ubicación:", txtUbicacion));

        panelPrincipal.add(Box.createVerticalStrut(20));

        // Sección: Cambiar contraseña
        panelPrincipal.add(crearSeccion("🔒 Cambiar Contraseña (Opcional)"));

        JLabel lblInfo = new JLabel("Deja en blanco si no deseas cambiar la contraseña");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelPrincipal.add(lblInfo);
        panelPrincipal.add(Box.createVerticalStrut(10));

        txtNuevaPassword = new JPasswordField(20);
        panelPrincipal.add(crearCampoEditable("Nueva Contraseña:", txtNuevaPassword));

        txtConfirmarPassword = new JPasswordField(20);
        panelPrincipal.add(crearCampoEditable("Confirmar Contraseña:", txtConfirmarPassword));

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(new Color(240, 240, 240));

        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnGuardar.addActionListener(e -> guardarCambios());

        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JLabel crearSeccion(String titulo) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setForeground(new Color(52, 73, 94));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearCampoSoloLectura(String etiqueta, String valor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(150, 25));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblValor.setForeground(Color.GRAY);

        panel.add(lbl);
        panel.add(lblValor);

        return panel;
    }

    private JPanel crearCampoEditable(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(150, 25));

        campo.setPreferredSize(new Dimension(250, 30));

        panel.add(lbl);
        panel.add(campo);

        return panel;
    }

    private void guardarCambios() {
        // Validar campos
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        String nuevaPassword = new String(txtNuevaPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El nombre y apellido son obligatorios.",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (email.isEmpty() || !ValidationUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingresa un email válido.",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ubicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La ubicación es obligatoria.",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar contraseña si se está cambiando
        if (!nuevaPassword.isEmpty() || !confirmarPassword.isEmpty()) {
            if (!nuevaPassword.equals(confirmarPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Las contraseñas no coinciden.",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nuevaPassword.length() < 4) {
                JOptionPane.showMessageDialog(this,
                        "La contraseña debe tener al menos 4 caracteres.",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Actualizar usuario
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setUbicacion(ubicacion);

        // Cambiar contraseña solo si se ingresó una nueva
        if (!nuevaPassword.isEmpty()) {
            usuario.setPassword(nuevaPassword);
        }

        // Guardar en el repositorio
        boolean exito = userController.actualizarPerfil(usuario);

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "✅ Perfil actualizado exitosamente!",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Error al actualizar el perfil. Intenta nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
