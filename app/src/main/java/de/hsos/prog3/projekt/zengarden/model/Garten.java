package de.hsos.prog3.projekt.zengarden.model;

/**
 * Model des ZenGarden.
 * @author Erik Dörenkämper
 */
public class Garten {
    /**
     * Geld des Spielers.
     */
    int geld = 1000;

    /**
     * Array welches die Pflanzen des Gartens hält.
     */
    Pflanze[][] pflanzen = new Pflanze[6][3];

    /**
     * Pflanze die in der Hand gehalten wird.
     */
    Pflanze pflanzeInDerHand = null;

    /**
     * Das zur Laufzeit aktuell ausgewählte Werkzeug.
     */
    AusgewaehltesWerkzeug ausgewaehltesWerkzeug = null;


    /**
     * Führt eine passende Methode auf dem angeklickten Topf aus.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @return gibt ein Array mit der Aktion und optionalen Zusatzdaten zurück.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    public Object[] topfWirdAngeklickt(int x, int y){
        Pflanze pflanze = pflanzen[x][y];

        if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERSCHIEBEN) {
            if (pflanzeInDerHand == null) {
                if (pflanze != null) {
                    pflanzeVerschieben(x, y);
                    return new Object[]{BenutzerAktion.PFLANZE_VERSCHOBEN};
                }
            } else {
                if (pflanze == null) {
                    pflanzeVerschieben(x, y);
                    return new Object[]{BenutzerAktion.PFLANZE_WIEDEREINGEPFLANZT};
                }
            }
            return null; // No valid move action
        }

        if (pflanze != null){
            if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN){
                int verkaufswert = pflanzeVerkaufen(x,y);
                return new Object[]{BenutzerAktion.PFLANZE_VERKAUFT, verkaufswert};
            } else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.GIESSKANNE) {
                pflanze.pflanzeWirdAngeklickt(ausgewaehltesWerkzeug);
                return new Object[]{BenutzerAktion.GIESSEN};
            } else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.DUENGER) {
                pflanze.pflanzeWirdAngeklickt(ausgewaehltesWerkzeug);
                return new Object[]{BenutzerAktion.DUENGEN};
            }
        }
        else {
            if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.SAMEN){
                int kosten = neuePflanzeKaufen(x,y);
                if (kosten > 0) {
                    return new Object[]{BenutzerAktion.PFLANZE_GEKAUFT, kosten};
                }
            }
        }
        return null;
    }

    /**
     * Verschiebt eine Pflanze von einem Topf in einen anderen.
     * @param x Spalte des angeklickten Topfes.
     * @param y Zeile des angeklickten Topfes.
     * @author Jasper Groetzner
     */
    private void pflanzeVerschieben(int x, int y) {
        if (pflanzeInDerHand == null) {
            if (pflanzen[x][y] != null) {
                pflanzeInDerHand = pflanzen[x][y];
                pflanzen[x][y] = null;
            }
        } else {
            if (pflanzen[x][y] == null) {
                pflanzen[x][y] = pflanzeInDerHand;
                pflanzeInDerHand = null;
            }
        }
    }

    /**
     * Pflanzt bei ausreichendem Geld eine neue Pflanze im ausgewählten Topf und gibt die Kosten zurück.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @return Kosten der Pflanze oder 0 wenn nicht erfolgreich.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private int neuePflanzeKaufen(int x,int y){
        if (geld >= 100) {
            pflanzen[x][y] = new Pflanze();
            geld -= 100;
            return 100;
        }
        return 0;
    }

    /**
     * Entfernt die Pflanze und gibt ihren Wert zurück.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @return Der Wert der verkauften Pflanze.
     * @author Erik Dörenkämper
     */
    private int pflanzeVerkaufen(int x,int y){
        int verkaufswert = pflanzen[x][y].berechneWertDerPflanze();
        geld += verkaufswert;
        pflanzen[x][y]  = null;
        return verkaufswert;
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

    public void bucheGeld(int geld){
        this.geld += geld;
    }

    public AusgewaehltesWerkzeug getWerkzeug() {
        return ausgewaehltesWerkzeug;
    }

    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        if(pflanzeInDerHand == null) {
            this.ausgewaehltesWerkzeug = ausgewaehltesWerkzeug;
        }
    }

    public Pflanze getPflanzeInDerHand() {
        return pflanzeInDerHand;
    }
}
