package de.hsos.prog3.projekt.zengarden.model;

/**
 * Enum der unterschiedlichen Wachstumsphasen.
 * @author Erik Dörenkämper
 */
public enum Wachstumsphase {
    KEIMLING(100),
    SAEMLING(150),
    KLEIN(200),
    AUSGEWACHSEN(300);
    public final int basisWert;
    Wachstumsphase(int basisWert) {
        this.basisWert = basisWert;
    }

    public int getBasisWert() {
        return basisWert;
    }
}
