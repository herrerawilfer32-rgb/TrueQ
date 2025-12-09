package view;

import model.Publicacion;
import model.PublicacionSubasta;
import model.User;
import controller.PublicacionController;
import util.ImageUtils;
import util.TipoPublicacion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

/**
 * Panel personalizado que representa una tarjeta de publicación.
 */
public class PublicacionCardPanel extends JPanel {

    private final Publicacion publicacion;
    private final PublicacionController controller;
    private boolean selected = false;
    private static final Color SELECTED_COLOR = new Color(200, 230, 255);
    private static final Color HOVER_COLOR = new Color(245, 245, 245);
    private static final Color NORMAL_COLOR = Color.WHITE;

    public PublicacionCardPanel(Publicacion publicacion, PublicacionController controller) {
        this.publicacion = publicacion;
        this.controller = controller;

        setLayout(new BorderLayout(5, 5));
        setBackground(NORMAL_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)));

        setPreferredSize(new Dimension(220, 340));
        setMaximumSize(new Dimension(220, 340));

        initComponents();
        addHoverEffect();
    }

    private void initComponents() {
        // Panel de imagen (arriba)
        JLabel lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setPreferredSize(new Dimension(200, 150));

        // Cargar imagen o placeholder
        ImageIcon imagen;
        if (publicacion.getFotosPaths() != null && !publicacion.getFotosPaths().isEmpty()) {
            String primeraFoto = publicacion.getFotosPaths().get(0);
            imagen = ImageUtils.loadImage(primeraFoto, 200, 150);
        } else {
            imagen = ImageUtils.getPlaceholderImage(200, 150);
        }
        lblImagen.setIcon(imagen);

        add(lblImagen, BorderLayout.NORTH);

        // Panel de información (centro)
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setOpaque(false);

        // Badge de tipo
        JLabel lblTipo = new JLabel(publicacion.getTipoPublicacion().toString());
        lblTipo.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTipo.setOpaque(true);
        lblTipo.setBorder(new EmptyBorder(2, 8, 2, 8));

        if (publicacion.getTipoPublicacion() == TipoPublicacion.SUBASTA) {
            lblTipo.setBackground(new Color(255, 193, 7));
            lblTipo.setForeground(new Color(80, 60, 0));
            lblTipo.setText("🔨 SUBASTA");
        } else {
            lblTipo.setBackground(new Color(76, 175, 80));
            lblTipo.setForeground(Color.WHITE);
            lblTipo.setText("🔄 TRUEQUE");
        }
        lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Título
        JLabel lblTitulo = new JLabel("<html><b>" + truncate(publicacion.getTitulo(), 25) + "</b></html>");
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Precio o "Trueque"
        JLabel lblPrecio = new JLabel();
        lblPrecio.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPrecio.setForeground(new Color(0, 150, 0));
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (publicacion instanceof PublicacionSubasta) {
            double precio = ((PublicacionSubasta) publicacion).getPrecioMinimo();
            lblPrecio.setText(String.format("$%.2f", precio));
        } else {
            lblPrecio.setText("Intercambio");
            lblPrecio.setForeground(new Color(100, 100, 100));
        }

        // Descripción
        String descripcion = publicacion.getDescripcion();
        if (descripcion == null)
            descripcion = "";
        JLabel lblDescripcion = new JLabel("<html>" + truncate(descripcion, 50) + "</html>");
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblDescripcion.setForeground(new Color(100, 100, 100));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nombre del vendedor y Ubicación
        User vendedor = null;
        String nombreVendedor = "Desconocido";
        String ubicacionVendedor = "Desconocida";

        try {
            vendedor = controller.obtenerVendedor(publicacion.getIdArticulo());
            if (vendedor != null) {
                nombreVendedor = vendedor.getNombre();
                if (vendedor.getUbicacion() != null && !vendedor.getUbicacion().isBlank()) {
                    ubicacionVendedor = vendedor.getUbicacion();
                }
            }
        } catch (Exception e) {
            nombreVendedor = "Usuario " + publicacion.getIdVendedor();
        }

        String textoVendedor = "👤 " + nombreVendedor;
        if (vendedor != null && vendedor.getNumeroCalificaciones() > 0) {
            textoVendedor += String.format(" (⭐ %.1f)", vendedor.getReputacion());
        }
        JLabel lblVendedor = new JLabel(textoVendedor);
        lblVendedor.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblVendedor.setForeground(new Color(52, 73, 94));
        lblVendedor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUbicacion = new JLabel("📍 " + ubicacionVendedor);
        lblUbicacion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblUbicacion.setForeground(new Color(100, 100, 100));
        lblUbicacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Estado
        JLabel lblEstado = new JLabel("• " + publicacion.getEstado().toString());
        lblEstado.setFont(new Font("SansSerif", Font.ITALIC, 10));
        lblEstado.setForeground(new Color(120, 120, 120));
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Agregar componentes al panel de info
        // Agregar componentes al panel de info
        panelInfo.add(lblTipo);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblTitulo);
        panelInfo.add(Box.createVerticalStrut(3));
        panelInfo.add(lblPrecio);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblDescripcion);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(lblVendedor);
        panelInfo.add(Box.createVerticalStrut(2)); // Espacio entre vendedor y ubicación
        panelInfo.add(lblUbicacion);
        panelInfo.add(Box.createVerticalGlue());

        add(panelInfo, BorderLayout.CENTER);

        // Panel inferior para estado y fecha de cierre
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.setBorder(new EmptyBorder(5, 0, 0, 0)); // Un poco de espacio arriba
        panelInferior.add(lblEstado, BorderLayout.WEST);

        // Si es subasta, agregar fecha de cierre a la derecha
        if (publicacion instanceof PublicacionSubasta) {
            PublicacionSubasta subasta = (PublicacionSubasta) publicacion;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
            String fechaFormateada = sdf.format(subasta.getFechaCierre());

            JLabel lblFechaCierre = new JLabel("⏰ " + fechaFormateada);
            lblFechaCierre.setFont(new Font("SansSerif", Font.BOLD, 10));
            lblFechaCierre.setForeground(new Color(220, 53, 69)); // Rojo para urgencia
            panelInferior.add(lblFechaCierre, BorderLayout.EAST);
        }

        // Agregar panelInferior al SUR del borde principal para que siempre se vea
        add(panelInferior, BorderLayout.SOUTH);
    }

    private void addHoverEffect() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    setBackground(HOVER_COLOR);
                }
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    setBackground(NORMAL_COLOR);
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBackground(SELECTED_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
                    new EmptyBorder(9, 9, 9, 9)));
        } else {
            setBackground(NORMAL_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(10, 10, 10, 10)));
        }
        repaint();
    }

    public boolean isSelected() {
        return selected;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        if (text.length() <= maxLength)
            return text;
        return text.substring(0, maxLength) + "...";
    }

}
