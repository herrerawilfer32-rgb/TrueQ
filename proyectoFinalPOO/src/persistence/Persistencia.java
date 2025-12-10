/*
 * Clase: Persistencia
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Clase utilidad para serializaciÃ³n de objetos.
 */

package persistence;

import java.io.*;

public class Persistencia {

    /**
     * Guarda un objeto serializable en la ruta especificada.
     * Crea automáticamente los directorios padre si no existen.
     * 
     * @param ruta   Ruta del archivo (ej. "data/users.dat")
     * @param objeto Objeto a guardar (debe implementar Serializable)
     * @throws IOException Si ocurre un error de escritura
     */
    public static void guardarObjeto(String ruta, Object objeto) throws IOException {
        // Crear directorios padre si no existen
        File archivo = new File(ruta);
        File directorioPadre = archivo.getParentFile();

        if (directorioPadre != null && !directorioPadre.exists()) {
            boolean creado = directorioPadre.mkdirs();
            if (creado) {
                System.out.println("✓ Directorio creado: " + directorioPadre.getPath());
            }
        }

        // Guardar el objeto
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(objeto);
        }
    }

    /**
     * Carga un objeto desde la ruta especificada.
     * 
     * @param ruta Ruta del archivo
     * @return El objeto cargado
     * @throws IOException            Si ocurre un error de lectura
     * @throws ClassNotFoundException Si la clase del objeto no se encuentra
     */
    public static Object cargarObjeto(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            return ois.readObject();
        }
    }
}
