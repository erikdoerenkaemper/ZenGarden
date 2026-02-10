package de.hsos.prog3.projekt.zengarden.viewmodel;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;

/**
 * ViewModel des ZenGarden.
 * @author Erik Dörenkämper
 */
public class GartenViewModel extends ViewModel {
    /**
     * Model des Gartens.
     */
    private Garten garten;
    /**
     * SharedPreference Objekt für die Persistierung des Gartens.
     */
    SharedPreferences gartenSharedPreferences;
    /**
     * Handler für periodisches Prüfen aller Pflanzen auf Events.
     */
    Handler handler = new Handler(Looper.getMainLooper());
    /**
     * LiveData für das Geld
     */
    private final MutableLiveData<Integer> geldLiveData = new MutableLiveData<>();
    /**
     * LiveData für das zur Laufzeit aktuell ausgewählte Werkzeug.
     */
    private final MutableLiveData<AusgewaehltesWerkzeug> ausgewaehltesWerkzeugMutableLiveData = new MutableLiveData<>();
    /**
     * LiveData für den gesamten Garten.
     */
    private final MutableLiveData<Garten> gartenLiveData = new MutableLiveData<>();

    private final MutableLiveData<Object[]> benutzerAktion = new MutableLiveData<>();

    /**
     * <p>Initialisiert das ViewModel.<br>
     * 1. Gartenobjekt wird geladen.<br>
     * 2. Handler für das periodische Prüfen aller Pflanzen auf Events wird gestartet.<br>
     * 3. LiveDatas werden gesetzt.</p>
     *
     * @author Erik Dörenkämper
     * @param sharedPreferences SharedPreference Objekt für die Persistierung des Gartens.
     */
    public void initialisiereViewModel(SharedPreferences sharedPreferences){
        // garten laden
        this.gartenSharedPreferences = sharedPreferences;
        garten = gartenLaden(this.gartenSharedPreferences);

        // Handler für periodisches Ausführen der Methode allePflanzenpruefen()
        handler.post(runnableAllePflanzenpruefen);

        // LiveData setzen
        geldLiveData.setValue(garten.getGeld());
        ausgewaehltesWerkzeugMutableLiveData.setValue(garten.getWerkzeug());
        gartenLiveData.setValue(garten);
    }


    // Observable getter
    /**
     * @author Erik Dörenkämper
     * @return LiveData für das zur Laufzeit aktuell ausgewählte Werkzeug.
     */
    public LiveData<AusgewaehltesWerkzeug> getWerkzeug() {
        return ausgewaehltesWerkzeugMutableLiveData;
    }

    /**
     * @author Erik Dörenkämper
     * @return LiveData für den gesamten Garten.
     */
    public LiveData<Garten> getGartenLiveData() {
        return gartenLiveData;
    }

    public LiveData<Object[]> getBenutzerAktion() {
        return benutzerAktion;
    }


    // Deligierungen ans Model
    /**
     * Leitet den Klick auf eines der Werkzeuge an as Model weiter.
     * @author Erik Dörenkämper
     * @param ausgewaehltesWerkzeug Werkzeug das angeklickt wurde.
     */
    public void setWerkzeug(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        garten.setWerkzeug(ausgewaehltesWerkzeug);
        ausgewaehltesWerkzeugMutableLiveData.setValue(ausgewaehltesWerkzeug);
    }


    /**
     * Verarbeitet einen Klick auf einen Topf im Garten.
     *
     * Diese Methode delegiert die Klick-Logik an das Garten-Modell. Abhängig von der
     * ausgeführten Aktion (z.B. pflanzen, gießen) wird ein entsprechendes Event für die View
     * ausgelöst. Anschließend wird der Garten gespeichert und die LiveData-Objekte werden
     * aktualisiert, um die UI auf den neuesten Stand zu bringen.
     *
     * @param x Die x-Koordinate (Spalte) des angeklickten Topfes.
     * @param y Die y-Koordinate (Zeile) des angeklickten Topfes.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    public void topfWirdAngeklickt(int x, int y){
        Object[] aktionResult = garten.topfWirdAngeklickt(x, y);
        if (aktionResult != null) {
            Object[] viewEvent;
            if (aktionResult.length > 1) {
                // Aktion mit Zusatzdaten (z.B. PFLANZE_VERKAUFT)
                viewEvent = new Object[]{aktionResult[0], x, y, aktionResult[1]};
            } else {
                // Aktion ohne Zusatzdaten
                viewEvent = new Object[]{aktionResult[0], x, y};
            }
            benutzerAktion.setValue(viewEvent);
        }
        gartenAktualisieren();
        gartenSpeichern(gartenSharedPreferences);
    }



    // Garten erneut ins LiveData setzen, damit LiveData aktiv wird
    /**
     * Setzt erneut den Garten in das LiveData Objekt, damit LiveData aktiv wird.
     * @author Erik Dörenkämper
     */
    private void gartenAktualisieren(){
        gartenLiveData.setValue(garten);
    }



    // periodisches Ausführen der Methode allePflanzenPruefen()
    /**
     * Runnable für das periodische Ausführen der Methode allePflanzenPruefen().
     */
    // TODO prüfen ob man den Handler auch in einen anderen Thread auslagern kann.
    Runnable runnableAllePflanzenpruefen = new Runnable() {
        @Override
        public void run() {
            allePflanzenPruefen();
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Überprüft alle Pflanzen im Garten auf Events.
     * Dazu wird die aktuelle Zeit mit dem Zeitpunkt des nächsten Events der Pflanze verglichen.
     * Wenn der Zeitpunkt des nächsten Events in der Vergangenheit liegt, wird das Event getriggert.
     * Wenn ein Event getriggert wurde, wird das LiveData Objekt des Gartens aktualisiert.
     * @author Erik Dörenkämper
     */
    private void allePflanzenPruefen(){
        long aktuelleZeit = System.currentTimeMillis();
        boolean eventWurdeGetriggert = false;
        int geld = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                Pflanze pflanze = garten.getPflanze(i, j);
                if (pflanze == null) continue;

                // zeitpunktDesNaechstenEvents = 0 zeigt an, dass nicht mehr getriggert werden muss
                if (pflanze.getZeitpunktDesNaechstenEvents() < aktuelleZeit && pflanze.getZeitpunktDesNaechstenEvents() != 0) {
                    geld += pflanze.triggerEvent();
                    eventWurdeGetriggert = true;
                }
            }
        }
        if (eventWurdeGetriggert) {
            garten.bucheGeld(geld);
            gartenAktualisieren();
        }
    }



    // Speichern und Laden des Gartens
    /**
     * Laden des Gartens aus dem SharedPreferences.
     * @param sharedPreferences SharedPreference Objekt für den Garten.
     * @return Garten
     * @author Erik Dörenkämper
     */
    public Garten gartenLaden(SharedPreferences sharedPreferences){
        String json = sharedPreferences.getString("garten", null);

        if (json == null){
            return new Garten();
        }


        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Garten> jsonAdapter = moshi.adapter(Garten.class);

        Garten geladenerGarten;
        try {
            geladenerGarten = jsonAdapter.fromJson(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return geladenerGarten;
    }

    /**
     * Speichern des Gartens in die SharedPreferences.
     * @param sharedPreferences SharedPreference Objekt für den Garten.
     * @author Erik Dörenkämper
     */
    public void gartenSpeichern(SharedPreferences sharedPreferences) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Garten> jsonAdapter = moshi.adapter(Garten.class);

        String json = jsonAdapter.toJson(garten);
        sharedPreferences.edit().putString("garten", json).apply();
    }
}
