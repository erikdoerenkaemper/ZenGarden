package de.hsos.prog3.projekt.zengarden.viewmodel;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;

public class GartenViewModel extends ViewModel {
    private final Garten garten;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnableAllePflanzenpruefen = new Runnable() {
        @Override
        public void run() {
            allePflanzenpruefen();
            handler.postDelayed(this, 1000);
        }
    };


    public GartenViewModel() {
        garten = new Garten();
        handler.post(runnableAllePflanzenpruefen);
    }



    private void allePflanzenpruefen(){
        long aktuelleZeit = System.currentTimeMillis();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Pflanze pflanze = garten.getPflanze(i, j);
                if (pflanze.getZeitpunktDesNaechstenEvents() < aktuelleZeit) {
                    pflanze.triggerEvent();

                }

            }
        }
    }





}
