package de.hsos.prog3.projekt.zengarden.model;

public class Garten {
    int geld;
    Pflanze[][] pflanzen = new Pflanze[6][4];




    public Pflanze getPflanze(int x, int y){
        return pflanzen[x][y];
    }








}
