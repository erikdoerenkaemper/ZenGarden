package de.hsos.prog3.projekt.zengarden.model;

public class Pflanze {
    private final Pflanzenart pflanzenart;
    private Wachstumsphase wachstumsphase;
    private PflanzenEvent aktuellesEvent;
    private PflanzenEvent naechstesPflanzenEvent;
    private long zeitpunktDesNaechstenEvents;

    public Pflanze(){
        pflanzenart = zufaelligePflanzenart();
        wachstumsphase = Wachstumsphase.KEIMLING;
        naechstesPflanzenEvent = PflanzenEvent.GIESSEN;
        aktuellesEvent = null;

        // Zufällige Zeit zwischen 20 und 80 Sekunden
        zeitpunktDesNaechstenEvents = zufaelligeWartezeit();
    }


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


    public void triggerEvent(){
        zeitpunktDesNaechstenEvents = 0;
        switch (naechstesPflanzenEvent) {
            case GIESSEN:
                willWasser();
                break;
            case DUENGEN:
                willDuenger();
                break;
            case WACHSTUM:
                wachsen();
                break;
        }
    }


    private void willWasser(){
        aktuellesEvent = naechstesPflanzenEvent;


    }


    private void willDuenger(){
        aktuellesEvent = naechstesPflanzenEvent;




    }



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


    private long zufaelligeWartezeit() {
        return System.currentTimeMillis() + 1000 * 20 + 1000 *  (long)(Math.random() * 60);
    }

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
    public long getZeitpunktDesNaechstenEvents() {
        return zeitpunktDesNaechstenEvents;
    }
    public Pflanzenart getPflanzenart() {
        return pflanzenart;
    }
    public Wachstumsphase getWachstumsphase() {
        return wachstumsphase;
    }
    public PflanzenEvent getAktuellesEvent() {
        return aktuellesEvent;
    }


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
