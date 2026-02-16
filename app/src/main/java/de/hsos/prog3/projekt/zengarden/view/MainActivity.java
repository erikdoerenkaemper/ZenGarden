package de.hsos.prog3.projekt.zengarden.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.BenutzerAktion;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;
import de.hsos.prog3.projekt.zengarden.viewmodel.GartenViewModel;

/**
 * Activity des ZenGarden.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Hält das Viewmodel
     */
    private GartenViewModel gartenViewModel;
    /**
     * hält den Animationmanager
     */
    private AnimationManager animationManager;
    /**
     *Kosntanten für die Transparenz
     */
    private static final float DIMMED_ALPHA = 0.7f;
    private static final float NORMAL_ALPHA = 1.0f;

    /**
     * Initialisiert die Activity, das Layout, ViewModel und die Listener.
     *
     * @author jasper groetzner
     * @param savedInstanceState Gespeicherter Zustand der Instanz.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root);
        initDisplayForFullscreen();

        gartenViewModel = new ViewModelProvider(this).get(GartenViewModel.class);
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        animationManager = new AnimationManager(this, gartengrid);

        gartenViewModel.initialisiereViewModel(getPreferences(MODE_PRIVATE));
        initialisiereGrid();
        buttonListenersSetzen();
        observablesSetzen();
    }

    /**
     * blendet die Systemleisten aus, sodass die App im Vollbild angezeigt wird.
     * Wurde aus Vorlseungsfolien kopiert.
     * @author Erik Dörenkämper
     */
    private void initDisplayForFullscreen(){ // aus Vorlseungsfolien kopiert
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(this.getWindow(),this.getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    /**
     * Initialisiert das Gartengrid, indem es für jede Zelle eine TopfMitPflanzeView
     * erstellt und zum GridLayout hinzufügt. Diese Methode wird einmalig in onCreate
     * aufgerufen, um das Grundgerüst der Benutzeroberfläche für den Garten aufzubauen.
     * @author Erik Dörenkämper
     */
    private void initialisiereGrid() {
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                TopfMitPflanzeView topfMitPflanze = topfMitPflanzeLayoutErstellen(i, j);
                gartengrid.addView(topfMitPflanze);
            }
        }
    }

    /**
     * Erstellt eine neue TopfMitPflanzeView für eine bestimmte Position im Garten-Grid.
     * Setzt ein Tag zur Identifizierung der Position und einen OnClickListener,
     * der das @link GartenViewModel über einen Klick an dieser Position informiert.
     *
     * @param x Die x-Koordinate (Spalte) der View im Grid.
     * @param y Die y-Koordinate (Zeile) der View im Grid.
     * @return Eine initialisierte @link TopfMitPflanzeView Instanz.
     * @author Erik Dörenkämper
     */
    private TopfMitPflanzeView topfMitPflanzeLayoutErstellen(int x, int y) {
        TopfMitPflanzeView topfMitPflanze = new TopfMitPflanzeView(this);
        topfMitPflanze.setTag("x: " + x + " y: " + y);
        topfMitPflanze.setOnClickListener(v -> gartenViewModel.topfWirdAngeklickt(x, y));
        return topfMitPflanze;
    }

    /**
     * Stellt den gesamten Garten auf der Benutzeroberfläche dar.
     * Iteriert durch das Gartengitter und aktualisiert jede TopfMitPflanzeView
     * basierend auf dem Zustand der entsprechenden @link Pflanze im @link Garten-Modell.
     * Startet oder stoppt die Atemanimation der Pflanzen, je nachdem, ob ein Event anliegt.
     * Aktualisiert außerdem die Anzeige des Geldbetrags.
     *
     * @param garten Das Objekt, das die darzustellenden Daten enthält.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private void gartenDarstellen(Garten garten) {
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        AusgewaehltesWerkzeug ausgewaehltesWerkzeug = gartenViewModel.getWerkzeug().getValue();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                TopfMitPflanzeView topfMitPflanze = gartengrid.findViewWithTag("x: " + i + " y: " + j);
                if (topfMitPflanze != null) {
                    Pflanze pflanze = garten.getPflanze(i, j);
                    topfMitPflanze.render(pflanze, ausgewaehltesWerkzeug);
                    if (pflanze != null) {
                        boolean shouldAnimate = pflanze.getAktuellesEvent() == null;
                        animationManager.spielePflanzenAtmung(topfMitPflanze.getPflanzeImageView(), shouldAnimate);
                    }
                }
            }
        }
        TextView geldButton = findViewById(R.id.geld_anzeige);
        geldButton.setText(getString(R.string.dollar, garten.getGeld()));
    }

    /**
     * Stellt die Werkzeug-Buttons in der Benutzeroberfläche dar.
     * Der aktuell ausgewählte Button wird normal (undurchsichtig) dargestellt, während die anderen
     * Buttons abgedunkelt (leicht transparent) angezeigt werden, um den Fokus des Benutzers zu lenken.
     * Wenn kein Werkzeug ausgewählt ist, werden alle Buttons normal dargestellt.
     *
     * @param ausgewaehltesWerkzeug Das Werkzeug, das aktuell ausgewählt ist, oder null,
     *                              wenn kein Werkzeug ausgewählt ist.
     * @author Jasper Groetzner
     */
    private void werkzeugButtonsDarstellen(AusgewaehltesWerkzeug ausgewaehltesWerkzeug) {
        Button wasserButton = findViewById(R.id.wasser_button);
        Button duengerButton = findViewById(R.id.duenger_button);
        Button samenButton = findViewById(R.id.samen_button);
        Button verschiebenButton = findViewById(R.id.verschieben_button);
        Button verkaufenButton = findViewById(R.id.verkaufen_button);

        if (ausgewaehltesWerkzeug != null) {
            wasserButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.GIESSKANNE ? NORMAL_ALPHA : DIMMED_ALPHA);
            duengerButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.DUENGER ? NORMAL_ALPHA : DIMMED_ALPHA);
            samenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.SAMEN ? NORMAL_ALPHA : DIMMED_ALPHA);
            verschiebenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERSCHIEBEN ? NORMAL_ALPHA : DIMMED_ALPHA);
            verkaufenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN ? NORMAL_ALPHA : DIMMED_ALPHA);
        } else {
            wasserButton.setAlpha(NORMAL_ALPHA);
            duengerButton.setAlpha(NORMAL_ALPHA);
            samenButton.setAlpha(NORMAL_ALPHA);
            verschiebenButton.setAlpha(NORMAL_ALPHA);
            verkaufenButton.setAlpha(NORMAL_ALPHA);
        }
    }

    /**
     * Initialisiert die Click-Listener für die Werkzeug-Buttons.
     * Bei einem Klick wird das entsprechende Werkzeug im ViewModel gesetzt oder zurückgesetzt.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private void buttonListenersSetzen() {
        setzeWerkzeugButtonListener(R.id.wasser_button, AusgewaehltesWerkzeug.GIESSKANNE);
        setzeWerkzeugButtonListener(R.id.duenger_button, AusgewaehltesWerkzeug.DUENGER);
        setzeWerkzeugButtonListener(R.id.samen_button, AusgewaehltesWerkzeug.SAMEN);
        setzeWerkzeugButtonListener(R.id.verschieben_button, AusgewaehltesWerkzeug.VERSCHIEBEN);
        setzeWerkzeugButtonListener(R.id.verkaufen_button, AusgewaehltesWerkzeug.VERKAUFEN);
    }

    /**
     * Setzt einen OnClickListener für einen Werkzeug-Button.
     * Beim Klick wird das entsprechende Werkzeug im ViewModel gesetzt oder zurückgesetzt,
     * falls es bereits ausgewählt war.
     *
     * @param buttonId Die ID des Buttons, für den der Listener gesetzt werden soll.
     * @param werkzeug Das Werkzeug, das mit diesem Button assoziiert ist.
     * @author Jasper Groetzner
     */
    private void setzeWerkzeugButtonListener(int buttonId, AusgewaehltesWerkzeug werkzeug) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == werkzeug) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(werkzeug);
            }
        });
    }

    /**
     * Initialisiert die Observer für die LiveData-Objekte im ViewModel.
     * Diese Methode ist zentral für die reaktive Aktualisierung der Benutzeroberfläche.*
     * 1. Werkzeug-Observer: Beobachtet Änderungen am ausgewählten Werkzeug.
     *    Bei einer Änderung werden die Werkzeug-Buttons (visuelle Hervorhebung) und der Garten
     *    (z.B. zur Darstellung von Interaktionsmöglichkeiten) neu gezeichnet.
     * 2. Garten-Observer: Beobachtet Änderungen am Zustand des Gartens.
     *    Bei jeder Änderung (z.B. Pflanzenwachstum, Gießen) wird die gesamte Gartenansicht
     *    aktualisiert, um den neuen Zustand darzustellen.
     * 3. BenutzerAktion-Observer: Beobachtet durchgeführte Benutzeraktionen (z.B. Gießen, Pflanzen).
     *    Bei einer Aktion wird eine entsprechende Animation über den AnimationManager
     *    an der jeweiligen Position im Garten ausgelöst.
     * @author Jasper Groetzner, Erik Dörenkämper
     */
    private void observablesSetzen() {
        gartenViewModel.getWerkzeug().observe(this, ausgewaehltesWerkzeug -> {
            werkzeugButtonsDarstellen(ausgewaehltesWerkzeug);
            Garten garten = gartenViewModel.getGartenLiveData().getValue();
            if (garten != null) {
                gartenDarstellen(garten);
            }
        });
        gartenViewModel.getGartenLiveData().observe(this, this::gartenDarstellen);
        gartenViewModel.getBenutzerAktionLiveData().observe(this, aktionData -> {
            if (aktionData != null) {
                BenutzerAktion aktion = (BenutzerAktion) aktionData[0];
                int x = (int) aktionData[1];
                int y = (int) aktionData[2];
                Integer betrag = (aktionData.length > 3) ? (Integer) aktionData[3] : null;
                animationManager.spieleBenutzerAktionAnimation(aktion, x, y, betrag);
            }
        });
    }
}
