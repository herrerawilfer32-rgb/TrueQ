package view;

import controller.ChatController;
import model.User;
import model.chat.Chat;
import model.chat.Mensaje;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Panel que muestra el detalle de un chat (conversación) y permite
 * enviar nuevos mensajes.
 *
 * Este panel depende de:
 * - Un ChatController para la lógica de negocio.
 * - Un User (usuarioActual) para saber quién envía los mensajes.
 * - Un Chat (chatActual) que es la conversación que se está visualizando.
 */
public class PanelChatDetalle extends JPanel {

    // ---------------------------------------------------------
    // Atributos de negocio
    // ---------------------------------------------------------
    private ChatController chatController;
    private User usuarioActual;
    private Chat chatActual;

    // ---------------------------------------------------------
    // Componentes de la interfaz gráfica
    // ---------------------------------------------------------
    private JTextArea areaConversacion;
    private JTextField campoNuevoMensaje;
    private JButton botonEnviar;

    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Constructor principal del panel de detalle de chat.
     *
     * @param chatController Controlador de chat para gestionar el envío de
     *                       mensajes.
     */
    public PanelChatDetalle(ChatController chatController) {
        if (chatController == null) {
            throw new IllegalArgumentException("El controlador de chat no puede ser nulo.");
        }
        this.chatController = chatController;

        inicializarComponentes();
        configurarEventos();
    }

    // ---------------------------------------------------------
    // Métodos públicos
    // ---------------------------------------------------------

    /**
     * Establece el usuario actual (logueado) que enviará los mensajes
     * desde este panel.
     *
     * @param usuarioActual Usuario logueado o null si se encuentra en modo
     *                      invitado.
     */
    public void setUsuarioActual(User usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    /**
     * Establece el chat que se debe mostrar en el panel.
     * Al asignar un nuevo chat, se recarga el área de conversación
     * y se marca el chat como leído.
     *
     * @param chat Chat a visualizar.
     */
    public void setChatActual(Chat chat) {
        this.chatActual = chat;

        if (chatActual != null) {
            chatController.marcarChatComoLeido(chatActual);
        }

        recargarConversacion();
    }

    /**
     * Limpia el chat actual (usado al cerrar sesión)
     */
    public void clearChat() {
        this.chatActual = null;
        areaConversacion.setText("");
    }

    // ---------------------------------------------------------
    // Inicialización UI
    // ---------------------------------------------------------

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Área de conversación: solo lectura, con salto de línea automático
        areaConversacion = new JTextArea();
        areaConversacion.setEditable(false);
        areaConversacion.setLineWrap(true);
        areaConversacion.setWrapStyleWord(true);

        JScrollPane scrollConversacion = new JScrollPane(areaConversacion);
        add(scrollConversacion, BorderLayout.CENTER);

        // Panel inferior: campo de texto + botón "Enviar"
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));

        campoNuevoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(botonEnviar);

        panelInferior.add(campoNuevoMensaje, BorderLayout.CENTER);
        panelInferior.add(panelBoton, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

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
        panelOfertas.setLayout(new javax.swing.BoxLayout(panelOfertas, javax.swing.BoxLayout.Y_AXIS));

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
            // Intentar cargar imagen
            ImageIcon icon = new ImageIcon(ruta);
            // Redimensionar si es muy grande
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);
            panelImg.add(new JLabel(new ImageIcon(newImg)));
        }

        dialog.add(new JScrollPane(panelImg));
        dialog.setVisible(true);
    }

    private void configurarEventos() {
        // Enviar al presionar el botón
        botonEnviar.addActionListener(e -> enviarMensajeDesdeCampo());

        // Enviar al presionar Enter en el campo de texto
        campoNuevoMensaje.addActionListener(e -> enviarMensajeDesdeCampo());
    }

    // ---------------------------------------------------------
    // Lógica de negocio de la vista
    // ---------------------------------------------------------

    /**
     * Envía el mensaje escrito en el campo de texto utilizando el controlador
     * de chat. Solo se envía si existe un chatActual y un usuarioActual.
     */
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
     * Recarga el área de conversación con todos los mensajes del chatActual.
     */
    private void recargarConversacion() {
        areaConversacion.setText("");

        if (chatActual == null) {
            return;
        }

        for (Mensaje mensaje : chatController.obtenerMensajes(chatActual)) {
            String nombre = mensaje.getUsuarioRemitente().getNombre();
            String hora = mensaje.getFechaHoraEnvio().format(formatoHora);
            String contenido = mensaje.getContenidoMensaje();

            areaConversacion.append(nombre + " (" + hora + "): " + contenido + "\n");
        }

        // Scroll automático al final
        areaConversacion.setCaretPosition(areaConversacion.getDocument().getLength());
    }
}
