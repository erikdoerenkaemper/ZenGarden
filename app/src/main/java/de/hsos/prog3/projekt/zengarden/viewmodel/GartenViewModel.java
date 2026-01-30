package de.hsos.prog3.projekt.zengarden.viewmodel;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;

public class GartenViewModel extends ViewModel {
    private Garten garten;
    SharedPreferences sharedPreferences;

    private final MutableLiveData<Integer> geldLiveData = new MutableLiveData<>();
    private final MutableLiveData<AusgewaehltesWerkzeug> ausgewaehltesWerkzeugMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Garten> gartenLiveData = new MutableLiveData<>();


    public void initialisiereViewModel(SharedPreferences sharedPreferences){
        // garten laden
        this.sharedPreferences = sharedPreferences;
        garten = gartenLaden(this.sharedPreferences);

        // Handler für periodisches Ausführen der Methode allePflanzenpruefen()
        handler.post(runnableAllePflanzenpruefen);

        // LiveData setzen
        geldLiveData.setValue(garten.getGeld());
        ausgewaehltesWerkzeugMutableLiveData.setValue(garten.getWerkzeug());
        gartenLiveData.setValue(garten);
    }


    // Observable getter
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
        gartenAktuaisieren();
        gartenSpeichern(sharedPreferences); // bei jeder Änderung des Gartens wird gespeichert
    }



    // Garten bei einer Änderung kopieren, damit LiveData aktiv wird
    // Die Methode funktionert auch ohne kopieren: Kopieren weg lassen oder drin lassen?
    private void gartenAktuaisieren(){
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
        boolean eventWurdeGetriggert = false;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Pflanze pflanze = garten.getPflanze(i, j);
                if (pflanze == null) continue;

                // zeitpunktDesNaechstenEvents = 0 zeigt an, dass nicht mehr getriggert werden muss
                if (pflanze.getZeitpunktDesNaechstenEvents() < aktuelleZeit && pflanze.getZeitpunktDesNaechstenEvents() != 0) {
                    pflanze.triggerEvent();
                    eventWurdeGetriggert = true;
                }
            }
        }
        if (eventWurdeGetriggert) {
            gartenAktuaisieren();
        }
    }



    // Speichern und Laden des Gartens
    public Garten gartenLaden(SharedPreferences sharedPreferences){
        String json = sharedPreferences.getString("garten", null);

        if (json == null){
            return new Garten();
        }

        Gson gson = new Gson();
        return gson.fromJson(json, Garten.class);

    }

    public void gartenSpeichern(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();
        String json = gson.toJson(garten);

        sharedPreferences.edit().putString("garten", json).apply();
    }
}
