/**
 * Clase: UIConstants
 * Constantes de diseño para mantener consistencia visual
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.3
 */
package util;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class UIConstants {

    // Colores principales
    public static final Color MORADO_PRINCIPAL = new Color(46, 0, 108);
    public static final Color MORADO_SECUNDARIO = new Color(100, 44, 169);
    public static final Color DORADO = new Color(240, 201, 108);
    public static final Color DORADO_CLARO = new Color(235, 203, 129);
    public static final Color VERDE_AZULADO = new Color(106, 153, 149);

    // Colores de acción
    public static final Color VERDE_EXITO = new Color(46, 204, 113);
    public static final Color ROJO_PELIGRO = new Color(192, 57, 43);
    public static final Color ROSA_ACENTO = new Color(254, 120, 251);
    public static final Color GRIS_NEUTRAL = new Color(189, 195, 199);

    // Colores adicionales
    public static final Color BLANCO = Color.WHITE;
    public static final Color NEGRO = Color.BLACK;

    // Fuentes
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FUENTE_BOTON = new Font("SansSerif", Font.BOLD, 12);

    // Bordes
    public static final Border BORDE_VACIO_10 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    public static final Border BORDE_VACIO_20 = BorderFactory.createEmptyBorder(20, 20, 20, 20);

    // Constructor privado para evitar instanciación
    private UIConstants() {
    }
}
