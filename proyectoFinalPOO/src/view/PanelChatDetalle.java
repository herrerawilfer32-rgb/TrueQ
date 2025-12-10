/*
 * Clase: PanelChatDetalle
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.ChatController;
import model.User;
import model.chat.Chat;
import model.chat.Mensaje;
import model.Publicacion;

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
    private controller.PublicacionController pubController;
    private User usuarioActual;
    private Chat chatActual;
    private JPanel panelAlertas;

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
    public PanelChatDetalle(ChatController chatController, controller.PublicacionController pubController) {
    	setBackground(new Color(71, 116, 112));
        if (chatController == null) {
            throw new IllegalArgumentException("El controlador de chat no puede ser nulo.");
        }
        this.chatController = chatController;
        this.pubController = pubController;

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
            verificarPagosPendientes();
        } else {
            if (panelAlertas != null)
                panelAlertas.removeAll();
        }

        recargarConversacion();
    }

    /**
     * Limpia el chat actual (usado al cerrar sesión)
     */
    public void clearChat() {
        this.chatActual = null;
        areaConversacion.setText("");
        if (panelAlertas != null) {
            panelAlertas.removeAll();
            panelAlertas.revalidate();
            panelAlertas.repaint();
        }
    }

    // ---------------------------------------------------------
    // Inicialización UI
    // ---------------------------------------------------------

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Área de conversación: solo lectura, con salto de línea automático
        areaConversacion = new JTextArea();
        areaConversacion.setBackground(new Color(231, 250, 254));
        areaConversacion.setEditable(false);
        areaConversacion.setLineWrap(true);
        areaConversacion.setWrapStyleWord(true);

        JScrollPane scrollConversacion = new JScrollPane(areaConversacion);
        add(scrollConversacion, BorderLayout.CENTER);

        // Panel inferior: campo de texto + botón "Enviar"
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setBackground(new Color(71, 116, 112));

        campoNuevoMensaje = new JTextField();
        campoNuevoMensaje.setBackground(new Color(206, 244, 253));
        botonEnviar = new JButton("Enviar");
        botonEnviar.setBackground(new Color(106, 153, 149));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.setBackground(new Color(71, 116, 112));
        panelBoton.add(botonEnviar);

        panelInferior.add(campoNuevoMensaje, BorderLayout.CENTER);
        panelInferior.add(panelBoton, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

        // Panel Superior: Botón de Ver Ofertas y Alertas
        JPanel panelSuperiorContainer = new JPanel(new BorderLayout());

        panelAlertas = new JPanel();
        panelAlertas.setBackground(new Color(71, 116, 112));
        panelAlertas.setLayout(new BoxLayout(panelAlertas, BoxLayout.Y_AXIS));
        panelSuperiorContainer.add(panelAlertas, BorderLayout.CENTER);

        JPanel panelBotonesSup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonesSup.setBackground(new Color(71, 116, 112));
        JButton btnVerOfertas = new JButton("Ver Ofertas Relacionadas");
        btnVerOfertas.setBackground(new Color(106, 153, 149));
        btnVerOfertas.addActionListener(e -> mostrarOfertasRelacionadas());
        panelBotonesSup.add(btnVerOfertas);

        panelSuperiorContainer.add(panelBotonesSup, BorderLayout.EAST);

        add(panelSuperiorContainer, BorderLayout.NORTH);
    }

    private void verificarPagosPendientes() {
        if (panelAlertas == null)
            return;
        panelAlertas.removeAll();

        if (chatActual == null || usuarioActual == null) {
            panelAlertas.revalidate();
            panelAlertas.repaint();
            return;
        }

        User otroUsuario = chatActual.obtenerOtroUsuario(usuarioActual);
        if (otroUsuario == null)
            return;

        java.util.List<model.Publicacion> pendientes = chatController.obtenerPagosPendientes(otroUsuario,
                usuarioActual);

        for (Publicacion p : pendientes) {
            JPanel alerta = new JPanel(new BorderLayout(10, 5));
            alerta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(251, 192, 45)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            if (p.getEstado() == util.EstadoPublicacion.FINALIZADA) {
                // --- CASO: TRANSACCIÓN FINALIZADA (PARA AMBOS) ---
                alerta.setBackground(new Color(200, 230, 201)); // Verde claro
                alerta.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113)));

                if (usuarioActual.haCalificadoPublicacion(p.getIdArticulo())) {
                    JLabel lblDone = new JLabel(
                            "<html><b>¡Transacción Completada!</b><br/>Ya has calificado esta transacción.</html>");
                    lblDone.setForeground(new Color(27, 94, 32));
                    alerta.add(lblDone, BorderLayout.CENTER);
                } else {
                    JLabel lblMsg = new JLabel("<html><b>¡Transacción Completada!</b><br/>'" + p.getTitulo()
                            + "'<br/>Recuerda calificar tu experiencia.</html>");
                    lblMsg.setForeground(new Color(27, 94, 32));

                    JButton btnCalificar = new JButton("⭐ Calificar Usuario");
                    btnCalificar.setBackground(new Color(255, 193, 7));
                    btnCalificar.setForeground(Color.BLACK);
                    btnCalificar.addActionListener(e -> {
                        // Determinar a quién calificar (al otro)
                        User u = chatActual.obtenerOtroUsuario(usuarioActual);
                        if (u != null) {
                            new view.PerfilUsuarioView(null, u,
                                    new controller.UserController(pubController.getUserService()), true,
                                    p.getIdArticulo(), usuarioActual).setVisible(true);
                            // Refrescar al cerrar la ventana de calificación (hack simple)
                            verificarPagosPendientes();
                        }
                    });

                    alerta.add(lblMsg, BorderLayout.CENTER);
                    alerta.add(btnCalificar, BorderLayout.EAST);
                }
            } else if (p.getIdVendedor().equals(usuarioActual.getId())) {
                // --- CASO: VENDEDOR ESPERANDO PAGO/CONFIRMACIÓN ---
                alerta.setBackground(new Color(255, 235, 59));
                String tipo = (p.getTipoPublicacion() == util.TipoPublicacion.SUBASTA) ? "Subasta" : "Trueque";
                JLabel lblMsg = new JLabel("<html><b>" + tipo + " Cerrada:</b> '" + p.getTitulo()
                        + "'.<br/>Esperando confirmación/pago del otro usuario.</html>");
                lblMsg.setForeground(new Color(60, 60, 0));
                alerta.add(lblMsg, BorderLayout.CENTER);
            } else {
                // --- CASO: COMPRADOR/OFERTANTE QUE DEBE PAGAR/CONFIRMAR ---
                alerta.setBackground(new Color(255, 235, 59));
                if (p.getTipoPublicacion() == util.TipoPublicacion.SUBASTA) {
                    JLabel lblMsg = new JLabel("<html><b>¡Ganaste la subasta!</b><br/>'" + p.getTitulo()
                            + "'<br/>Debes realizar el pago.</html>");
                    lblMsg.setForeground(new Color(60, 60, 0));

                    JButton btnPagar = new JButton("Pagar Ahora");
                    btnPagar.setBackground(new Color(46, 204, 113));
                    btnPagar.setForeground(Color.WHITE);
                    btnPagar.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Esto simulará el pago para '" + p.getTitulo() + "'.\n¿Confirmar pago?", "Pago",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            pubController.finalizarSubastaConPago(p.getIdArticulo(), usuarioActual.getId());
                            verificarPagosPendientes(); // Refrescar
                        }
                    });
                    alerta.add(lblMsg, BorderLayout.CENTER);
                    alerta.add(btnPagar, BorderLayout.EAST);
                } else {
                    JLabel lblMsg = new JLabel("<html><b>¡Tu oferta fue aceptada!</b><br/>Trueque: '" + p.getTitulo()
                            + "'<br/>Confirma para finalizar.</html>");
                    lblMsg.setForeground(new Color(60, 60, 0));

                    JButton btnConcretar = new JButton("Concretar Trueque");
                    btnConcretar.setBackground(new Color(52, 152, 219));
                    btnConcretar.setForeground(Color.WHITE);
                    btnConcretar.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "¿Deseas concretar el intercambio para '" + p.getTitulo() + "'?", "Confirmar Trueque",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            pubController.concretarIntercambio(p.getIdArticulo(), usuarioActual.getId());
                            verificarPagosPendientes(); // Refrescar
                        }
                    });
                    alerta.add(lblMsg, BorderLayout.CENTER);
                    alerta.add(btnConcretar, BorderLayout.EAST);
                }
            }
            panelAlertas.add(alerta);
            panelAlertas.add(Box.createVerticalStrut(5));
        }

        panelAlertas.revalidate();
        panelAlertas.repaint();
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

        dialog.getContentPane().add(new JScrollPane(panelImg));
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
