/**
 * Clase: ImageUtils
 * clase de utilidad
 * @author Anggel Leal, Wilfer Herrera, David Santos
 * @version 1.2
 */

package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Utilidades para manejo de imágenes en la aplicación.
 */
public class ImageUtils {

    /**
     * Carga una imagen desde un path y la redimensiona.
     */
    public static ImageIcon loadImage(String path, int width, int height) {
        try {
            File imgFile = new File(path);
            if (!imgFile.exists()) {
                return getPlaceholderImage(width, height);
            }

            BufferedImage img = ImageIO.read(imgFile);
            if (img == null) {
                return getPlaceholderImage(width, height);
            }

            return scaleImage(new ImageIcon(img), width, height);
        } catch (Exception e) {
            return getPlaceholderImage(width, height);
        }
    }

    /**
     * Crea una imagen placeholder cuando no hay foto disponible.
     */
    public static ImageIcon getPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = placeholder.createGraphics();

        // Fondo gris claro
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // Borde
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRect(0, 0, width - 1, height - 1);

        // Icono de imagen
        g2d.setColor(new Color(180, 180, 180));
        int iconSize = Math.min(width, height) / 3;
        int x = (width - iconSize) / 2;
        int y = (height - iconSize) / 2;

        // Dibujar un rectángulo con una montaña simple
        g2d.fillRect(x, y, iconSize, iconSize);
        g2d.setColor(new Color(220, 220, 220));
        int[] xPoints = { x, x + iconSize / 2, x + iconSize };
        int[] yPoints = { y + iconSize, y + iconSize / 2, y + iconSize };
        g2d.fillPolygon(xPoints, yPoints, 3);

        // Texto
        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String text = "Sin imagen";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (width - textWidth) / 2, height - 10);

        g2d.dispose();
        return new ImageIcon(placeholder);
    }

    /**
     * Escala una imagen manteniendo la proporción.
     */
    public static ImageIcon scaleImage(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();

        // Calcular dimensiones manteniendo proporción
        int originalWidth = img.getWidth(null);
        int originalHeight = img.getHeight(null);

        double scaleX = (double) width / originalWidth;
        double scaleY = (double) height / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
