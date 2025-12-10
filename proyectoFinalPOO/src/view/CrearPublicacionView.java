/*
 * Clase: CrearPublicacionView
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Vista de la interfaz.
 */

package view;

import controller.PublicacionController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CrearPublicacionView extends JFrame {

    private final PublicacionController controller;
    private final User vendedor;
    private final MainWindow mainWindow; // Para refrescar la lista al terminar

    private JTextField txtTitulo, txtPrecio;
    private JTextArea txtDescripcion, txtDeseos;
    private JComboBox<String> cmbTipo, cmbCategoria, cmbCondicion;
    private JPanel panelDinamico;
    private CardLayout cardLayout;
    private java.util.List<String> fotosSeleccionadas; // Lista de rutas de fotos
    private JPanel panelPreviewFotos; // Panel para mostrar previews
    private persistence.ConfiguracionRepository configRepo;

    public CrearPublicacionView(PublicacionController controller, User vendedor, MainWindow mainWindow) {
        setBackground(new Color(55, 206, 191));
        this.controller = controller;
        this.vendedor = vendedor;
        this.mainWindow = mainWindow;

        setTitle("Nueva Publicación");
        setSize(500, 650);
        setLocationRelativeTo(mainWindow);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        fotosSeleccionadas = new ArrayList<>();
        configRepo = new persistence.ConfiguracionRepository();

        initComponents();
    }

    private void initComponents() {
        // --- Formulario Común ---
        JPanel panelForm = new JPanel(new GridLayout(0, 1, 5, 5));
        panelForm.setBackground(new Color(145, 78, 184));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Título del Artículo:");
        label.setBackground(new Color(145, 78, 184));
        panelForm.add(label);
        txtTitulo = new JTextField();
        txtTitulo.setBackground(new Color(238, 181, 251));
        panelForm.add(txtTitulo);

        JLabel label_1 = new JLabel("Descripción:");
        label_1.setBackground(new Color(145, 78, 184));
        panelForm.add(label_1);
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setBackground(new Color(238, 181, 251));
        panelForm.add(new JScrollPane(txtDescripcion));

        JLabel label_2 = new JLabel("Tipo de Publicación:");
        label_2.setBackground(new Color(145, 78, 184));
        panelForm.add(label_2);
        cmbTipo = new JComboBox<>(new String[] { "SUBASTA", "TRUEQUE" });
        cmbTipo.setBackground(new Color(238, 181, 251));
        panelForm.add(cmbTipo);

        // --- Categoría ---
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBackground(new Color(145, 78, 184));
        panelForm.add(lblCategoria);

        java.util.List<String> categorias = configRepo.obtenerConfiguracion().getCategorias();
        categorias.add("+ Agregar nueva categoría...");
        cmbCategoria = new JComboBox<>(categorias.toArray(new String[0]));
        cmbCategoria.setBackground(new Color(238, 181, 251));
        cmbCategoria.addActionListener(e -> manejarSeleccionCategoria());
        panelForm.add(cmbCategoria);

        // --- Condición del Artículo ---
        JLabel lblCondicion = new JLabel("Condición del Artículo:");
        lblCondicion.setBackground(new Color(145, 78, 184));
        panelForm.add(lblCondicion);

        cmbCondicion = new JComboBox<>(new String[] {
                "Nuevo",
                "Usado como nuevo",
                "Usado buen estado",
                "Aceptable"
        });
        cmbCondicion.setBackground(new Color(238, 181, 251));
        panelForm.add(cmbCondicion);

        // --- Panel Dinámico (Cambia según el combo) ---
        cardLayout = new CardLayout();
        panelDinamico = new JPanel(cardLayout);

        // Opción A: Panel Subasta
        JPanel panelSubasta = new JPanel(new GridLayout(0, 1));
        JLabel label_4 = new JLabel("Precio Mínimo ($):");
        label_4.setBackground(new Color(238, 181, 251));
        panelSubasta.add(label_4);
        txtPrecio = new JTextField();
        txtPrecio.setBackground(new Color(145, 78, 184));
        panelSubasta.add(txtPrecio);
        panelDinamico.add(panelSubasta, "SUBASTA");

        // Opción B: Panel Trueque
        JPanel panelTrueque = new JPanel(new GridLayout(0, 1));
        panelTrueque.add(new JLabel("¿Qué buscas a cambio?"));
        txtDeseos = new JTextArea(2, 20);
        panelTrueque.add(new JScrollPane(txtDeseos));
        panelDinamico.add(panelTrueque, "TRUEQUE");

        panelForm.add(panelDinamico);

        // --- Sección de Fotos ---
        JLabel label_3 = new JLabel("Fotos del Artículo:");
        label_3.setBackground(new Color(145, 78, 184));
        panelForm.add(label_3);

        JButton btnAgregarFoto = new JButton("📷 Seleccionar Imágenes");
        btnAgregarFoto.setBackground(new Color(206, 244, 253));
        btnAgregarFoto.addActionListener(e -> seleccionarImagenes());
        panelForm.add(btnAgregarFoto);

        // Panel para mostrar previews de fotos
        panelPreviewFotos = new JPanel();
        panelPreviewFotos.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelPreviewFotos.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelPreviewFotos.setPreferredSize(new Dimension(380, 100));
        panelForm.add(new JScrollPane(panelPreviewFotos));

        // Listener para cambiar campos
        cmbTipo.addActionListener(e -> cardLayout.show(panelDinamico, (String) cmbTipo.getSelectedItem()));

        getContentPane().add(panelForm, BorderLayout.CENTER);

        // =======================================================
        // 🆕 PANEL INFERIOR CON BOTÓN CERRAR + PUBLICAR
        // =======================================================

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelInferior.setBackground(new Color(145, 78, 184));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(254, 150, 252));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnCerrar.addActionListener(e -> dispose());

        JButton btnPublicar = new JButton("PUBLICAR AHORA");
        btnPublicar.setBackground(new Color(46, 204, 113));
        btnPublicar.setForeground(Color.WHITE);
        btnPublicar.setFont(new Font("Arial", Font.BOLD, 14));
        btnPublicar.addActionListener(e -> manejarPublicacion());

        panelInferior.add(btnCerrar);
        panelInferior.add(btnPublicar);

        getContentPane().add(panelInferior, BorderLayout.SOUTH);
    }

    private void seleccionarImagenes() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes", "jpg", "jpeg", "png", "gif", "bmp"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            java.io.File[] archivos = fileChooser.getSelectedFiles();

            for (java.io.File archivo : archivos) {
                fotosSeleccionadas.add(archivo.getAbsolutePath());
            }

            actualizarPreviewFotos();
        }
    }

    private void actualizarPreviewFotos() {
        panelPreviewFotos.removeAll();

        for (int i = 0; i < fotosSeleccionadas.size(); i++) {
            final int index = i;
            String ruta = fotosSeleccionadas.get(i);

            JPanel cardFoto = new JPanel(new BorderLayout());
            cardFoto.setPreferredSize(new Dimension(80, 80));
            cardFoto.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            javax.swing.ImageIcon icon = util.ImageUtils.loadImage(ruta, 75, 75);
            JLabel lblFoto = new JLabel(icon);
            lblFoto.setHorizontalAlignment(SwingConstants.CENTER);

            JButton btnEliminar = new JButton("X");
            btnEliminar.setFont(new Font("Arial", Font.BOLD, 10));
            btnEliminar.setPreferredSize(new Dimension(20, 20));
            btnEliminar.setMargin(new Insets(0, 0, 0, 0));
            btnEliminar.addActionListener(e -> {
                fotosSeleccionadas.remove(index);
                actualizarPreviewFotos();
            });

            cardFoto.add(lblFoto, BorderLayout.CENTER);
            cardFoto.add(btnEliminar, BorderLayout.NORTH);

            panelPreviewFotos.add(cardFoto);
        }

        panelPreviewFotos.revalidate();
        panelPreviewFotos.repaint();
    }

    private void manejarSeleccionCategoria() {
        String seleccion = (String) cmbCategoria.getSelectedItem();
        if (seleccion != null && seleccion.equals("+ Agregar nueva categoría...")) {
            String nuevaCategoria = JOptionPane.showInputDialog(this,
                    "Ingrese el nombre de la nueva categoría:",
                    "Nueva Categoría",
                    JOptionPane.PLAIN_MESSAGE);

            if (nuevaCategoria != null && !nuevaCategoria.trim().isEmpty()) {
                model.ConfiguracionGlobal config = configRepo.obtenerConfiguracion();
                config.agregarCategoria(nuevaCategoria.trim());
                configRepo.actualizarConfiguracion(config);

                // Recargar el combo
                cmbCategoria.removeAllItems();
                for (String cat : config.getCategorias()) {
                    cmbCategoria.addItem(cat);
                }
                cmbCategoria.addItem("+ Agregar nueva categoría...");
                cmbCategoria.setSelectedItem(nuevaCategoria.trim());
            } else {
                cmbCategoria.setSelectedIndex(0);
            }
        }
    }

    private void manejarPublicacion() {
        String titulo = txtTitulo.getText().trim();
        String desc = txtDescripcion.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();
        String categoria = (String) cmbCategoria.getSelectedItem();
        String condicionStr = (String) cmbCondicion.getSelectedItem();

        // Validar categoría
        if (categoria == null || categoria.equals("+ Agregar nueva categoría...")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría válida", "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convertir condición string a enum
        util.CondicionArticulo condicion = convertirCondicion(condicionStr);

        String errorMessage = validarCamposComunes(titulo, desc);
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(this, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean exito = false;

        if (tipo.equals("SUBASTA")) {
            String precioTexto = txtPrecio.getText().trim();

            errorMessage = validarPrecioSubasta(precioTexto);
            if (errorMessage != null) {
                JOptionPane.showMessageDialog(this, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double precio = Double.parseDouble(precioTexto);
            exito = controller.crearSubasta(
                    titulo,
                    desc,
                    vendedor,
                    precio,
                    7,
                    new ArrayList<>(fotosSeleccionadas),
                    categoria,
                    condicion);
        } else {
            String deseos = txtDeseos.getText().trim();

            errorMessage = validarDeseosTrueque(deseos);
            if (errorMessage != null) {
                JOptionPane.showMessageDialog(this, errorMessage, "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            exito = controller.crearTrueque(
                    titulo,
                    desc,
                    vendedor,
                    deseos,
                    new ArrayList<>(fotosSeleccionadas),
                    categoria,
                    condicion);
        }

        if (exito) {
            if (fotosSeleccionadas.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Artículo publicado con éxito.\nNota: No agregaste fotos.",
                        "Publicación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "¡Artículo publicado con éxito!");
            }
            mainWindow.cargarPublicaciones();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la publicación.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private util.CondicionArticulo convertirCondicion(String condicionStr) {
        switch (condicionStr) {
            case "Nuevo":
                return util.CondicionArticulo.NUEVO;
            case "Usado como nuevo":
                return util.CondicionArticulo.USADO_COMO_NUEVO;
            case "Usado buen estado":
                return util.CondicionArticulo.USADO_BUEN_ESTADO;
            case "Aceptable":
                return util.CondicionArticulo.ACEPTABLE;
            default:
                return util.CondicionArticulo.NUEVO;
        }
    }

    private String validarCamposComunes(String titulo, String desc) {
        if (!util.ValidationUtils.isNotEmpty(titulo)) {
            return "El título es obligatorio";
        }
        if (!util.ValidationUtils.isLengthInRange(titulo, 5, 100)) {
            return "El título debe tener entre 5 y 100 caracteres";
        }

        if (!util.ValidationUtils.isNotEmpty(desc)) {
            return "La descripción es obligatoria";
        }
        if (!util.ValidationUtils.isLengthInRange(desc, 10, 500)) {
            return "La descripción debe tener entre 10 y 500 caracteres";
        }

        return null;
    }

    private String validarPrecioSubasta(String precioTexto) {
        if (!util.ValidationUtils.isNotEmpty(precioTexto)) {
            return "El precio mínimo es obligatorio";
        }

        Double precio = util.ValidationUtils.tryParseDouble(precioTexto);
        if (precio == null) {
            return "El precio debe ser un número válido";
        }

        if (precio <= 0) {
            return "El precio debe ser mayor a cero";
        }

        if (precio > 1500000000) {
            return "El precio no puede exceder $1,500,000,000 COP";
        }

        return null;
    }

    private String validarDeseosTrueque(String deseos) {
        if (!util.ValidationUtils.isNotEmpty(deseos)) {
            return "Debes especificar qué buscas a cambio";
        }

        if (!util.ValidationUtils.hasMinLength(deseos, 10)) {
            return "La descripción de lo que buscas debe tener al menos 10 caracteres";
        }

        return null;
    }
}
