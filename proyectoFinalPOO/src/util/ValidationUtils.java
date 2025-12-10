/*
 * Clase: ValidationUtils
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Clase utilidad.
 */

package util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para validaciones de formularios.
 */
public class ValidationUtils {

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern ALPHABETIC_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");

    /**
     * Verifica si un texto no está vacío (después de trim).
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Verifica si un texto tiene al menos la longitud mínima.
     */
    public static boolean hasMinLength(String text, int min) {
        return text != null && text.trim().length() >= min;
    }

    /**
     * Verifica si un texto no excede la longitud máxima.
     */
    public static boolean hasMaxLength(String text, int max) {
        return text != null && text.trim().length() <= max;
    }

    /**
     * Verifica si un texto está en el rango de longitud especificado.
     */
    public static boolean isLengthInRange(String text, int min, int max) {
        return hasMinLength(text, min) && hasMaxLength(text, max);
    }

    /**
     * Verifica si un texto es alfanumérico (letras, números y guión bajo).
     */
    public static boolean isAlphanumeric(String text) {
        return text != null && ALPHANUMERIC_PATTERN.matcher(text).matches();
    }

    /**
     * Verifica si un texto contiene solo números.
     */
    public static boolean isNumeric(String text) {
        return text != null && NUMERIC_PATTERN.matcher(text).matches();
    }

    /**
     * Verifica si un texto contiene solo letras y espacios.
     */
    public static boolean isAlphabetic(String text) {
        return text != null && ALPHABETIC_PATTERN.matcher(text).matches();
    }

    /**
     * Verifica si un email tiene formato válido.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Verifica si una contraseña es válida (mínimo 6 caracteres, al menos una letra
     * y un número).
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");

        return hasLetter && hasDigit;
    }

    /**
     * Verifica si una cédula es válida (solo números, 6-15 dígitos).
     */
    public static boolean isValidCedula(String cedula) {
        return cedula != null &&
                isNumeric(cedula) &&
                isLengthInRange(cedula, 6, 15);
    }

    /**
     * Verifica si un texto representa un número positivo.
     */
    public static boolean isPositiveNumber(String text) {
        try {
            double value = Double.parseDouble(text.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si un valor está en el rango especificado.
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Intenta parsear un texto a double, retorna null si falla.
     */
    public static Double tryParseDouble(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Obtiene un mensaje de error descriptivo para validación de longitud.
     */
    public static String getLengthErrorMessage(String fieldName, int min, int max) {
        if (max == Integer.MAX_VALUE) {
            return fieldName + " debe tener al menos " + min + " caracteres";
        }
        return fieldName + " debe tener entre " + min + " y " + max + " caracteres";
    }
}
