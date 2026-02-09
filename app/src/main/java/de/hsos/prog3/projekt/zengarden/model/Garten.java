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
     * @return gibt das ausgeführte Event zurück
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    public BenutzerAktion topfWirdAngeklickt(int x, int y){
        Pflanze pflanze = pflanzen[x][y];

        if(pflanzeInDerHand != null){
            pflanzeAusDerHandEinpflanzen(pflanze,x,y);
            return null;
        }

        // Wenn Topf nicht leer ist
        if (pflanze != null){
            if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN){
                pflanzeVerkaufen(x,y);
                return BenutzerAktion.PFLANZE_VERKAUFT;
            } else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERSCHIEBEN) {
               pflanzeInDieHandNehmen(x,y,pflanze);
            } else {
                return pflanze.pflanzeWirdAngeklickt(ausgewaehltesWerkzeug);
            }
        }
        // Wenn Topf leer ist
        else if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.SAMEN){
            neuePflanzeKaufen(x,y);
            return BenutzerAktion.PFLANZE_GEKAUFT;
        }
        return null;
    }

    /**
     * Setzt bei leerem Topf die Pflanze in der Hand in diesen Topf.
     * @param angeklicktePflanze Pflanze/Topf die/der angeklickt wurde
     * @param x Spalte in die die Pflanze in der Hand gesetzt werden soll.
     * @param y Zeile in die die Pflanze in der Hand gesetzt werden soll.
     * @author Erik Dörenkämper
     */
    private void pflanzeAusDerHandEinpflanzen(Pflanze angeklicktePflanze, int x, int y){
        if(angeklicktePflanze == null){
            pflanzen[x][y] = pflanzeInDerHand;
            pflanzeInDerHand = null;
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

    /**
     * Hebt die Pflanze auf dem ausgewählten Topf auf, indem sie in die Hand genommen wird.
     * Der Topf an der ursprünglichen Position wird geleert.
     * @param x Spalte des Topfes der geleert werden muss
     * @param y Zeile des Topfes der geleert werden muss
     * @param pflanze Die Pflanze die in die Hand genommen werden soll
     * @author Jasper Groetzner
     */
    private void pflanzeInDieHandNehmen(int x,int y, Pflanze pflanze){
        pflanzeInDerHand = pflanze;
        pflanzen[x][y] = null;
    }


    // getter und setter
    /**
     * Getter für eine Pflanze im Garten.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @return Pflanze an der Position x und y
     * @author Erik Dörenkämper
     */
    public Pflanze getPflanze(int x, int y){
        return pflanzen[x][y];
    }

    /**
     * Setter für eine Pflanze im Garten.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @param pflanze Pflanze die gesetzt werden soll.
     * @author Erik Dörenkämper
     */
    public void setPflanze(int x, int y, Pflanze pflanze){
        this.pflanzen[x][y] = pflanze;
    }

    /**
     * Getter für das Geld des Spielers.
     * @return Geld des Spielers.
     * @author Erik Dörenkämper
     */
    public int getGeld() {
        return geld;
    }

    /**
     * Setter für das Geld des Spielers.
     * @param geld Neuer Geldwert.
     * @author Erik Dörenkämper
     */
    public void setGeld(int geld) {
        this.geld = geld;
    }

    /**
     * Geld des Spielers wird um den übergebenen Betrag verändert.
     * @param geld Betrag um den das Geld verändert werden soll.
     * @author Erik Dörenkämper
     */
    public void bucheGeld(int geld){
        this.geld += geld;
    }

    /**
     * Getter für das zur Laufzeit aktuell ausgewählte Werkzeug.
     * @return Das zur Laufzeit aktuell ausgewählte Werkzeug.
     * @author Erik Dörenkämper
     */
    public AusgewaehltesWerkzeug getWerkzeug() {
        return ausgewaehltesWerkzeug;
    }

    /**
     * Setter für das zur Laufzeit aktuell ausgewählte Werkzeug.
     * @param ausgewaehltesWerkzeug Das neue zur Laufzeit aktuell ausgewählte Werkzeug.
     * @author Erik Dörenkämper
     */
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        if(pflanzeInDerHand == null) {
            this.ausgewaehltesWerkzeug = ausgewaehltesWerkzeug;
        }
    }

    /**
     * Getter für die Pflanze in der Hand.
     * @return Die Pflanze in der Hand.
     */
    public Pflanze getPflanzeInDerHand() {
        return pflanzeInDerHand;
    }
}