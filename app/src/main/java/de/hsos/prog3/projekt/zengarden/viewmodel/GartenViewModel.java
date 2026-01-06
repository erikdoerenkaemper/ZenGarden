package de.hsos.prog3.projekt.zengarden.viewmodel;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;

public class GartenViewModel extends ViewModel {
    private final Garten garten;
    public GartenViewModel() {
        garten = new Garten();
        handler.post(runnableAllePflanzenpruefen);
    }











    // periodisches Ausführen der Methode allePflanzenPruefen()
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnableAllePflanzenpruefen = new Runnable() {
        @Override
        public void run() {
            allePflanzenPruefen();
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
