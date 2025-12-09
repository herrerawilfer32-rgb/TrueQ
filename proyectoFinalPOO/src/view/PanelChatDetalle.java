package view;

import controller.ChatController;
import controller.PublicacionController;
import model.User;
import model.chat.Chat;
import model.chat.Mensaje;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel que muestra el detalle de un chat (conversación) y permite
 * enviar nuevos mensajes.
 */
public class PanelChatDetalle extends JPanel {

    // Atributos de negocio
    private ChatController chatController;
    private PublicacionController publicacionController; // NUEVO: para pagos/intercambios
    private User usuarioActual;
    private Chat chatActual;

    // Componentes de la interfaz
    private JTextArea areaConversacion;
    private JTextField campoNuevoMensaje;
    private JButton botonEnviar;

    // Panel para botones especiales (Pagar / Sí / No)
    private JPanel panelAccionesEspeciales;

    // Últimos mensajes especiales detectados
    private Mensaje ultimoMensajePagar;
    private Mensaje ultimoMensajeConfirmarTrueque;

    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    public PanelChatDetalle(ChatController chatController) {
        if (chatController == null) {
            throw new IllegalArgumentException("El controlador de chat no puede ser nulo.");
        }
        this.chatController = chatController;

        inicializarComponentes();
        configurarEventos();
    }

    /**
     * Permite inyectar el PublicacionController para poder
     * realizar acciones como finalizar subasta o concretar intercambio.
     */
    public void setPublicacionController(PublicacionController publicacionController) {
        this.publicacionController = publicacionController;
    }

    public void setUsuarioActual(User usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public void setChatActual(Chat chat) {
        this.chatActual = chat;

        if (chatActual != null) {
            chatController.marcarChatComoLeido(chatActual);
        }

        recargarConversacion();
    }

    public void clearChat() {
        this.chatActual = null;
        areaConversacion.setText("");
        actualizarPanelAccionesEspeciales(null, null);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaConversacion = new JTextArea();
        areaConversacion.setEditable(false);
        areaConversacion.setLineWrap(true);
        areaConversacion.setWrapStyleWord(true);

        JScrollPane scrollConversacion = new JScrollPane(areaConversacion);
        add(scrollConversacion, BorderLayout.CENTER);

        // Panel de acciones especiales (arriba del campo de texto)
        panelAccionesEspeciales = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAccionesEspeciales.setVisible(false);

        // Panel inferior: campo de texto + botón "Enviar"
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        campoNuevoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(botonEnviar);

        panelInferior.add(campoNuevoMensaje, BorderLayout.CENTER);
        panelInferior.add(panelBoton, BorderLayout.EAST);

        // Contenedor SUR: primero botones especiales, luego campo de escribir
        JPanel panelSur = new JPanel(new BorderLayout(5, 5));
        panelSur.add(panelAccionesEspeciales, BorderLayout.NORTH);
        panelSur.add(panelInferior, BorderLayout.SOUTH);

        add(panelSur, BorderLayout.SOUTH);

        // Panel Superior: Botón de Ver Ofertas
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVerOfertas = new JButton("Ver Ofertas Relacionadas");
        btnVerOfertas.addActionListener(e -> mostrarOfertasRelacionadas());
        panelSuperior.add(btnVerOfertas);
        add(panelSuperior, BorderLayout.NORTH);
    }

    private void mostrarOfertasRelacionadas() {
        if (chatActual == null)
            return;

        java.util.List<model.Oferta> ofertas = chatController.obtenerOfertasRelacionadas(chatActual);
        if (ofertas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ofertas relacionadas entre estos usuarios.");
            return;
        }

        JPanel panelOfertas = new JPanel();
        panelOfertas.setLayout(new BoxLayout(panelOfertas, BoxLayout.Y_AXIS));

        for (model.Oferta o : ofertas) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createTitledBorder("Oferta de " + o.getIdOfertante()));

            String info = "<html><b>Descripción:</b> "
                    + (o.getDescripcionTrueque() != null ? o.getDescripcionTrueque() : "Dinero") + "<br/>" +
                    "<b>Monto:</b> " + o.getMontoOferta() + "<br/>" +
                    "<b>Estado:</b> " + o.getEstadoOferta() + "<br/>" +
                    "<b>Fecha:</b> " + o.getFechaOferta() + "</html>";
            card.add(new JLabel(info), BorderLayout.CENTER);

            if (o.getRutasImagenes() != null && !o.getRutasImagenes().isEmpty()) {
                JButton btnVerImagenes = new JButton("Ver Imágenes Adjuntas (" + o.getRutasImagenes().size() + ")");
                btnVerImagenes.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "El usuario ha adjuntado imágenes.\n¿Deseas verlas? (Asegúrate de que no sea contenido inapropiado)",
                            "Confirmación de Seguridad", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        mostrarImagenes(o.getRutasImagenes());
                    }
                });
                card.add(btnVerImagenes, BorderLayout.SOUTH);
            }

            panelOfertas.add(card);
            panelOfertas.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(panelOfertas);
        scroll.setPreferredSize(new java.awt.Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scroll, "Ofertas Relacionadas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarImagenes(java.util.List<String> rutas) {
        JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Imágenes Adjuntas",
                true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panelImg = new JPanel();
        for (String ruta : rutas) {
            ImageIcon icon = new ImageIcon(ruta);
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
            panelImg.add(new JLabel(new ImageIcon(newImg)));
        }

        dialog.add(new JScrollPane(panelImg));
        dialog.setVisible(true);
    }

    private void configurarEventos() {
        botonEnviar.addActionListener(e -> enviarMensajeDesdeCampo());
        campoNuevoMensaje.addActionListener(e -> enviarMensajeDesdeCampo());
    }

    private void enviarMensajeDesdeCampo() {
        if (chatActual == null) {
            return;
        }
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes iniciar sesión para enviar mensajes.",
                    "Sesión requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String texto = campoNuevoMensaje.getText();
        if (texto == null || texto.isBlank()) {
            return;
        }

        chatController.enviarMensaje(chatActual, usuarioActual, texto);
        campoNuevoMensaje.setText("");
        recargarConversacion();
    }

    /**
     * Recarga el área de conversación y detecta mensajes especiales
     * para mostrar botones de acción (pagar / sí-no).
     */
    private void recargarConversacion() {
        areaConversacion.setText("");

        if (chatActual == null) {
            actualizarPanelAccionesEspeciales(null, null);
            return;
        }

        Mensaje msgPagar = null;
        Mensaje msgTrueque = null;

        for (Mensaje mensaje : chatController.obtenerMensajes(chatActual)) {
            String nombre = mensaje.getUsuarioRemitente().getNombre();
            String hora = mensaje.getFechaHoraEnvio().format(formatoHora);
            String contenido = mensaje.getContenidoMensaje();

            areaConversacion.append(nombre + " (" + hora + "): " + contenido + "\n");

            // Detectar mensajes especiales dirigidos al usuarioActual
            if (usuarioActual != null && !mensaje.getUsuarioRemitente().equals(usuarioActual)) {
                if (mensaje.getTipoMensaje() == Mensaje.TipoMensaje.BOTON_PAGAR_SUBASTA) {
                    msgPagar = mensaje;
                } else if (mensaje.getTipoMensaje() == Mensaje.TipoMensaje.BOTON_CONFIRMAR_TRUEQUE) {
                    msgTrueque = mensaje;
                }
            }
        }

        areaConversacion.setCaretPosition(areaConversacion.getDocument().getLength());
        actualizarPanelAccionesEspeciales(msgPagar, msgTrueque);
    }

    /**
     * Actualiza el panel de acciones especiales según los últimos mensajes detectados.
     */
    private void actualizarPanelAccionesEspeciales(Mensaje msgPagar, Mensaje msgTrueque) {
        this.ultimoMensajePagar = msgPagar;
        this.ultimoMensajeConfirmarTrueque = msgTrueque;

        panelAccionesEspeciales.removeAll();
        boolean visible = false;

        if (ultimoMensajePagar != null) {
            JButton btnPagar = new JButton("Pagar subasta");
            btnPagar.addActionListener(e -> manejarPagarSubasta());
            panelAccionesEspeciales.add(btnPagar);
            visible = true;
        }

        if (ultimoMensajeConfirmarTrueque != null) {
            JButton btnSi = new JButton("Sí, continuar con el trato");
            JButton btnNo = new JButton("No, cancelar");

            btnSi.addActionListener(e -> manejarAceptarTrueque());
            btnNo.addActionListener(e -> manejarRechazarTrueque());

            panelAccionesEspeciales.add(btnSi);
            panelAccionesEspeciales.add(btnNo);
            visible = true;
        }

        panelAccionesEspeciales.setVisible(visible);
        panelAccionesEspeciales.revalidate();
        panelAccionesEspeciales.repaint();
    }

    // ================== ACCIONES ESPECIALES ==================

    private void manejarPagarSubasta() {
        if (ultimoMensajePagar == null || publicacionController == null || usuarioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo procesar el pago. Falta información del usuario o publicación.");
            return;
        }

        String idPublicacion = ultimoMensajePagar.getIdPublicacionAsociada();
        if (idPublicacion == null || idPublicacion.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo identificar la subasta asociada al mensaje.");
            return;
        }

        publicacionController.finalizarSubastaConPago(idPublicacion, usuarioActual.getId());
        recargarConversacion();
    }

    private void manejarAceptarTrueque() {
        if (ultimoMensajeConfirmarTrueque == null || publicacionController == null || usuarioActual == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo concretar el intercambio. Falta información del usuario o publicación.");
            return;
        }

        String idPublicacion = ultimoMensajeConfirmarTrueque.getIdPublicacionAsociada();
        if (idPublicacion == null || idPublicacion.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo identificar el trueque asociado al mensaje.");
            return;
        }

        publicacionController.concretarIntercambio(idPublicacion, usuarioActual.getId());
        recargarConversacion();
    }

    private void manejarRechazarTrueque() {
        if (chatActual == null || usuarioActual == null) {
            return;
        }

        chatController.enviarMensaje(chatActual, usuarioActual,
                "Gracias por la oferta, pero no deseo continuar con el trato.");
        JOptionPane.showMessageDialog(this, "Has rechazado el trato.");
        recargarConversacion();
    }
}
