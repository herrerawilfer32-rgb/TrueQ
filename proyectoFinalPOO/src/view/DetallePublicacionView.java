package view;

import controller.PublicacionController;
import controller.ReporteController;
import model.Publicacion;
import model.PublicacionSubasta;
import model.PublicacionTrueque;
import model.User;
import util.TipoPublicacion;
import util.TipoReporte;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class DetallePublicacionView extends JFrame {

    private final PublicacionController controller;
    private final Publicacion publicacion;
    private final User usuarioActual;
    private final MainWindow mainWindow; // referencia a la ventana principal
    private final ReporteController reporteController;

    public DetallePublicacionView(PublicacionController controller,
            Publicacion publicacion,
            User usuarioActual,
            MainWindow mainWindow,
            ReporteController reporteController) {
        this.controller = controller;
        this.publicacion = publicacion;
        this.usuarioActual = usuarioActual;
        this.mainWindow = mainWindow;
        this.reporteController = reporteController;

        setTitle("Detalle de Publicación: " + publicacion.getTitulo());
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel lblTitulo = new JLabel(publicacion.getTitulo());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelInfo.add(lblTitulo);

        // Descripción
        JTextArea txtDesc = new JTextArea(publicacion.getDescripcion());
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(new JLabel("Descripción:"));
        panelInfo.add(txtDesc);

        // Tipo y Detalles Específicos
        panelInfo.add(Box.createVerticalStrut(10));
        JLabel lblTipo = new JLabel("Tipo: " + publicacion.getTipoPublicacion());
        lblTipo.setFont(new Font("Arial", Font.ITALIC, 14));
        panelInfo.add(lblTipo);

        if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
            PublicacionSubasta subasta = (PublicacionSubasta) publicacion;
            panelInfo.add(new JLabel("Precio Mínimo: $" + subasta.getPrecioMinimo()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            panelInfo.add(new JLabel("Cierra el: " + sdf.format(subasta.getFechaCierre())));
        } else {
            PublicacionTrueque trueque = (PublicacionTrueque) publicacion;
            panelInfo.add(new JLabel("Busca a cambio: " + trueque.getObjetosDeseados()));
        }

        // Imágenes (Lista de rutas por ahora)
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(new JLabel("Imágenes adjuntas: " + publicacion.getFotosPaths().size()));
        for (String path : publicacion.getFotosPaths()) {
            JLabel lblPath = new JLabel(path);
            lblPath.setForeground(Color.GRAY);
            panelInfo.add(lblPath);
        }

        add(new JScrollPane(panelInfo), BorderLayout.CENTER);

        // Botones inferiores
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        if (usuarioActual != null && !publicacion.getIdVendedor().equals(usuarioActual.getId())) {

            JButton btnOfertar = new JButton("Realizar Oferta");
            btnOfertar.setFont(new Font("Arial", Font.BOLD, 16));
            btnOfertar.setBackground(new Color(52, 152, 219));
            btnOfertar.setForeground(Color.WHITE);
            btnOfertar.addActionListener(e -> mostrarDialogoOferta());
            panelBotones.add(btnOfertar);

            JButton btnContactar = new JButton("Contactar vendedor");
            btnContactar.addActionListener(e -> contactarVendedor());
            panelBotones.add(btnContactar);

            JButton btnReportar = new JButton("⚠️ Reportar");
            btnReportar.setBackground(new Color(231, 76, 60));
            btnReportar.setForeground(Color.WHITE);
            btnReportar.addActionListener(e -> reportarPublicacion());
            panelBotones.add(btnReportar);

        } else {
            JLabel lblDueño = new JLabel("Eres el propietario de esta publicación", SwingConstants.CENTER);
            lblDueño.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(lblDueño, BorderLayout.SOUTH);
            return; // No agregar panel de botones si es el dueño
        }

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void mostrarDialogoOferta() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ofertar.");
            return;
        }

        if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
            String montoStr = JOptionPane.showInputDialog(this, "Ingresa tu monto de puja:");
            if (montoStr != null && !montoStr.isEmpty()) {
                try {
                    double monto = Double.parseDouble(montoStr);
                    boolean exito = controller.ofertar(publicacion.getIdArticulo(), usuarioActual.getId(), monto, null,
                            null);
                    if (exito)
                        JOptionPane.showMessageDialog(this, "¡Puja realizada con éxito!");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.");
                }
            }
        } else {
            // Dialogo personalizado para Trueque con imágenes
            JDialog dialog = new JDialog(this, "Realizar Oferta de Trueque", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JPanel panelCentral = new JPanel();
            panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
            panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            panelCentral.add(new JLabel("Describe tu oferta de trueque:"));
            JTextArea txtPropuesta = new JTextArea(5, 20);
            txtPropuesta.setLineWrap(true);
            panelCentral.add(new JScrollPane(txtPropuesta));

            panelCentral.add(Box.createVerticalStrut(10));

            java.util.List<String> rutasImagenes = new java.util.ArrayList<>();
            JLabel lblImagenes = new JLabel("Imágenes adjuntas: 0");
            JButton btnAdjuntar = new JButton("Adjuntar Imagen");

            btnAdjuntar.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    rutasImagenes.add(fileChooser.getSelectedFile().getAbsolutePath());
                    lblImagenes.setText("Imágenes adjuntas: " + rutasImagenes.size());
                }
            });

            JPanel panelImg = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelImg.add(btnAdjuntar);
            panelImg.add(lblImagenes);
            panelCentral.add(panelImg);

            dialog.add(panelCentral, BorderLayout.CENTER);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.addActionListener(e -> dialog.dispose());

            JButton btnEnviar = new JButton("Enviar Oferta");
            btnEnviar.addActionListener(e -> {
                String propuesta = txtPropuesta.getText();
                if (propuesta == null || propuesta.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "La descripción no puede estar vacía.");
                    return;
                }

                boolean exito = controller.ofertar(publicacion.getIdArticulo(), usuarioActual.getId(), 0, propuesta,
                        rutasImagenes);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Propuesta enviada con éxito!");
                    dialog.dispose();
                }
            });

            panelBotones.add(btnCancelar);
            panelBotones.add(btnEnviar);
            dialog.add(panelBotones, BorderLayout.SOUTH);

            dialog.setVisible(true);
        }
    }

    /**
     * Invocado cuando el usuario pulsa "Contactar vendedor".
     * Delegamos en MainWindow la lógica de abrir o crear el chat.
     */
    private void contactarVendedor() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes iniciar sesión para contactar al vendedor.",
                    "Sesión requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (mainWindow == null) {
            JOptionPane.showMessageDialog(this,
                    "No se puede abrir el chat desde esta ventana.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainWindow.abrirChatConVendedor(publicacion);
        dispose(); // Cerramos el detalle y dejamos al usuario en la pestaña de chats
    }

    private void reportarPublicacion() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión para reportar.");
            return;
        }

        new CrearReporteDialog(this, reporteController, usuarioActual,
                publicacion.getIdArticulo(), TipoReporte.PUBLICACION)
                .setVisible(true);
    }
}
