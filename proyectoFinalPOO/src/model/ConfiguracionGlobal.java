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

    /**
     * Constructor con valores por defecto
     */
    public ConfiguracionGlobal() {
        this.diasMaximosSubasta = 30;
        this.comisionPlataforma = 0.05; // 5%
        this.maxImagenesPorPublicacion = 5;
        this.permitirOfertasAnonimas = false;
        this.tiempoExpiracionOfertaHoras = 48;
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
}
