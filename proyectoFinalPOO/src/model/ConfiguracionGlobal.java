/*
 * Clase: ConfiguracionGlobal
 * Autores: Anggel Leal, Wilfer Herrera, David Santos
 * DescripciÃ³n: Modelo de datos.
 */

package model;

/**
 * Configuración global del sistema que puede ser modificada por
 * administradores.
 */
public class ConfiguracionGlobal implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int diasMaximosSubasta;
    private double comisionPlataforma;
    private int maxImagenesPorPublicacion;
    private boolean permitirOfertasAnonimas;
    private int tiempoExpiracionOfertaHoras;
    private java.util.List<String> categorias;

    /**
     * Constructor con valores por defecto
     */
    public ConfiguracionGlobal() {
        this.diasMaximosSubasta = 30;
        this.comisionPlataforma = 0.05; // 5%
        this.maxImagenesPorPublicacion = 5;
        this.permitirOfertasAnonimas = false;
        this.tiempoExpiracionOfertaHoras = 48;

        // Categorías por defecto
        this.categorias = new java.util.ArrayList<>();
        this.categorias.add("Electrónica");
        this.categorias.add("Ropa y Accesorios");
        this.categorias.add("Hogar y Jardín");
        this.categorias.add("Deportes");
        this.categorias.add("Libros y Música");
        this.categorias.add("Juguetes y Juegos");
        this.categorias.add("Vehículos");
        this.categorias.add("Inmuebles");
        this.categorias.add("Servicios");
        this.categorias.add("Otros");
    }

    // Getters
    public int getDiasMaximosSubasta() {
        return diasMaximosSubasta;
    }

    public double getComisionPlataforma() {
        return comisionPlataforma;
    }

    public int getMaxImagenesPorPublicacion() {
        return maxImagenesPorPublicacion;
    }

    public boolean isPermitirOfertasAnonimas() {
        return permitirOfertasAnonimas;
    }

    public int getTiempoExpiracionOfertaHoras() {
        return tiempoExpiracionOfertaHoras;
    }

    // Setters
    public void setDiasMaximosSubasta(int diasMaximosSubasta) {
        if (diasMaximosSubasta > 0) {
            this.diasMaximosSubasta = diasMaximosSubasta;
        }
    }

    public void setComisionPlataforma(double comisionPlataforma) {
        if (comisionPlataforma >= 0 && comisionPlataforma <= 1) {
            this.comisionPlataforma = comisionPlataforma;
        }
    }

    public void setMaxImagenesPorPublicacion(int maxImagenesPorPublicacion) {
        if (maxImagenesPorPublicacion > 0) {
            this.maxImagenesPorPublicacion = maxImagenesPorPublicacion;
        }
    }

    public void setPermitirOfertasAnonimas(boolean permitirOfertasAnonimas) {
        this.permitirOfertasAnonimas = permitirOfertasAnonimas;
    }

    public void setTiempoExpiracionOfertaHoras(int tiempoExpiracionOfertaHoras) {
        if (tiempoExpiracionOfertaHoras > 0) {
            this.tiempoExpiracionOfertaHoras = tiempoExpiracionOfertaHoras;
        }
    }

    public java.util.List<String> getCategorias() {
        return new java.util.ArrayList<>(categorias);
    }

    public void setCategorias(java.util.List<String> categorias) {
        if (categorias != null) {
            this.categorias = new java.util.ArrayList<>(categorias);
        }
    }

    public void agregarCategoria(String categoria) {
        if (categoria != null && !categoria.trim().isEmpty() && !categorias.contains(categoria)) {
            this.categorias.add(categoria);
        }
    }

    public void eliminarCategoria(String categoria) {
        this.categorias.remove(categoria);
    }
}
