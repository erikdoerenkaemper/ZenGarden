package de.hsos.prog3.projekt.zengarden.viewmodel;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;

public class GartenViewModel extends ViewModel {
    private Garten garten;

    private final MutableLiveData<Integer> geldLiveData = new MutableLiveData<>();
    private final MutableLiveData<AusgewaehltesWerkzeug> ausgewaehltesWerkzeugMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Garten> gartenLiveData = new MutableLiveData<>();



    public GartenViewModel() {
        garten = new Garten();
        handler.post(runnableAllePflanzenpruefen);
        initialisiereViewModel();
    }

    private void initialisiereViewModel(){
        geldLiveData.setValue(garten.getGeld());
        ausgewaehltesWerkzeugMutableLiveData.setValue(garten.getWerkzeug());
        gartenLiveData.setValue(garten);
    }


    // Observable getter
    public LiveData<Integer> getGeldLiveData(){
        return geldLiveData;
    }
    public LiveData<AusgewaehltesWerkzeug> getWerkzeug() {
        return ausgewaehltesWerkzeugMutableLiveData;
    }
    public LiveData<Garten> getGartenLiveData() {
        return gartenLiveData;
    }







    // Deligierungen ans Model
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        garten.setWerkzeug(ausgewaehltesWerkzeug);
    }

    public void topfWirdAngeklickt(int x, int y){
        garten.topfWirdAngeklickt(x,y);
        gartenKopieren();
    }



    // Garten bei einer Änderung kopieren, damit LiveData aktiv wird
    // Die Methode funktionert auch ohne kopieren: Kopieren weg lassen oder drin lassen?
    private void gartenKopieren(){
//        Garten gartenKopie = new Garten();
//        gartenKopie.setWerkzeug(garten.getWerkzeug());
//        gartenKopie.setGeld(garten.getGeld());
//
//        for (int i = 0; i < 6; i++) {
//            for (int j = 0; j < 3; j++) {
//                Pflanze pflanze = garten.getPflanze(i, j);
//                if (pflanze == null) continue;
//                gartenKopie.setPflanze(i, j, pflanze);
//            }
//        }
//        garten = gartenKopie;
        gartenLiveData.setValue(garten);
    }


    // periodisches Ausführen der Methode allePflanzenPruefen()
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnableAllePflanzenpruefen = new Runnable() {
        @Override
        public void run() {
            allePflanzenPruefen();
            garten.setGeld(garten.getGeld() + 1);
            geldLiveData.setValue(garten.getGeld());
            handler.postDelayed(this, 1000);
        }
    };

    private void allePflanzenPruefen(){
        long aktuelleZeit = System.currentTimeMillis();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Pflanze pflanze = garten.getPflanze(i, j);
                if (pflanze == null) continue;
                if (pflanze.getZeitpunktDesNaechstenEvents() < aktuelleZeit) {
                    pflanze.triggerEvent();
                }
            }
        }
    }
}
