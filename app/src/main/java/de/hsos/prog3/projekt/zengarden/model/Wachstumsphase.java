package de.hsos.prog3.projekt.zengarden.model;

/**
 * Enum der unterschiedlichen Wachstumsphasen.
 * @author Erik Dörenkämper, Jasper Groetzner
 */
public enum Wachstumsphase {
    KEIMLING(50),
    SAEMLING(75),
    KLEIN(100),
    AUSGEWACHSEN(200);
    public final int basisWert;
    Wachstumsphase(int basisWert) {
        this.basisWert = basisWert;
    }

    public int getBasisWert() {
        return basisWert;
    }
}
