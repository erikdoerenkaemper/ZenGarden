package de.hsos.prog3.projekt.zengarden.model;

/**
 * Enum der unterschiedlichen Pflanzenarten.
 * @author Erik Dörenkämper
 */
public enum Pflanzenart {
    SONNENBLUME(1.0f),
    GAENSEBLUEMCHEN(1.3f),
    ROSE (1.7f),
    EISSONNENBLUME(2.0f),
    EISGAENSEBLUEMCHEN(2.5f);
    public final float wertMultiplikator;

    Pflanzenart(float wertMultiplikator) {
        this.wertMultiplikator = wertMultiplikator;
    }

    public float getWertMultiplikator() {
        return wertMultiplikator;
    }
}
