/**
 * Clase: LoginWindow
 * Ventana de inicio de sesión.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */
package view;

import controller.AuthController;
import model.User;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión de usuarios, esta permite ingresar nombre de usuario y contraseña, valida los datos y notifica a la ventana principal cuando el usuario inicia sesión correctamente.
 */
public class LoginWindow extends JFrame {

    private final AuthController authController;
    private final MainWindow mainWindow; // Referencia a la ventana principal

    private JTextField campoUsuario;
    private JPasswordField campoContraseña;

    // Constructor modificado: recibe MainWindow
    
    /**
     * Constructor de la ventana de login.
     *
     * @param authController controlador de autenticación
     * @param mainWindow     ventana principal que será notificada al iniciar sesión
     */
    public LoginWindow(AuthController authController, MainWindow mainWindow) {
        this.authController = authController;
        this.mainWindow = mainWindow;

        setTitle("Acceso de Usuario");
        setSize(400, 250);
        // DISPOSE_ON_CLOSE: Solo cierra esta ventanita, no toda la app
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(mainWindow); // Aparece centrada sobre la principal

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Encabezado
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(util.UIConstants.MORADO_PRINCIPAL);
        panelEncabezado.setBorder(util.UIConstants.BORDE_VACIO_20);

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(util.UIConstants.FUENTE_TITULO);
        lblTitulo.setForeground(util.UIConstants.DORADO);
        panelEncabezado.add(lblTitulo);

        add(panelEncabezado, BorderLayout.NORTH);

        // Panel Central (Formulario)
        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFormulario.setBackground(util.UIConstants.BLANCO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        campoUsuario = new JTextField();
        campoContraseña = new JPasswordField();

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(util.UIConstants.FUENTE_NORMAL);

        JLabel lblContraseña = new JLabel("Contraseña:");
        lblContraseña.setFont(util.UIConstants.FUENTE_NORMAL);

        panelFormulario.add(lblUsuario);
        panelFormulario.add(campoUsuario);
        panelFormulario.add(lblContraseña);
        panelFormulario.add(campoContraseña);

        add(panelFormulario, BorderLayout.CENTER);

        // Panel Inferior (Botones)
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(util.UIConstants.BLANCO);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(util.UIConstants.VERDE_EXITO);
        btnEntrar.setForeground(util.UIConstants.BLANCO);
        btnEntrar.setFont(util.UIConstants.FUENTE_BOTON);

        JButton btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.setBackground(util.UIConstants.MORADO_SECUNDARIO);
        btnRegistrarse.setForeground(util.UIConstants.DORADO);
        btnRegistrarse.setFont(util.UIConstants.FUENTE_BOTON);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(util.UIConstants.GRIS_NEUTRAL);
        btnCancelar.setForeground(util.UIConstants.NEGRO);
        btnCancelar.setFont(util.UIConstants.FUENTE_BOTON);

        btnEntrar.addActionListener(e -> handleLogin());
        btnRegistrarse.addActionListener(e -> openRegisterWindow());
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnEntrar);
        panelBotones.add(btnRegistrarse);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String user = campoUsuario.getText().trim();
        String pass = new String(campoContraseña.getPassword()).trim();

        // Validar campos
        String errorMessage = validarCamposLogin(user, pass);
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User usuario = authController.manejarLogin(user, pass);

        if (usuario != null) {
            // Avisar a la ventana principal
            mainWindow.setUsuarioLogueado(usuario);

            // Mostrar mensaje de bienvenida con tema personalizado
            mostrarMensajeExito("¡Bienvenido de nuevo, " + usuario.getNombre() + "!");

            // Cerrar ventana de login
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de Autenticación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Valida los campos del formulario de login.
     * 
     * @return mensaje de error si hay problemas, null si todo está bien
     */
    private String validarCamposLogin(String user, String pass) {
        if (!util.ValidationUtils.isNotEmpty(user)) {
            return "Por favor ingrese su usuario";
        }

        if (!util.ValidationUtils.isNotEmpty(pass)) {
            return "Por favor ingrese su contraseña";
        }

        if (!util.ValidationUtils.hasMinLength(user, 3)) {
            return "El usuario debe tener al menos 3 caracteres";
        }

        if (!util.ValidationUtils.hasMinLength(pass, 4)) {
            return "La contraseña debe tener al menos 4 caracteres";
        }

        return null; // Todo válido
    }

    private void openRegisterWindow() {
        new RegisterWindow(authController, this);
    }

    // Método para mostrar mensajes de éxito con tema personalizado
    private void mostrarMensajeExito(String mensaje) {
        JDialog dialogo = new JDialog(this, "Éxito", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.setSize(350, 150);
        dialogo.setLocationRelativeTo(this);

        // Panel con mensaje
        JPanel panelMensaje = new JPanel();
        panelMensaje.setBackground(util.UIConstants.VERDE_AZULADO);
        panelMensaje.setBorder(util.UIConstants.BORDE_VACIO_20);

        JLabel lblMensaje = new JLabel(mensaje);
        lblMensaje.setFont(util.UIConstants.FUENTE_SUBTITULO);
        lblMensaje.setForeground(util.UIConstants.BLANCO);
        panelMensaje.add(lblMensaje);

        dialogo.add(panelMensaje, BorderLayout.CENTER);

        // Botón OK
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(util.UIConstants.BLANCO);
        JButton btnOk = new JButton("OK");
        btnOk.setBackground(util.UIConstants.VERDE_EXITO);
        btnOk.setForeground(util.UIConstants.BLANCO);
        btnOk.setFont(util.UIConstants.FUENTE_BOTON);
        btnOk.addActionListener(e -> dialogo.dispose());
        panelBoton.add(btnOk);

        dialogo.add(panelBoton, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
}
