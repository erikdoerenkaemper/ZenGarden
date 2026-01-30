package de.hsos.prog3.projekt.zengarden.model;

/**
 * Model des ZenGarden.
 * @author Erik Dörenkämper
 */
public class Garten {
    /**
     * Geld des Spielers.
     */
    int geld = 10000;

    /**
     * Array welches die Pflanzen des Gartens hält.
     */
    Pflanze[][] pflanzen = new Pflanze[6][3];

    /**
     * Das zur Laufzeit aktuell ausgewählte Werkzeug.
     */
    AusgewaehltesWerkzeug ausgewaehltesWerkzeug = AusgewaehltesWerkzeug.NICHTS;


    /**
     * Führt eine passende Methode auf dem angeklickten Topf aus.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @author Erik Dörenkämper
     */
    public void topfWirdAngeklickt(int x, int y){
        Pflanze pflanze = pflanzen[x][y];

        // Wenn Topf nicht leer ist
        if (pflanze != null){
            if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN){
                pflanzeVerkaufen(x,y);
            } else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERSCHIEBEN) {
                // TODO Verschieben hinzufügen
            } else {
                pflanze.pflanzeWirdAngeklickt(ausgewaehltesWerkzeug);
            }
        }
        // Wenn Topf leer ist
        else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.SAMEN){
            neuePflanzeKaufen(x,y);
        }
    }


    /**
     * Pflanzt bei ausreichendem Geld eine neue Pflanze im ausgewählten Topf.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @author Erik Dörenkämper
     */
    private void neuePflanzeKaufen(int x,int y){
        if (geld > 100) {
            pflanzen[x][y] = new Pflanze();
            geld -= 100;
        }
    }


    /**
     * Entfernt die Pflanze auf dem ausgewählten Topf und erhöht das Geld um den Wert der Pflanze.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @author Erik Dörenkämper
     */
    private void pflanzeVerkaufen(int x,int y){
        geld += pflanzen[x][y].berechneWertDerPflanze();
        pflanzen[x][y]  = null;
    }


    // getter und setter
    public Pflanze getPflanze(int x, int y){
        return pflanzen[x][y];
    }
    public void setPflanze(int x, int y, Pflanze pflanze){
        this.pflanzen[x][y] = pflanze;
    }
    public int getGeld() {
        return geld;
    }
    public void setGeld(int geld) {
        this.geld = geld;
    }
    public AusgewaehltesWerkzeug getWerkzeug() {
        return ausgewaehltesWerkzeug;
    }
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        this.ausgewaehltesWerkzeug = ausgewaehltesWerkzeug;
    }
}
