package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class PerfilUsuarioView extends JDialog {

    public PerfilUsuarioView(Frame parent, User usuario, UserController userController, boolean puedeCalificar,
            String idPublicacion, User usuarioCalificador) {
        super(parent, "Perfil de Usuario", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelInfo.setBackground(new Color(250, 250, 250));

        // Avatar
        JLabel lblAvatar = new JLabel("👤", SwingConstants.CENTER);
        lblAvatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nombre
        JLabel lblNombre = new JLabel(usuario.getNombre() + " " + usuario.getApellido());
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ubicación
        JLabel lblUbicacion = new JLabel("📍 " + usuario.getUbicacion());
        lblUbicacion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblUbicacion.setForeground(Color.GRAY);
        lblUbicacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Reputación
        JPanel panelReputacion = new JPanel();
        panelReputacion.setOpaque(false);

        JLabel lblEstrella = new JLabel("⭐");
        lblEstrella.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblEstrella.setForeground(new Color(255, 193, 7)); // Dorado

        String reputacionTexto;
        if (usuario.getNumeroCalificaciones() == 0) {
            reputacionTexto = "Sin calificaciones";
        } else {
            reputacionTexto = String.format("%.1f (%d)", usuario.getReputacion(), usuario.getNumeroCalificaciones());
        }
        JLabel lblPuntaje = new JLabel(reputacionTexto);
        lblPuntaje.setFont(new Font("SansSerif", Font.BOLD, 18));

        panelReputacion.add(lblEstrella);
        panelReputacion.add(lblPuntaje);
        panelReputacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfo.add(lblAvatar);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(lblNombre);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblUbicacion);
        panelInfo.add(Box.createVerticalStrut(15));
        panelInfo.add(panelReputacion);

        add(panelInfo, BorderLayout.CENTER);

        // Botón Calificar
        if (puedeCalificar) {
            JPanel panelBoton = new JPanel();
            JButton btnCalificar = new JButton("Calificar Usuario");
            btnCalificar.setBackground(new Color(0, 123, 255));
            btnCalificar.setForeground(Color.WHITE);
            btnCalificar.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(this, "Califica del 1 al 5:", "Calificar",
                        JOptionPane.QUESTION_MESSAGE);
                try {
                    int estrellas = Integer.parseInt(input);
                    if (estrellas < 1 || estrellas > 5) {
                        JOptionPane.showMessageDialog(this, "Por favor ingresa un número entre 1 y 5.");
                    } else {
                        // Pasar contexto de publicación y calificador
                        String idCalificador = (usuarioCalificador != null) ? usuarioCalificador.getId() : null;
                        if (userController.calificarUsuario(usuario.getId(), estrellas, idPublicacion, idCalificador)) {
                            JOptionPane.showMessageDialog(this, "¡Calificación enviada!");
                            dispose();
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Entrada inválida.");
                }
            });
            panelBoton.add(btnCalificar);
            add(panelBoton, BorderLayout.SOUTH);
        }
    }

    // Constructor de compatibilidad (o para solo ver perfil)
    public PerfilUsuarioView(Frame parent, User usuario, UserController userController, boolean puedeCalificar) {
        this(parent, usuario, userController, puedeCalificar, null, null);
    }
}
