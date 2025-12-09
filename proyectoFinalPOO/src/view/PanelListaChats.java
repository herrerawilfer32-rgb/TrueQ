package view;

import controller.ChatController;
import model.User;
import model.chat.Chat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.awt.Color;

/**
 * Panel encargado de mostrar la lista de chats asociados a un usuario.
 * Notifica a un listener externo cuando el usuario desea abrir un chat.
 */
public class PanelListaChats extends JPanel {

    /**
     * Listener para notificar que el usuario desea abrir un chat específico.
     */
    public interface ChatSeleccionListener {
        void abrirChat(Chat chatSeleccionado);
    }

    // ---------------------------------------------------------
    // Atributos de negocio
    // ---------------------------------------------------------
    private ChatController chatController;
    private User usuarioActual;
    private ChatSeleccionListener chatSeleccionListener;

    // ---------------------------------------------------------
    // Componentes UI
    // ---------------------------------------------------------
    private JTable tablaChats;
    private DefaultTableModel modeloTablaChats;
    private JButton botonAbrirChat;
    private JButton botonActualizar;
    private JLabel etiquetaUsuarioActual;

    /**
     * Constructor principal del panel de lista de chats.
     *
     * @param chatController        Controlador de chat que provee los datos.
     * @param chatSeleccionListener Listener que será notificado cuando se
     *                              desee abrir un chat específico.
     */
    public PanelListaChats(ChatController chatController,
            ChatSeleccionListener chatSeleccionListener) {
    	setForeground(new Color(240, 201, 108));
    	setBackground(new Color(71, 116, 112));

        if (chatController == null) {
            throw new IllegalArgumentException("El controlador de chat no puede ser nulo.");
        }

        this.chatController = chatController;
        this.chatSeleccionListener = chatSeleccionListener;

        inicializarComponentes();
        configurarEventos();
        actualizarEtiquetaUsuario(); // Modo invitado al inicio
    }

    // ---------------------------------------------------------
    // Métodos públicos
    // ---------------------------------------------------------

    /**
     * Establece el usuario actual (logueado) sobre el cual se listarán los chats.
     *
     * @param usuarioActual Usuario actual o null si es invitado.
     */
    public void setUsuarioActual(User usuarioActual) {
        this.usuarioActual = usuarioActual;
        actualizarEtiquetaUsuario();
        cargarChatsEnTabla();
    }

    // ---------------------------------------------------------
    // Inicialización de UI
    // ---------------------------------------------------------

    private void inicializarComponentes() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Panel superior ----
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setForeground(new Color(240, 201, 108));
        panelSuperior.setBackground(new Color(71, 116, 112));
        etiquetaUsuarioActual = new JLabel();
        panelSuperior.add(etiquetaUsuarioActual, BorderLayout.WEST);

        botonActualizar = new JButton("Actualizar");
        botonActualizar.setForeground(new Color(0, 0, 0));
        botonActualizar.setBackground(new Color(106, 153, 149));
        JPanel panelBotonActualizar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonActualizar.setBackground(new Color(71, 116, 112));
        panelBotonActualizar.setForeground(new Color(143, 35, 233));
        panelBotonActualizar.add(botonActualizar);
        panelSuperior.add(panelBotonActualizar, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // ---- Tabla ----
        modeloTablaChats = new DefaultTableModel(
                new Object[] { "Contacto", "Mensajes no leídos", "Total" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaChats = new JTable(modeloTablaChats);
        tablaChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollTabla = new JScrollPane(tablaChats);
        add(scrollTabla, BorderLayout.CENTER);

        // ---- Panel inferior ----
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(71, 116, 112));
        botonAbrirChat = new JButton("Abrir chat");
        botonAbrirChat.setForeground(new Color(0, 0, 0));
        botonAbrirChat.setBackground(new Color(106, 153, 149));
        panelInferior.add(botonAbrirChat);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        botonActualizar.addActionListener(e -> cargarChatsEnTabla());
        botonAbrirChat.addActionListener(e -> notificarChatSeleccionado());
    }

    private void actualizarEtiquetaUsuario() {
        if (usuarioActual == null) {
            etiquetaUsuarioActual.setText("Chats de: Invitado (sin sesión)");
        } else {
            etiquetaUsuarioActual.setText("Chats de: " + usuarioActual.getNombre());
        }
    }

    // ---------------------------------------------------------
    // Carga de información
    // ---------------------------------------------------------

    private void cargarChatsEnTabla() {
        modeloTablaChats.setRowCount(0);

        if (usuarioActual == null) {
            return; // modo invitado - no cargar chats
        }

        List<Chat> listaChats = chatController.listarChatsDeUsuario(usuarioActual);
        if (listaChats == null || listaChats.isEmpty()) {
            return;
        }

        for (Chat chat : listaChats) {

            User otroUsuario = chat.obtenerOtroUsuario(usuarioActual);
            String nombreContacto = (otroUsuario != null)
                    ? otroUsuario.getNombre()
                    : "Desconocido";

            String textoNoLeidos = chat.isTieneMensajesNoLeidos() ? "Sí" : "No";
            int totalMensajes = chat.getListaMensajes().size();

            modeloTablaChats.addRow(new Object[] {
                    nombreContacto,
                    textoNoLeidos,
                    totalMensajes
            });
        }
    }

    // ---------------------------------------------------------
    // Selección de chat
    // ---------------------------------------------------------

    private void notificarChatSeleccionado() {
        if (chatSeleccionListener == null || usuarioActual == null) {
            return;
        }

        int fila = tablaChats.getSelectedRow();
        if (fila < 0) {
            return;
        }

        List<Chat> listaChats = chatController.listarChatsDeUsuario(usuarioActual);
        if (fila >= listaChats.size()) {
            return;
        }

        Chat chatSeleccionado = listaChats.get(fila);

        // Marcar como leído antes de abrir
        chatController.marcarChatComoLeido(chatSeleccionado);

        chatSeleccionListener.abrirChat(chatSeleccionado);
    }
}
