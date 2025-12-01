package view;

import controller.PublicacionController;
import model.Oferta;
import model.Publicacion;
import model.User;
import util.TipoPublicacion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MisOfertasView extends JFrame {

    private final PublicacionController controller;
    private final User usuarioActual;
    private JList<Publicacion> listaPublicaciones;
    private DefaultListModel<Publicacion> listModelPublicaciones;
    private JPanel panelOfertas;

    public MisOfertasView(PublicacionController controller, User usuarioActual) {
        this.controller = controller;
        this.usuarioActual = usuarioActual;

        setTitle("Gestión de Ofertas");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        cargarMisPublicaciones();
    }

    private void initComponents() {
        // Panel Izquierdo: Mis Publicaciones
        listModelPublicaciones = new DefaultListModel<>();
        listaPublicaciones = new JList<>(listModelPublicaciones);
        listaPublicaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPublicaciones.addListSelectionListener(e -> cargarOfertasDePublicacion());

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBorder(BorderFactory.createTitledBorder("Mis Publicaciones"));
        panelIzq.add(new JScrollPane(listaPublicaciones), BorderLayout.CENTER);
        panelIzq.setPreferredSize(new Dimension(250, 0));

        // Panel Derecho: Ofertas recibidas
        panelOfertas = new JPanel();
        panelOfertas.setLayout(new BoxLayout(panelOfertas, BoxLayout.Y_AXIS));
        JScrollPane scrollOfertas = new JScrollPane(panelOfertas);
        scrollOfertas.setBorder(BorderFactory.createTitledBorder("Ofertas Recibidas"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzq, scrollOfertas);
        add(splitPane, BorderLayout.CENTER);
    }

    private void cargarMisPublicaciones() {
        listModelPublicaciones.clear();
        List<Publicacion> misPublicaciones = controller.obtenerPublicacionesPorVendedor(usuarioActual.getId());
        for (Publicacion p : misPublicaciones) {
            listModelPublicaciones.addElement(p);
        }
    }

    private void cargarOfertasDePublicacion() {
        Publicacion seleccionada = listaPublicaciones.getSelectedValue();
        panelOfertas.removeAll();

        if (seleccionada == null) {
            panelOfertas.revalidate();
            panelOfertas.repaint();
            return;
        }

        // Botón para cerrar subasta manualmente (si es subasta)
        if (seleccionada.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
            JButton btnCerrarSubasta = new JButton("CERRAR SUBASTA AHORA");
            btnCerrarSubasta.setBackground(Color.ORANGE);
            btnCerrarSubasta.addActionListener(e -> {
                controller.cerrarSubasta(seleccionada.getIdArticulo(), usuarioActual.getId());
                cargarMisPublicaciones(); // Recargar porque ya no estará activa
                panelOfertas.removeAll();
                panelOfertas.revalidate();
                panelOfertas.repaint();
            });
            panelOfertas.add(btnCerrarSubasta);
            panelOfertas.add(Box.createVerticalStrut(10));
        }

        List<Oferta> ofertas = controller.obtenerOfertas(seleccionada.getIdArticulo());

        if (ofertas.isEmpty()) {
            panelOfertas.add(new JLabel("No hay ofertas aún."));
        } else {
            for (Oferta oferta : ofertas) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                // Obtener el nombre del ofertante
                persistence.UserRepository userRepo = new persistence.UserRepository();
                model.User ofertante = userRepo.buscarPorId(oferta.getIdOfertante());
                String nombreOfertante = (ofertante != null) ? ofertante.getNombre() : oferta.getIdOfertante();

                String texto = "<html><b>De:</b> " + nombreOfertante + "<br/>";
                if (seleccionada.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
                    texto += "<b>Monto:</b> $" + oferta.getMontoOferta();
                } else {
                    texto += "<b>Propuesta:</b> " + oferta.getDescripcionTrueque();
                }
                texto += "<br/><b>Estado:</b> " + oferta.getEstadoOferta() + "</html>";

                JLabel lblInfo = new JLabel(texto);
                lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                card.add(lblInfo, BorderLayout.CENTER);

                // Debug info
                System.out.println("DEBUG: Publicacion Tipo: " + seleccionada.getTipoPublicacion());
                System.out.println("DEBUG: Oferta Estado: " + oferta.getEstadoOferta());
                System.out.println(
                        "DEBUG: Is Trueque? " + (seleccionada.getTipoPublicacion() == TipoPublicacion.TRUEQUE));
                System.out.println("DEBUG: Is Pendiente? " + (oferta.getEstadoOferta() == util.EstadoOferta.PENDIENTE));

                // Botón Aceptar y Rechazar (Solo para Trueques PENDIENTES)
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                if (seleccionada.getTipoPublicacion() == TipoPublicacion.TRUEQUE
                        && oferta.getEstadoOferta() == util.EstadoOferta.PENDIENTE) {

                    JButton btnAceptar = new JButton("Aceptar");
                    btnAceptar.addActionListener(e -> {
                        boolean exito = controller.aceptarOferta(oferta.getIdOferta(), usuarioActual.getId());
                        if (exito) {
                            JOptionPane.showMessageDialog(this, "Oferta aceptada.");
                            cargarMisPublicaciones();
                            panelOfertas.removeAll();
                            panelOfertas.revalidate();
                            panelOfertas.repaint();
                        }
                    });

                    JButton btnRechazar = new JButton("Rechazar");
                    btnRechazar.addActionListener(e -> {
                        boolean exito = controller.rechazarOferta(oferta.getIdOferta(), usuarioActual.getId());
                        if (exito) {
                            JOptionPane.showMessageDialog(this, "Oferta rechazada.");
                            cargarOfertasDePublicacion();
                        }
                    });

                    btnPanel.add(btnAceptar);
                    btnPanel.add(btnRechazar);
                } else {
                    // Para ofertas no pendientes o subastas, permitir eliminar
                    JButton btnEliminar = new JButton("🗑️");
                    btnEliminar.setToolTipText("Eliminar Oferta");
                    btnEliminar.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar esta oferta?",
                                "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean exito = controller.eliminarOferta(oferta.getIdOferta(), usuarioActual.getId());
                            if (exito) {
                                cargarOfertasDePublicacion();
                            }
                        }
                    });
                    btnPanel.add(btnEliminar);
                }

                card.add(btnPanel, BorderLayout.EAST);
                panelOfertas.add(card);
                panelOfertas.add(Box.createVerticalStrut(5));
            }
        }

        panelOfertas.revalidate();
        panelOfertas.repaint();
    }
}
