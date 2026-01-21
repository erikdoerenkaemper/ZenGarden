package de.hsos.prog3.projekt.zengarden.model;

public class Garten {
    int geld = 100;
    Pflanze[][] pflanzen = new Pflanze[6][3];
    AusgewaehltesWerkzeug ausgewaehltesWerkzeug = AusgewaehltesWerkzeug.NICHTS;

    public void topfWirdAngeklickt(int x, int y){
        Pflanze pflanze = pflanzen[x][y];

        // Wenn Topf nicht leer ist
        if (pflanze != null){
            if (ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN){
                geld += pflanze.berechneWertDerPflanze();
                pflanzen[x][y] = null;
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



    private void neuePflanzeKaufen(int x,int y){
        if (geld > 100) {
            pflanzen[x][y] = new Pflanze();
            geld -= 100;
        }
    }


    // getter und setter
    public Pflanze getPflanze(int x, int y){
        return pflanzen[x][y];
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
