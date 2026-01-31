package de.hsos.prog3.projekt.zengarden.model;

/**
 * Model für eine Pflanze.
 * @author Erik Dörenkämper
 */
public class Pflanze {
    private final Pflanzenart pflanzenart;
    private Wachstumsphase wachstumsphase;
    private PflanzenEvent aktuellesEvent;
    private PflanzenEvent naechstesPflanzenEvent;
    private long zeitpunktDesNaechstenEvents;

    /**
     * Konstruktor für eine Pflanze.
     */
    public Pflanze(){
        pflanzenart = zufaelligePflanzenart();
        wachstumsphase = Wachstumsphase.KEIMLING;
        naechstesPflanzenEvent = PflanzenEvent.GIESSEN;
        aktuellesEvent = null;

        // Zufällige Zeit zwischen 20 und 80 Sekunden
        zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
    }

    /**
     * Führt eine passende Methode auf der angeklickten Pflanze aus.
     * @param ausgewaehltesWerkzeug Beim Anklicken ausgewähltes Werkzeug.
     * @author Erik Dörenkämper
     */
    public void pflanzeWirdAngeklickt(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        switch (ausgewaehltesWerkzeug){
            case GIESSKANNE:
                if (aktuellesEvent == PflanzenEvent.GIESSEN){
                    giessen();
                }
                break;
            case DUENGER:
                if (aktuellesEvent == PflanzenEvent.DUENGEN){
                    duengen();
                }
                break;
            default: break;
        }
    }

    /**
     * Triggert die passende Methode für das aktuelle Event.
     * @author Erik Dörenkämper
     */
    public void triggerEvent(){
        zeitpunktDesNaechstenEvents = 0;
        switch (naechstesPflanzenEvent) {
            // Bei GIESSEN und DUENGEN muss jeweils nur zum naechsten Event gewechselt werden
            case GIESSEN:
            case DUENGEN:
                aktuellesEvent = naechstesPflanzenEvent;
                break;
            case WACHSTUM:
                wachsen();
                break;
        }
    }


    // giessen und duengen könnte man als beduerfnissErfüllen() zusammenfassen
    private void giessen() {
        zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
        naechstesPflanzenEvent = zufaelligesPflanzenEvent();
        aktuellesEvent = null;


    }

    private void duengen() {
        zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
        naechstesPflanzenEvent = zufaelligesPflanzenEvent();
        aktuellesEvent = null;


    }



    /**
     * Erhöht die Wachstumsphase der Pflanze und setzt ein neues Event und neuen Zeitpunkt für das nächste Event.
     * Beim Erreichen der höchsten Stufe wird kein neues Event gesetzt.
     * @author Erik Dörenkämper
     */
    private void wachsen(){
        switch (wachstumsphase){
            case KEIMLING:
                zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
                naechstesPflanzenEvent = zufaelligesPflanzenEvent();
                wachstumsphase = Wachstumsphase.SAEMLING;
                break;
            case SAEMLING:
                zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
                naechstesPflanzenEvent = zufaelligesPflanzenEvent();
                wachstumsphase = Wachstumsphase.KLEIN;
                break;
            case KLEIN:
                wachstumsphase = Wachstumsphase.AUSGEWACHSEN;
                naechstesPflanzenEvent = null;
                zeitpunktDesNaechstenEvents = 0;
                break;
            default: break;
        }
        aktuellesEvent = null;
    }


    /**
     * Generiert eine zufällige Wartezeit zwischen 20 und 80 Sekunden.
     * @return Generierte Wartezeit
     * @author Erik Dörenkämper
     */
    private long zufaelligeWartezeit() {
        return System.currentTimeMillis() + 1000 * 20 + 1000 *  (long)(Math.random() * 60);
    }


    /**
     * <p>Generiert mit unterschiedlichen Wahrscheinlichkeiten ein zufälliges Event.<br>
     * Gießen -> 60%<br>
     * Düngen -> 20%<br>
     * Wachstum -> 20%</p>
     * @return Generiertes Event
     * @author Erik Dörenkämper
     */
    private PflanzenEvent zufaelligesPflanzenEvent() {
        PflanzenEvent zufaelligesPflanzenEvent;
        int zufall = (int) (Math.random() * 100);

        // 60% Giessen, 20% Düngen, 20% Wachstum
        if (zufall < 60){
            zufaelligesPflanzenEvent = PflanzenEvent.GIESSEN;
        } else if (zufall < 80){
            zufaelligesPflanzenEvent = PflanzenEvent.DUENGEN;
        } else {
            zufaelligesPflanzenEvent = PflanzenEvent.WACHSTUM;
        }

        return zufaelligesPflanzenEvent;
    }


    /**
     * <p>Generiert mit unterschiedlichen Wahrscheinlichkeiten eine zufällige Pflanzenart.<br>
     * Sonnenblume -> 50%<br>
     * Gänseblümchen -> 40%<br>
     * Rose -> 10%</p>
     * @return Generierte Pflanzenart
     * @author Erik Dörenkämper
     */
    private Pflanzenart zufaelligePflanzenart(){
        Pflanzenart zufaelligePflanzenart;
        int zufall = (int) (Math.random() * 100);

        // 50% Sonnenblumen, 40% Gänseblümchen, 10% Rose
        if (zufall < 50){
            zufaelligePflanzenart = Pflanzenart.SONNENBLUME;
        } else if (zufall < 90){
            zufaelligePflanzenart = Pflanzenart.GAENSEBLUEMCHEN;
        } else {
            zufaelligePflanzenart = Pflanzenart.ROSE;
        }

        return zufaelligePflanzenart;
    }

    // getter

    /**
     * Getter für die Zeitpunkt des nächsten Events.
     * @return Zeitpunkt des nächsten Events
     * @author Erik Dörenkämper
     */
    public long getZeitpunktDesNaechstenEvents() {
        return zeitpunktDesNaechstenEvents;
    }

    /**
     * Getter für die Pflanzenart.
     * @return Pflanzenart
     * @author Erik Dörenkämper
     */
    public Pflanzenart getPflanzenart() {
        return pflanzenart;
    }

    /**
     * Getter für die Wachstumsphase.
     * @return Wachstumsphase
     * @author Erik Dörenkämper
     */
    public Wachstumsphase getWachstumsphase() {
        return wachstumsphase;
    }

    /**
     * Getter für das aktuelle Event.
     * @return Aktuelles Event
     * @author Erik Dörenkämper
     */
    public PflanzenEvent getAktuellesEvent() {
        return aktuellesEvent;
    }


    /**
     * Berechnet den Wert der Pflanze anhand der Wachstumsstufe.
     * @return Wert der Pflanze
     */
    public int berechneWertDerPflanze(){
        if (wachstumsphase == Wachstumsphase.KEIMLING){
            return 100;
        } else if (wachstumsphase == Wachstumsphase.SAEMLING) {
            return 150;
        } else if (wachstumsphase == Wachstumsphase.KLEIN) {
            return 200;
        } else {
            return 300;
        }
    }
}
