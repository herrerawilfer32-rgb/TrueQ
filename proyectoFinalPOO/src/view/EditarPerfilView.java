/**
 * Clase: EditarPerfilView
 * Vista de la interfaz.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
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

        setSize(500, 700); // Aumentado de 600 a 700 para mostrar todo el contenido
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        // Encabezado
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(util.UIConstants.MORADO_PRINCIPAL);
        panelEncabezado.setBorder(util.UIConstants.BORDE_VACIO_20);

        JLabel lblTitulo = new JLabel("✏️ Editar Mi Perfil");
        lblTitulo.setFont(util.UIConstants.FUENTE_TITULO);
        lblTitulo.setForeground(util.UIConstants.DORADO);
        panelEncabezado.add(lblTitulo);

        add(panelEncabezado, BorderLayout.NORTH);

        // Panel principal SIN scroll
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelPrincipal.setBackground(util.UIConstants.BLANCO);

        // Información no editable
        panelPrincipal.add(crearSeccion("📋 Información de la Cuenta (No Editable)"));
        panelPrincipal.add(crearCampoSoloLectura("Nombre de Usuario:", usuario.getNombreUsuario()));
        panelPrincipal.add(crearCampoSoloLectura("Cédula/ID:", usuario.getId()));

        String reputacionTexto = usuario.getNumeroCalificaciones() == 0
                ? "Sin calificaciones"
                : String.format("%.1f ⭐ (%d calificaciones)", usuario.getReputacion(),
                        usuario.getNumeroCalificaciones());
        panelPrincipal.add(crearCampoSoloLectura("Reputación:", reputacionTexto));
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Información personal editable
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

        // Cambiar contraseña
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

        // Agregar panel principal SIN JScrollPane
        add(panelPrincipal, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(util.UIConstants.MORADO_PRINCIPAL);

        JButton btnGuardar = new JButton("💾 Guardar Cambios");
        btnGuardar.setBackground(util.UIConstants.VERDE_EXITO);
        btnGuardar.setForeground(util.UIConstants.BLANCO);
        btnGuardar.setFont(util.UIConstants.FUENTE_BOTON);
        btnGuardar.addActionListener(e -> guardarCambios());

        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setBackground(util.UIConstants.GRIS_NEUTRAL);
        btnCancelar.setForeground(util.UIConstants.NEGRO);
        btnCancelar.setFont(util.UIConstants.FUENTE_BOTON);
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JLabel crearSeccion(String titulo) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(util.UIConstants.FUENTE_SUBTITULO);
        lbl.setForeground(util.UIConstants.MORADO_SECUNDARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearCampoSoloLectura(String etiqueta, String valor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(util.UIConstants.BLANCO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(util.UIConstants.FUENTE_NORMAL);
        lbl.setPreferredSize(new Dimension(150, 25));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(util.UIConstants.FUENTE_NORMAL);
        lblValor.setForeground(Color.GRAY);

        panel.add(lbl);
        panel.add(lblValor);

        return panel;
    }

    private JPanel crearCampoEditable(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(util.UIConstants.BLANCO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(util.UIConstants.FUENTE_NORMAL);
        lbl.setPreferredSize(new Dimension(150, 25));

        campo.setPreferredSize(new Dimension(250, 30));
        // Fondo gris claro para distinguir campos editables
        campo.setBackground(new Color(245, 245, 245));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

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
