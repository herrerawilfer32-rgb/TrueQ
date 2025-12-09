package view;

import controller.PublicacionController;
import controller.ReporteController;
import model.Publicacion;
import model.PublicacionSubasta;
import model.PublicacionTrueque;
import model.User;
import model.Oferta;
import util.TipoPublicacion;
import util.TipoReporte;
import util.EstadoPublicacion;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetallePublicacionView extends JFrame {

    private final PublicacionController controller;
    private final Publicacion publicacion;
    private final User usuarioActual;
    private final MainWindow mainWindow;
    private final ReporteController reporteController;

    // Elementos específicos para subasta
    private PublicacionSubasta publicacionSubasta;
    private JLabel lblPujaActual;
    private JLabel lblIncrementoSugerido;
    private JPanel panelHistorialPujas;
    private final NumberFormat formatoMoneda;

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

        // Formato "#.###,##"
        formatoMoneda = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        formatoMoneda.setMinimumFractionDigits(2);
        formatoMoneda.setMaximumFractionDigits(2);

        setTitle("Detalle de Publicación: " + publicacion.getTitulo());
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {

        // ===== BARRA SUPERIOR INTERNA CON BOTÓN "X" =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        headerPanel.setBackground(new Color(245, 245, 245));

        JButton btnCerrarSuperior = new JButton("X");
        btnCerrarSuperior.setMargin(new Insets(2, 6, 2, 6));
        btnCerrarSuperior.setFocusable(false);
        btnCerrarSuperior.setBackground(new Color(192, 57, 43));
        btnCerrarSuperior.setForeground(Color.WHITE);
        btnCerrarSuperior.addActionListener(e -> dispose());

        headerPanel.add(btnCerrarSuperior, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== PANEL CENTRAL CON LA INFORMACIÓN =====
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

        // Tipo
        panelInfo.add(Box.createVerticalStrut(10));
        JLabel lblTipo = new JLabel("Tipo: " + publicacion.getTipoPublicacion());
        lblTipo.setFont(new Font("Arial", Font.ITALIC, 14));
        panelInfo.add(lblTipo);

        // Detalle específico según tipo
        if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
            configurarSeccionSubasta(panelInfo);
        } else {
            configurarSeccionTrueque(panelInfo);
        }
        
     // MENSAJE: "La subasta ha cerrado" para NO dueños en subasta cerrada
        if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA
                && publicacion.getEstado() != EstadoPublicacion.ACTIVA
                && (usuarioActual == null || !publicacion.getIdVendedor().equals(usuarioActual.getId()))) {
            JLabel lblCerrada = new JLabel("La subasta ha cerrado");
            lblCerrada.setForeground(Color.RED);
            lblCerrada.setFont(lblCerrada.getFont().deriveFont(Font.BOLD));
            panelInfo.add(Box.createVerticalStrut(5));
            panelInfo.add(lblCerrada);
        }

        // Imágenes (solo rutas por ahora)
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(new JLabel("Imágenes adjuntas: " + publicacion.getFotosPaths().size()));
        for (String path : publicacion.getFotosPaths()) {
            JLabel lblPath = new JLabel(path);
            lblPath.setForeground(Color.GRAY);
            panelInfo.add(lblPath);
        }

        add(new JScrollPane(panelInfo), BorderLayout.CENTER);

        // ===== BOTONES INFERIORES (SE MANTIENEN IGUAL, SIN "CERRAR") =====
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        if (usuarioActual != null && !publicacion.getIdVendedor().equals(usuarioActual.getId())) {

            JButton btnOfertar = new JButton("Realizar Oferta");
            btnOfertar.setFont(new Font("Arial", Font.BOLD, 16));
            btnOfertar.setBackground(new Color(52, 152, 219));
            btnOfertar.setForeground(Color.WHITE);
            btnOfertar.addActionListener(e -> mostrarDialogoOferta());
            panelBotones.add(btnOfertar);

            // Botón de puja rápida solo en subastas
            if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
                JButton btnPujaRapida = new JButton("Subir puja +10% inicial");
                btnPujaRapida.setBackground(new Color(39, 174, 96));
                btnPujaRapida.setForeground(Color.WHITE);
                btnPujaRapida.addActionListener(e -> realizarPujaRapida());
                panelBotones.add(btnPujaRapida);
            }

            JButton btnContactar = new JButton("Contactar vendedor");
            btnContactar.addActionListener(e -> contactarVendedor());
            panelBotones.add(btnContactar);

            JButton btnReportar = new JButton("⚠️ Reportar");
            btnReportar.setBackground(new Color(231, 76, 60));
            btnReportar.setForeground(Color.WHITE);
            btnReportar.addActionListener(e -> reportarPublicacion());
            panelBotones.add(btnReportar);

            // ⛔ Ya NO se añade el botón "Cerrar" aquí

            add(panelBotones, BorderLayout.SOUTH);

        } else {
            // Dueño de la publicación: solo mensaje informativo abajo.
            JLabel lblDueño = new JLabel("Eres el propietario de esta publicación", SwingConstants.CENTER);
            lblDueño.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(lblDueño, BorderLayout.SOUTH);
            // El cierre lo hace la "X" superior
        }
    }

    // ============= SECCIÓN SUBASTA =============

    private void configurarSeccionSubasta(JPanel panelInfo) {
        this.publicacionSubasta = (PublicacionSubasta) publicacion;

        panelInfo.add(Box.createVerticalStrut(10));

        double precioMinimo = publicacionSubasta.getPrecioMinimo();
        JLabel lblPrecioMinimo = new JLabel("Precio mínimo inicial: $ " + formatoMoneda.format(precioMinimo));
        panelInfo.add(lblPrecioMinimo);

        // Puja actual obtenida desde el controlador
        lblPujaActual = new JLabel();
        refrescarPujaActual();
        panelInfo.add(lblPujaActual);

        // Incremento rápido (10%) obtenido desde el controlador
        double incremento = controller.calcularIncrementoRapidoSubasta(publicacionSubasta);
        lblIncrementoSugerido = new JLabel("Incremento rápido (+10% inicial): $ " + formatoMoneda.format(incremento));
        panelInfo.add(lblIncrementoSugerido);

        // Fecha de cierre
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        panelInfo.add(new JLabel("Cierra el: " + sdf.format(publicacionSubasta.getFechaCierre())));

        // Historial de pujas
        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(new JLabel("Historial de pujas:"));

        panelHistorialPujas = new JPanel();
        panelHistorialPujas.setLayout(new BoxLayout(panelHistorialPujas, BoxLayout.Y_AXIS));
        panelHistorialPujas.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelInfo.add(panelHistorialPujas);

        cargarHistorialPujas();
    }

    private void refrescarPujaActual() {
        if (publicacionSubasta == null || lblPujaActual == null) {
            return;
        }
        double pujaActual = controller.obtenerPujaActualSubasta(publicacionSubasta);
        lblPujaActual.setText("Puja actual: $ " + formatoMoneda.format(pujaActual));
    }

    private void cargarHistorialPujas() {
        if (panelHistorialPujas == null) {
            return;
        }

        panelHistorialPujas.removeAll();

        List<Oferta> ofertas = controller.obtenerOfertas(publicacion.getIdArticulo());
        if (ofertas == null || ofertas.isEmpty()) {
            JLabel lblSinOfertas = new JLabel("No hay pujas registradas aún.");
            lblSinOfertas.setForeground(Color.GRAY);
            panelHistorialPujas.add(lblSinOfertas);
        } else {
            for (Oferta oferta : ofertas) {
                String texto = "• Ofertante: " + oferta.getIdOfertante()
                        + " | Monto: $ " + formatoMoneda.format(oferta.getMontoOferta());
                JLabel lblOferta = new JLabel(texto);
                panelHistorialPujas.add(lblOferta);
            }
        }

        panelHistorialPujas.revalidate();
        panelHistorialPujas.repaint();
    }

    private void realizarPujaRapida() {
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ofertar.");
            return;
        }
        if (publicacionSubasta == null) {
            return;
        }

        double pujaActual = controller.obtenerPujaActualSubasta(publicacionSubasta);
        double incremento = controller.calcularIncrementoRapidoSubasta(publicacionSubasta);
        double nuevoMonto = pujaActual + incremento;

        int opt = JOptionPane.showConfirmDialog(
                this,
                "Se ofertará automáticamente: $ " + formatoMoneda.format(nuevoMonto)
                        + "\n(Incremento de +10% sobre la puja inicial)",
                "Confirmar puja rápida",
                JOptionPane.YES_NO_OPTION);

        if (opt != JOptionPane.YES_OPTION) {
            return;
        }

        boolean exito;
        try {
            exito = controller.ofertar(
                    publicacion.getIdArticulo(),
                    usuarioActual.getId(),
                    nuevoMonto,
                    null,
                    null
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar la puja rápida: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "¡Puja rápida realizada con éxito!");
            // Refrescar UI
            refrescarPujaActual();
            cargarHistorialPujas();
        }
    }

    // ============= TRUEQUE =============

    private void configurarSeccionTrueque(JPanel panelInfo) {
        PublicacionTrueque trueque = (PublicacionTrueque) publicacion;
        panelInfo.add(new JLabel("Busca a cambio: " + trueque.getObjetosDeseados()));
    }

    // ============= DIÁLOGO DE OFERTAS =============

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
                    boolean exito = controller.ofertar(
                            publicacion.getIdArticulo(),
                            usuarioActual.getId(),
                            monto,
                            null,
                            null
                    );
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "¡Puja realizada con éxito!");
                        refrescarPujaActual();
                        cargarHistorialPujas();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Monto inválido.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al ofertar: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // TRUEQUE: diálogo con descripción + imágenes
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

                boolean exito = controller.ofertar(
                        publicacion.getIdArticulo(),
                        usuarioActual.getId(),
                        0,
                        propuesta,
                        rutasImagenes
                );
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

    // ============= CHAT Y REPORTE =============

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
        dispose();
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
