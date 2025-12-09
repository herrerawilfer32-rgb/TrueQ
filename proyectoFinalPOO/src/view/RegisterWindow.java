package view;

import controller.AuthController;
import javax.swing.*;
import java.awt.*;

public class RegisterWindow extends JFrame {

    private final AuthController authController;

    private JTextField txtId;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtPasswordConfirm;
    private JTextField txtCorreo;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCiudad;

    public RegisterWindow(AuthController authController, LoginWindow loginWindow) {
        this.authController = authController;

        setTitle("Registro de Usuario");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(loginWindow);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Cédula (ID):*"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Usuario:*"));
        txtUsername = new JTextField();
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Contraseña:*"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Confirmar Contraseña:*"));
        txtPasswordConfirm = new JPasswordField();
        formPanel.add(txtPasswordConfirm);

        formPanel.add(new JLabel("Correo:*"));
        txtCorreo = new JTextField();
        formPanel.add(txtCorreo);

        formPanel.add(new JLabel("Nombre:*"));
        txtNombre = new JTextField();
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Apellido:*"));
        txtApellido = new JTextField();
        formPanel.add(txtApellido);

        formPanel.add(new JLabel("Ciudad:*"));
        txtCiudad = new JTextField();
        formPanel.add(txtCiudad);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRegister = new JButton("Registrarse");
        JButton btnCancel = new JButton("Cancelar");

        btnRegister.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleRegister() {
        String id = txtId.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String passwordConfirm = new String(txtPasswordConfirm.getPassword()).trim();
        String correo = txtCorreo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String ciudad = txtCiudad.getText().trim();

        // Validar campos
        String errorMessage = validarCamposRegistro(id, username, password, passwordConfirm,
                correo, nombre, apellido, ciudad);
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            authController.manejarRegistro(id, username, password, correo, nombre, apellido, ciudad);
            JOptionPane.showMessageDialog(this, "¡Registro exitoso! Ahora puedes iniciar sesión.");
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida todos los campos del formulario de registro.
     * 
     * @return mensaje de error si hay problemas, null si todo está bien
     */
    private String validarCamposRegistro(String id, String username, String password,
            String passwordConfirm, String correo,
            String nombre, String apellido, String ciudad) {
        // Validar cédula
        if (!util.ValidationUtils.isNotEmpty(id)) {
            return "La cédula es obligatoria";
        }
        if (!util.ValidationUtils.isValidCedula(id)) {
            return "La cédula debe contener solo números y tener entre 6 y 15 dígitos";
        }

        // Validar usuario
        if (!util.ValidationUtils.isNotEmpty(username)) {
            return "El usuario es obligatorio";
        }
        if (!util.ValidationUtils.isLengthInRange(username, 3, 20)) {
            return "El usuario debe tener entre 3 y 20 caracteres";
        }
        if (!util.ValidationUtils.isAlphanumeric(username)) {
            return "El usuario solo puede contener letras, números y guión bajo";
        }

        // Validar contraseña
        if (!util.ValidationUtils.isNotEmpty(password)) {
            return "La contraseña es obligatoria";
        }
        if (!util.ValidationUtils.isValidPassword(password)) {
            return "La contraseña debe tener al menos 6 caracteres, incluyendo letras y números";
        }

        // Validar confirmación de contraseña
        if (!password.equals(passwordConfirm)) {
            return "Las contraseñas no coinciden";
        }

        // Validar correo
        if (!util.ValidationUtils.isNotEmpty(correo)) {
            return "El correo es obligatorio";
        }
        if (!util.ValidationUtils.isValidEmail(correo)) {
            return "El correo debe tener un formato válido (ejemplo@dominio.com)";
        }

        // Validar nombre
        if (!util.ValidationUtils.isNotEmpty(nombre)) {
            return "El nombre es obligatorio";
        }
        if (!util.ValidationUtils.hasMinLength(nombre, 2)) {
            return "El nombre debe tener al menos 2 caracteres";
        }
        if (!util.ValidationUtils.isAlphabetic(nombre)) {
            return "El nombre solo puede contener letras y espacios";
        }

        // Validar apellido
        if (!util.ValidationUtils.isNotEmpty(apellido)) {
            return "El apellido es obligatorio";
        }
        if (!util.ValidationUtils.hasMinLength(apellido, 2)) {
            return "El apellido debe tener al menos 2 caracteres";
        }
        if (!util.ValidationUtils.isAlphabetic(apellido)) {
            return "El apellido solo puede contener letras y espacios";
        }

        // Validar ciudad
        if (!util.ValidationUtils.isNotEmpty(ciudad)) {
            return "La ciudad es obligatoria";
        }
        if (!util.ValidationUtils.hasMinLength(ciudad, 3)) {
            return "La ciudad debe tener al menos 3 caracteres";
        }

        return null; // Todo válido
    }
}
