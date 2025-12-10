/**
 * Clase: MenuPrincipalView
 * Vista de la interfaz.
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vista del menú principal del sistema de Trueque y Subasta.
 * Esta clase corresponde únicamente a la interfaz gráfica y muestra opciones principales para que el usuario navegue en el sistema.
 */
public class MenuPrincipalView extends JFrame {

    private JButton viewPublicationsButton;
    private JButton createPublicationButton;
    private JButton viewMyOffersButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    /**
     * Constructor que inicializa la ventana principal del menú.
     * Configura el tamaño, título, layout y los componentes gráficos.
     */
    public MenuPrincipalView() {
        setTitle("Menú Principal - Sistema de Trueque y Subasta");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Bienvenido, usuario!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // 4 botones, 1 columna
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        viewPublicationsButton = new JButton("1. Ver Publicaciones Activas");
        createPublicationButton = new JButton("2. Crear Nueva Publicación");
        viewMyOffersButton = new JButton("3. Ver mis Ofertas Recibidas");
        logoutButton = new JButton("4. Cerrar Sesión");

        buttonPanel.add(viewPublicationsButton);
        buttonPanel.add(createPublicationButton);
        buttonPanel.add(viewMyOffersButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    // --- Métodos para que el Controller configure el mensaje de bienvenida ---
    
    /**
     * Cambia el mensaje de bienvenida y muestra el nombre del usuario.
     *
     * @param username nombre del usuario autenticado
     */
    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Bienvenido, " + username + "!");
    }

    // --- Métodos para que el Controller registre acciones de los botones ---
    
     /**
     * Registra un listener para el botón "Ver Publicaciones Activas".
     *
     * @param listener acción que ejecutará el controlador
     */
    public void addViewPublicationsListener(ActionListener listener) {
        viewPublicationsButton.addActionListener(listener);
    }

    public void addCreatePublicationListener(ActionListener listener) {
        createPublicationButton.addActionListener(listener);
    }

    public void addViewMyOffersListener(ActionListener listener) {
        viewMyOffersButton.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}
