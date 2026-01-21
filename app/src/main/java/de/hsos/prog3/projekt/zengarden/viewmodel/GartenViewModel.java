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
    private final Garten garten;

    private final MutableLiveData<Integer> geld = new MutableLiveData<>();
    private final MutableLiveData<AusgewaehltesWerkzeug> ausgewaehltesWerkzeug = new MutableLiveData<>();


    public GartenViewModel() {
        garten = new Garten();
        handler.post(runnableAllePflanzenpruefen);
        initialisiereViewModel();
    }

    private void initialisiereViewModel(){
        geld.setValue(garten.getGeld());
        ausgewaehltesWerkzeug.setValue(garten.getWerkzeug());
    }


    // Observable getter
    public LiveData<Integer> getGeld(){
        return geld;
    }
    public LiveData<AusgewaehltesWerkzeug> getWerkzeug() {
        return ausgewaehltesWerkzeug;
    }





    // Deligierungen ans Model
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        garten.setWerkzeug(ausgewaehltesWerkzeug);
    }

    public void topfWirdAngeklickt(int x, int y){
        garten.topfWirdAngeklickt(x,y);
    }



    // periodisches Ausführen der Methode allePflanzenPruefen()
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnableAllePflanzenpruefen = new Runnable() {
        @Override
        public void run() {
            allePflanzenPruefen();
            garten.setGeld(garten.getGeld() + 1);
            geld.setValue(garten.getGeld());
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
