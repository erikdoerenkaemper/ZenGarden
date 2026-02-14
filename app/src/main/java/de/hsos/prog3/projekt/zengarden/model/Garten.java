package de.hsos.prog3.projekt.zengarden.model;

/**
 * Model des ZenGarden.
 * @author Erik Dörenkämper
 */
public class Garten {
    /**
     * Geld des Spielers.
     */
    int geld = 200;

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
            return null;
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


    /**
     * Gibt eine Pflanze an einer bestimmten Stelle im Garten zurück.
     * @param x Die Spalte der Pflanze im Garten.
     * @param y Die Zeile der Pflanze im Garten.
     * @return Das Pflanzenobjekt an der angegebenen Position oder null, wenn dort keine Pflanze ist.
     */
    public Pflanze getPflanze(int x, int y){
        return pflanzen[x][y];
    }

    /**
     * Setzt eine Pflanze an eine bestimmte Position im Garten.
     * @param x Die Spalte (x-Koordinate) im Garten, an der die Pflanze platziert werden soll.
     * @param y Die Zeile (y-Koordinate) im Garten, an der die Pflanze platziert werden soll.
     * @param pflanze Die Pflanze, die an der angegebenen Position gesetzt werden soll.
     * @author Erik Dörenkämper
     */
    public void setPflanze(int x, int y, Pflanze pflanze){
        this.pflanzen[x][y] = pflanze;
    }

    /**
     * Gibt das aktuelle Geld des Spielers zurück.
     * @return Geld des Spielers.
     * @author Erik Dörenkämper
     */
    public int getGeld() {
        return geld;
    }

    /**
     * Setzt das Geld des Spielers auf einen bestimmten Wert.
     * @param geld der neue Geldwert.
     * @author Erik Dörenkämper
     */
    public void setGeld(int geld) {
        this.geld = geld;
    }

    /**
     * Bucht einen Geldbetrag auf das Konto des Spielers.
     * Kann für positive und negative Beträge verwendet werden.
     * @param geld Der zu buchende Betrag.
     * @author Erik Dörenkämper
     */
    public void bucheGeld(int geld){
        this.geld += geld;
    }

    /**
     * Gibt das aktuell ausgewählte Werkzeug zurück.
     * @return das ausgewählte Werkzeug.
     * @author Erik Dörenkämper
     */
    public AusgewaehltesWerkzeug getWerkzeug() {
        return ausgewaehltesWerkzeug;
    }

    /**
     * Setzt das aktuell ausgewählte Werkzeug.
     * Das Werkzeug kann nur geändert werden, wenn keine Pflanze in der Hand gehalten wird.
     * @param ausgewaehltesWerkzeug Das zu setzende Werkzeug.
     * @author Jasper Groetzner
     */
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        if(pflanzeInDerHand == null) {
            this.ausgewaehltesWerkzeug = ausgewaehltesWerkzeug;
        }
    }

    /**
     * Gibt die Pflanze zurück, die der Spieler aktuell in der Hand hält (beim Verschieben).
     *
     * @return Die Pflanze in der Hand oder null, wenn keine Pflanze gehalten wird.
     * @author Erik Dörenkämper
     */
    public Pflanze getPflanzeInDerHand() {
        return pflanzeInDerHand;
    }
}
