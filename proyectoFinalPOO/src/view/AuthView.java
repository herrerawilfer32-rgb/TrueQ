/*
 * Clase: AuthView
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Para que el Controller pueda "escuchar" los botones

public class AuthView extends JFrame { // JFrame para ser una ventana independiente

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel; // Para mostrar mensajes de éxito/error

    public AuthView() {
        setTitle("Sistema de Trueque y Subasta - Autenticación");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana
        setLayout(new BorderLayout());

        // Panel para el formulario
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Filas, columnas, hgap, vgap
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Usuario:"));
        usernameField = new JTextField(20);
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Contraseña:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField);
        
        // Panel para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginButton = new JButton("Iniciar Sesión");
        registerButton = new JButton("Registrarse");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED); // Inicialmente rojo para errores

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(messageLabel, BorderLayout.NORTH); // Mensajes en la parte superior
    }

    // --- Métodos para que el Controller obtenga la entrada ---
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // --- Métodos para que el Controller muestre mensajes ---
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
    }
    
    // --- Métodos para que el Controller registre acciones ---
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addRegisterListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
}