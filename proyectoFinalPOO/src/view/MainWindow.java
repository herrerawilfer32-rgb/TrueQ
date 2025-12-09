package view;

import controller.AuthController;
import controller.PublicacionController;
import controller.ChatController;
import controller.AdminController;
import controller.ReporteController;

import model.Publicacion;
import model.User;
import model.chat.Chat;
import util.RolUsuario;

import persistence.ChatRepository;
import persistence.ChatFileRepository;
import persistence.UserRepository;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.Timer;

public class MainWindow extends JFrame {

    private User usuarioLogueado = null; // INICIO: MODO INVITADO

    private final AuthController authController;
    private final PublicacionController pubController;
    private final ChatController chatController;
    private final AdminController adminController;
    private final ReporteController reporteController;

    // Componentes UI
    private JLabel lblBienvenida;
    private JButton btnLoginLogout;
    private JButton btnPanelAdmin; // Nuevo botón admin
    private JPanel panelContenedorCards;
    private java.util.List<PublicacionCardPanel> tarjetasActuales;
    private PublicacionCardPanel tarjetaSeleccionada;

    // Pestañas y paneles de chat
    private JTabbedPane pestañasCentro;
    private PanelListaChats panelListaChats;
    private PanelChatDetalle panelChatDetalle;

    private JButton btnNotificaciones;

    public MainWindow(AuthController authController, PublicacionController pubController,
            ChatController chatController, AdminController adminController, ReporteController reporteController) {
        this.authController = authController;
        this.pubController = pubController;
        this.chatController = chatController;
        this.adminController = adminController;
        this.reporteController = reporteController;

        setTitle("Mercado Local - Inicio");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        cargarPublicaciones(); // MOSTRAR PUBLICACIONES APENAS INICIA

        // Timer para notificaciones (cada 5 segundos)
        new Timer(5000, e -> actualizarNotificaciones()).start();
    }

    private void initUI() {
        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        lblBienvenida = new JLabel("Bienvenido, Invitado");
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel panelDerechoHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelDerechoHeader.setOpaque(false);

        btnNotificaciones = new JButton("🔔 0");
        btnNotificaciones.setVisible(false);
        btnNotificaciones.setBackground(new Color(231, 76, 60));
        btnNotificaciones.setForeground(Color.WHITE);
        btnNotificaciones.addActionListener(e -> {
            if (pestañasCentro != null)
                pestañasCentro.setSelectedIndex(1); // Ir a chats
        });

        btnPanelAdmin = new JButton("🛠️ Panel Admin");
        btnPanelAdmin.setVisible(false);
        btnPanelAdmin.setBackground(new Color(243, 156, 18));
        btnPanelAdmin.setForeground(Color.WHITE);
        btnPanelAdmin.addActionListener(e -> abrirPanelAdmin());

        btnLoginLogout = new JButton("Iniciar Sesión");
        btnLoginLogout.addActionListener(e -> manejarSesion());

        panelDerechoHeader.add(btnPanelAdmin);
        panelDerechoHeader.add(btnNotificaciones);
        panelDerechoHeader.add(btnLoginLogout);

        header.add(lblBienvenida, BorderLayout.WEST);
     // Panel derecho para Cerrar Sesión + Salir
        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelDerecha.setOpaque(false);

        // Botón Cerrar Sesión (el que ya tenías)
        panelDerecha.add(btnLoginLogout);

        // Botón Salir del Programa
        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(192, 57, 43));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnSalir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas salir del programa?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        panelDerecha.add(btnSalir);

        // Agregar panel al header
        header.add(panelDerecha, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- CENTRO: PESTAÑAS (Publicaciones + Chats) ---
        pestañasCentro = new JTabbedPane();

        // ==== TAB PUBLICACIONES ====
        tarjetasActuales = new java.util.ArrayList<>();

        panelContenedorCards = new JPanel();
        panelContenedorCards.setLayout(new GridLayout(0, 3, 15, 15));
        panelContenedorCards.setBackground(Color.WHITE);
        panelContenedorCards.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollCards = new JScrollPane(panelContenedorCards);
        scrollCards.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollCards.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCards.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panelPublicaciones = new JPanel(new BorderLayout());
        panelPublicaciones.setBorder(BorderFactory.createTitledBorder(" Últimas Publicaciones "));
        panelPublicaciones.add(scrollCards, BorderLayout.CENTER);

        pestañasCentro.addTab("Publicaciones", panelPublicaciones);

        // ==== TAB CHATS ====
        panelListaChats = new PanelListaChats(chatController, chatSeleccionado -> {
            // Cuando seleccionan un chat en la lista
            panelChatDetalle.setChatActual(chatSeleccionado);
            pestañasCentro.setSelectedIndex(1); // Ir a la pestaña "Chats"
        });

        panelChatDetalle = new PanelChatDetalle(chatController);
        panelChatDetalle.setPublicacionController(pubController);


        JSplitPane splitChats = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                panelListaChats,
                panelChatDetalle);
        splitChats.setDividerLocation(300);

        pestañasCentro.addTab("Chats", splitChats);

        add(pestañasCentro, BorderLayout.CENTER);

        // --- FOOTER: BOTONES DE ACCIÓN ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnVender = new JButton("💰 Publicar Artículo");
        JButton btnMisOfertas = new JButton("🤝 Ver Mis Ofertas");
        JButton btnRefrescar = new JButton("🔄 Actualizar Lista");

        // Nuevos botones CRUD
        JButton btnVerDetalle = new JButton("👁️ Ver Detalle");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnSalirApp = new JButton("Salir");

        // LOGICA DEL "PORTERO" (GATEKEEPER)
        btnVender.addActionListener(e -> {
            if (esInvitado())
                abrirLogin();
            else
                abrirFormularioVenta();
        });

        btnMisOfertas.addActionListener(e -> {
            if (esInvitado())
                abrirLogin();
            else
                new MisOfertasView(pubController, usuarioLogueado).setVisible(true);
        });

        btnRefrescar.addActionListener(e -> cargarPublicaciones());

        btnVerDetalle.addActionListener(e -> verDetalleSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPublicacionSeleccionada());
        btnEditar.addActionListener(e -> editarPublicacionSeleccionada());
        btnSalirApp.addActionListener(e -> cerrarAplicacion());
        
        footer.add(btnVender);
        footer.add(btnMisOfertas);
        footer.add(btnRefrescar);
        footer.add(new JSeparator(SwingConstants.VERTICAL));
        footer.add(btnVerDetalle);
        footer.add(btnEditar);
        footer.add(btnEliminar);
        footer.add(btnSalirApp);  

        add(footer, BorderLayout.SOUTH);
    }

    // --- MÉTODOS LÓGICOS ---

    public void cargarPublicaciones() {
        panelContenedorCards.removeAll();
        tarjetasActuales.clear();
        tarjetaSeleccionada = null;

        List<Publicacion> lista = pubController.obtenerPublicacionesActivas();

        if (lista != null) {
            for (Publicacion p : lista) {
                PublicacionCardPanel card = new PublicacionCardPanel(p, pubController);

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        seleccionarTarjeta(card);
                    }
                });

                tarjetasActuales.add(card);
                panelContenedorCards.add(card);
            }
        }

        panelContenedorCards.revalidate();
        panelContenedorCards.repaint();
    }

    private void seleccionarTarjeta(PublicacionCardPanel card) {
        if (tarjetaSeleccionada != null) {
            tarjetaSeleccionada.setSelected(false);
        }
        tarjetaSeleccionada = card;
        tarjetaSeleccionada.setSelected(true);
    }

    private void verDetalleSeleccionado() {
        if (esInvitado()) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ver detalles y ofertar.");
            return;
        }

        if (tarjetaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una publicación primero.");
            return;
        }
        Publicacion seleccionada = tarjetaSeleccionada.getPublicacion();

        // 🔗 Ahora le pasamos también this (MainWindow) para poder abrir el chat desde
        // el detalle, y el ReporteController
        new DetallePublicacionView(pubController, seleccionada, usuarioLogueado, this, reporteController)
                .setVisible(true);
    }

    private void eliminarPublicacionSeleccionada() {
        if (esInvitado()) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión.");
            return;
        }

        if (tarjetaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una publicación primero.");
            return;
        }
        Publicacion seleccionada = tarjetaSeleccionada.getPublicacion();

        // Permitir eliminar si es dueño O si es admin
        boolean esDueño = seleccionada.getIdVendedor().equals(usuarioLogueado.getId());
        boolean esAdmin = usuarioLogueado.isAdmin();

        if (!esDueño && !esAdmin) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar esta publicación.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de eliminar '" + seleccionada.getTitulo() + "'?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = false;
            if (esAdmin && !esDueño) {
                exito = adminController.eliminarPublicacion(seleccionada.getIdArticulo(), usuarioLogueado.getId());
            } else {
                exito = pubController.eliminarPublicacion(seleccionada.getIdArticulo(), usuarioLogueado.getId());
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, "Publicación eliminada.");
                cargarPublicaciones();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la publicación.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarPublicacionSeleccionada() {
        if (esInvitado()) {
            JOptionPane.showMessageDialog(this, "Debes iniciar sesión.");
            return;
        }

        if (tarjetaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una publicación primero.");
            return;
        }
        Publicacion seleccionada = tarjetaSeleccionada.getPublicacion();

        // Verificar dueño antes de abrir ventana
        if (!seleccionada.getIdVendedor().equals(usuarioLogueado.getId())) {
            JOptionPane.showMessageDialog(this, "No puedes editar esta publicación (No eres el dueño).", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Abrir ventana de edición
        new EditarPublicacionView(pubController, usuarioLogueado, this, seleccionada).setVisible(true);
    }

    private boolean esInvitado() {
        return usuarioLogueado == null;
    }

    private void abrirLogin() {
        JOptionPane.showMessageDialog(this, "Debes iniciar sesión para realizar esta acción.");
        new LoginWindow(authController, this); // Pasamos 'this' para que el login nos actualice
    }

    private void abrirFormularioVenta() {
        new CrearPublicacionView(pubController, usuarioLogueado, this).setVisible(true);
    }

    private void abrirPanelAdmin() {
        if (usuarioLogueado != null && usuarioLogueado.isAdmin()) {
            new AdminDashboardView(adminController, reporteController, usuarioLogueado).setVisible(true);
        }
    }

    private void manejarSesion() {
        if (esInvitado()) {
            new LoginWindow(authController, this);
        } else {
            int opt = JOptionPane.showConfirmDialog(this, "¿Cerrar Sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                setUsuarioLogueado(null); // Volver a modo invitado
            }
        }
    }

    // Llamado por LoginWindow cuando el login es exitoso
    public void setUsuarioLogueado(User user) {
        this.usuarioLogueado = user;
        if (user != null) {
            lblBienvenida.setText("Hola, " + user.getNombre());
            btnLoginLogout.setText("Cerrar Sesión");

            // Mostrar botón admin si corresponde
            if (user.isAdmin()) {
                btnPanelAdmin.setVisible(true);
            } else {
                btnPanelAdmin.setVisible(false);
            }
        } else {
            lblBienvenida.setText("Bienvenido, Invitado");
            btnLoginLogout.setText("Iniciar Sesión");
            btnPanelAdmin.setVisible(false);
        }

        // 🔄 Actualizar módulo de chat cuando cambia el usuario
        if (panelListaChats != null) {
            panelListaChats.setUsuarioActual(usuarioLogueado);
        }
        if (panelChatDetalle != null) {
            panelChatDetalle.setUsuarioActual(usuarioLogueado);
            // Limpiar chat al cerrar sesión
            if (usuarioLogueado == null) {
                panelChatDetalle.clearChat();
            }
        }
    }

    /**
     * Método de integración: abre (o crea) un chat entre el usuario logueado
     * y el vendedor de la publicación indicada.
     */
    public void abrirChatConVendedor(Publicacion publicacion) {
        if (usuarioLogueado == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes iniciar sesión para contactar al vendedor.");
            return;
        }
        if (publicacion == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la publicación.");
            return;
        }
        if (usuarioLogueado.getId().equals(publicacion.getIdVendedor())) {
            JOptionPane.showMessageDialog(this,
                    "Eres el propietario de esta publicación.");
            return;
        }

        // Buscar datos del vendedor
        UserRepository userRepo = new UserRepository();
        User vendedor = userRepo.buscarPorId(publicacion.getIdVendedor());
        if (vendedor == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la información del vendedor.");
            return;
        }

        // Obtener o crear el chat entre comprador y vendedor
        Chat chat = chatController.obtenerOCrearChat(usuarioLogueado, vendedor);

        // Actualizar paneles de chat
        if (panelListaChats != null) {
            panelListaChats.setUsuarioActual(usuarioLogueado);
        }
        if (panelChatDetalle != null) {
            panelChatDetalle.setUsuarioActual(usuarioLogueado);
            panelChatDetalle.setChatActual(chat);
        }

        // Cambiar a la pestaña "Chats"
        if (pestañasCentro != null) {
            pestañasCentro.setSelectedIndex(1);
        }
    }

    /**
     * Actualiza el botón de notificaciones con el número de mensajes no leídos.
     */
    private void actualizarNotificaciones() {
        if (usuarioLogueado == null) {
            btnNotificaciones.setVisible(false);
            return;
        }

        // Obtener chats del usuario actual
        List<Chat> chats = chatController.obtenerChatsDeUsuario(usuarioLogueado);
        int mensajesNoLeidos = 0;

        for (Chat chat : chats) {
            if (chat.tieneMensajesNoLeidos()) {
                mensajesNoLeidos++;
            }
        }

        if (mensajesNoLeidos > 0) {
            btnNotificaciones.setText("🔔 " + mensajesNoLeidos);
            btnNotificaciones.setVisible(true);
        } else {
            btnNotificaciones.setVisible(false);
        }
    }
    /**
     * Cierra la aplicación completa pidiendo confirmación al usuario.
     */
    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas salir de la aplicación?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();        // Cierra la ventana principal
            System.exit(0);   // Termina el proceso Java
        }
    }
}
