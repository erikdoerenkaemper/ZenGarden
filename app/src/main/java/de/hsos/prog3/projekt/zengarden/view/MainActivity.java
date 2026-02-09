package de.hsos.prog3.projekt.zengarden.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.BenutzerAktion;
import de.hsos.prog3.projekt.zengarden.model.Garten;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;
import de.hsos.prog3.projekt.zengarden.model.PflanzenEvent;
import de.hsos.prog3.projekt.zengarden.viewmodel.GartenViewModel;

/**
 * Activity des ZenGarden.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * ViewModel des Gartens.
     */
    private GartenViewModel gartenViewModel;

    private static final float DIMMED_ALPHA = 0.7f;
    private static final float NORMAL_ALPHA = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root);

        gartenViewModel = new ViewModelProvider(this).get(GartenViewModel.class);

        gartenViewModel.initialisiereViewModel(getPreferences(MODE_PRIVATE));
        initialisiereGrid();
        buttonListenersSetzen();
        observablesSetzen();
    }

    /**
     * Befüllt das Grid mit topfMitPflanze Layouts.
     * @author Erik Dörenkämper
     */
    private void initialisiereGrid(){
        GridLayout gartengrid = findViewById(R.id.gartengrid);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                FrameLayout topfMitPflanze = topfMitPflanzeLayoutErstellen(i,j);
                gartengrid.addView(topfMitPflanze);
            }
        }
    }

    /**
     * Erstellt ein topfMitPflanze FrameLayout und setzt einen OnClickListener.
     * @param x Spalte in der sich die Pflanze im Garten befindet.
     * @param y Zeile in der sich die Pflanze im Garten befindet.
     * @return Fertiges topfMitPflanze FrameLayout.
     * @author Erik Dörenkämper
     */
    private FrameLayout topfMitPflanzeLayoutErstellen(int x, int y){
        FrameLayout topfMitPflanze = new FrameLayout(this);
        LayoutInflater.from(this).inflate(R.layout.topf_mit_pflanze, topfMitPflanze, true);
        topfMitPflanze.setTag("x: " + x + " y: " + y);

        // Listener setzen
        topfMitPflanze.setOnClickListener(v -> gartenViewModel.topfWirdAngeklickt(x,y));

        return topfMitPflanze;
    }



    // UI Elemente anpassen
    /**
     * Durchläuft alle Views und passt diese anhand des Models an.
     * @param garten Model des Gartens
     * @author Erik Dörenkämper
     */
    private void gartenDarstellen(Garten garten){
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        AusgewaehltesWerkzeug ausgewaehltesWerkzeug = gartenViewModel.getWerkzeug().getValue();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                FrameLayout topfMitPflanze = gartengrid.findViewWithTag("x: " + i + " y: " + j);
                topfMitPflanzeDartstellen(topfMitPflanze, garten.getPflanze(i,j), ausgewaehltesWerkzeug);
            }
        }

        // Geld anzeige
        TextView geldButton = findViewById(R.id.geld_button);
        geldButton.setText(getString(R.string.dollar, garten.getGeld()));
    }


    /**
     * Passt die Views in topfMitPflanze anhand des Models an. Startet oder stoppt Animationen
     * basierend auf dem Pflanzenzustand.
     * @param topfMitPflanze FrameLayout, das die Views des Topfes, der Pflanze und der Bedüfnisse enthält.
     * @param pflanze Model der Pflanze.
     * @param ausgewaehltesWerkzeug Aktuell ausgewähltes Werkzeug zur Hervorhebung.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private void topfMitPflanzeDartstellen(FrameLayout topfMitPflanze, Pflanze pflanze, AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        ImageView pflanzeImageView = topfMitPflanze.findViewById(R.id.pflanze);
        ImageView beduerfnissImageView = topfMitPflanze.findViewById(R.id.beduerfniss);

        // Topf leer oder nicht leer
        if (pflanze == null){
            pflanzeImageView.setVisibility(View.GONE);
            beduerfnissImageView.setVisibility(View.GONE);
            pflanzeImageView.clearAnimation();

        } else {
            pflanzeImageView.setVisibility(View.VISIBLE);

            // Atmungs-Animation starten/stoppen
            atmungsAnimationDarstellen(pflanze, pflanzeImageView);

            // UI-Elemente für die Pflanze aktualisieren
            pflanzenartDarstellen(pflanze, pflanzeImageView);
            wachstumsphaseDarstellen(pflanze, pflanzeImageView);
            aktuellesEventDarstellen(pflanze, beduerfnissImageView);
        }

        // Hervorhebung basierend auf dem ausgewählten Werkzeug
        pflanzenHervorhebung(topfMitPflanze, pflanze, ausgewaehltesWerkzeug);
    }

    private void atmungsAnimationDarstellen(Pflanze pflanze, ImageView pflanzeImageView) {
        if (pflanze.getAktuellesEvent() == null) {
            // Nur starten, wenn keine Animation läuft
            if (pflanzeImageView.getAnimation() == null) {
                Animation atmung = AnimationUtils.loadAnimation(this, R.anim.pflanzenatmung);
                // Zufälliger Start-Offset, um die Animationen asynchron zu machen
                atmung.setStartOffset((long) (Math.random() * 1500));
                pflanzeImageView.startAnimation(atmung);
            }
        } else {
            pflanzeImageView.clearAnimation();
        }
    }

    /**
     * Hebt die Pflanze an der gegebenen Position hervor.
     * Hebt die Hervorhebung auf, wenn die Position -1 ist.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private static void pflanzenHervorhebung(FrameLayout topfMitPflanze, Pflanze pflanze, AusgewaehltesWerkzeug ausgewaehltesWerkzeug) {
        boolean highlight;

        if (ausgewaehltesWerkzeug == null) {
            highlight = true;
        } else {
            switch (ausgewaehltesWerkzeug) {
                case GIESSKANNE:
                    highlight = pflanze != null && pflanze.getAktuellesEvent() == PflanzenEvent.GIESSEN;
                    break;
                case DUENGER:
                    highlight = pflanze != null && pflanze.getAktuellesEvent() == PflanzenEvent.DUENGEN;
                    break;
                case SAMEN:
                    highlight = pflanze == null;
                    break;
                case VERSCHIEBEN:
                    highlight = pflanze != null;
                    break;
                case VERKAUFEN:
                    highlight = pflanze != null;
                    break;
                default:
                    highlight = true;
            }
        }
        topfMitPflanze.setAlpha(highlight ? NORMAL_ALPHA : DIMMED_ALPHA);
    }

    /**
     * Stellt das aktuelle Event (Bedürfnis) einer Pflanze dar, indem das entsprechende
     * Icon in einem ImageView angezeigt wird. Wenn kein Event vorhanden ist,
     * wird das ImageView ausgeblendet.
     *
     * @param pflanze Die Pflanze, deren aktuelles Event dargestellt werden soll.
     * @param beduerfnissImageView Das ImageView, in dem das Event-Icon angezeigt wird.
     * @author Jasper Groetzner, Erik Dörenkämper
     */
    private static void aktuellesEventDarstellen(Pflanze pflanze, ImageView beduerfnissImageView) {
        if (pflanze != null) {
            PflanzenEvent aktuellesEvent = pflanze.getAktuellesEvent();
            if (aktuellesEvent == null) {
                beduerfnissImageView.setVisibility(View.GONE);
            } else {
                switch (aktuellesEvent) {
                    case GIESSEN:
                        beduerfnissImageView.setImageResource(R.drawable.wassertropfen);
                        beduerfnissImageView.setVisibility(View.VISIBLE);
                        break;

                    case DUENGEN:
                        beduerfnissImageView.setImageResource(R.drawable.duenger);
                        beduerfnissImageView.setVisibility(View.VISIBLE);
                        break;

                    default:
                        beduerfnissImageView.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    /**
     * Passt die visuelle Darstellung einer Pflanze entsprechend ihrer Wachstumsphase an.
     * Dies umfasst die Anpassung der Größe (Skalierung) und des vertikalen Abstands (Margin)
     * des ImageViews, um das Wachstum zu simulieren. Für die Phase "Keimling" wird zudem
     * ein spezifisches Bild gesetzt.
     *
     * @param pflanzeImageView Das ImageView, das die Pflanze darstellt.
     * @param pflanze Das Pflanzen-Model, das die aktuelle Wachstumsphase enthält.
     * @author Jasper Groetzner, Erik Dörenkämper
     */
    private static void wachstumsphaseDarstellen(Pflanze pflanze, ImageView pflanzeImageView) {
        if (pflanze != null) {
            switch (pflanze.getWachstumsphase()) {
                case KEIMLING:
                    pflanzeImageView.setScaleX(0.25f);
                    pflanzeImageView.setScaleY(0.25f);
                    MarginLayoutParams keimlingMargin = (MarginLayoutParams) pflanzeImageView.getLayoutParams();
                    keimlingMargin.setMargins(0, 0, 0, 40);
                    pflanzeImageView.setLayoutParams(keimlingMargin);
                    pflanzeImageView.setImageResource(R.drawable.saemling);
                    break;
                case SAEMLING:
                    pflanzeImageView.setScaleX(0.3f);
                    pflanzeImageView.setScaleY(0.3f);
                    MarginLayoutParams saemlingMargin = (MarginLayoutParams) pflanzeImageView.getLayoutParams();
                    saemlingMargin.setMargins(0, 0, 0, 60);
                    pflanzeImageView.setLayoutParams(saemlingMargin);
                    break;
                case KLEIN:
                    pflanzeImageView.setScaleX(0.6f);
                    pflanzeImageView.setScaleY(0.6f);
                    MarginLayoutParams kleinMargin = (MarginLayoutParams) pflanzeImageView.getLayoutParams();
                    kleinMargin.setMargins(0, 0, 0, 90);
                    pflanzeImageView.setLayoutParams(kleinMargin);
                    break;
                case AUSGEWACHSEN:
                    pflanzeImageView.setScaleX(1.0f);
                    pflanzeImageView.setScaleY(1.0f);
                    MarginLayoutParams ausgewachsenMargin = (MarginLayoutParams) pflanzeImageView.getLayoutParams();
                    ausgewachsenMargin.setMargins(0, 0, 0, 105);
                    pflanzeImageView.setLayoutParams(ausgewachsenMargin);
            }
        }
    }

    /**
     * Stellt die korrekte Grafik für die Pflanzenart dar, wenn die Pflanze ausgewachsen ist.
     * @param pflanzeImageView Das ImageView-Element, das die Pflanzengrafik anzeigt.
     * @param pflanze Das Pflanzenobjekt, dessen Art dargestellt werden soll.
     * @author Jasper Groetzner, Erik Dörenkämper
     */
    private static void pflanzenartDarstellen(Pflanze pflanze, ImageView pflanzeImageView) {
        if (pflanze != null) {
            switch (pflanze.getPflanzenart()){
                case GAENSEBLUEMCHEN:
                    pflanzeImageView.setImageResource(R.drawable.marigold);
                    break;

                case SONNENBLUME:
                    pflanzeImageView.setImageResource(R.drawable.sonnenblume);
                    break;

                case ROSE:
                    pflanzeImageView.setImageResource(R.drawable.rose);
                    break;
            }
        }
    }


    /**
     * Stellt die Werkzeug Buttons dar.
     * Der Button mit dem aktuell ausgewählten Werkzeug wird hervorgehoben.
     * @param ausgewaehltesWerkzeug Werkzeug das aktuell ausgewählt wurde.
     * @author Jasper Groetzner
     */
    private void werkzeugButtonsDarstellen(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        Button wasserButton = findViewById(R.id.wasser_button);
        Button duengerButton = findViewById(R.id.duenger_button);
        Button samenButton = findViewById(R.id.samen_button);
        Button verschiebenButton = findViewById(R.id.verschieben_button);
        Button verkaufenButton = findViewById(R.id.verkaufen_button);

        // Wenn ein Werkzeug ausgewählt ist, werden alle anderen abgedunkelt.
        if (ausgewaehltesWerkzeug != null) {
            wasserButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.GIESSKANNE ? NORMAL_ALPHA : DIMMED_ALPHA);
            duengerButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.DUENGER ? NORMAL_ALPHA : DIMMED_ALPHA);
            samenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.SAMEN ? NORMAL_ALPHA : DIMMED_ALPHA);
            verschiebenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERSCHIEBEN ? NORMAL_ALPHA : DIMMED_ALPHA);
            verkaufenButton.setAlpha(ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.VERKAUFEN ? NORMAL_ALPHA : DIMMED_ALPHA);
        } else {
            // Wenn kein Werkzeug ausgewählt ist, sind alle normal sichtbar.
            wasserButton.setAlpha(NORMAL_ALPHA);
            duengerButton.setAlpha(NORMAL_ALPHA);
            samenButton.setAlpha(NORMAL_ALPHA);
            verschiebenButton.setAlpha(NORMAL_ALPHA);
            verkaufenButton.setAlpha(NORMAL_ALPHA);
        }
    }

    /**
     * Setzt die OnClickListener für die Werkzeugbuttons.
     * Listeners für die topfMitPflanzen Views werden bereits bei der Erstellung der Pflanzen hinzugefügt
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private void buttonListenersSetzen() {

        Button wasserButton = findViewById(R.id.wasser_button);
        wasserButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.GIESSKANNE) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.GIESSKANNE);
            }
        });

        Button duengerButton = findViewById(R.id.duenger_button);
        duengerButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.DUENGER) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.DUENGER);
            }
        });

        Button samenButton = findViewById(R.id.samen_button);
        samenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.SAMEN) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.SAMEN);
            }
        });

        Button verschiebenButton = findViewById(R.id.verschieben_button);
        verschiebenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.VERSCHIEBEN) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERSCHIEBEN);
            }
        });

        Button verkaufenButton = findViewById(R.id.verkaufen_button);
        verkaufenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.VERKAUFEN) {
                gartenViewModel.setWerkzeug(null);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERKAUFEN);
            }
        });
    }

    /**
     * Setzt die Observables für das ViewModel.
     * @author Erik Dörenkämper, Jasper Groetzner
     */
    private void observablesSetzen(){
        // Werkzeug Anzeige und Neubau des Gartens bei Werkzeugwechsel
        gartenViewModel.getWerkzeug().observe(this, ausgewaehltesWerkzeug -> {
            werkzeugButtonsDarstellen(ausgewaehltesWerkzeug);
            Garten garten = gartenViewModel.getGartenLiveData().getValue();
            if (garten != null) {
                gartenDarstellen(garten);
            }
        });

        // Garten
        gartenViewModel.getGartenLiveData().observe(this, this::gartenDarstellen);

        // BenutzerAktion-Animation
        gartenViewModel.getBenutzerAktion().observe(this, aktionData -> {
            if (aktionData != null) {
                BenutzerAktion aktion = (BenutzerAktion) aktionData[0];
                int x = (int) aktionData[1];
                int y = (int) aktionData[2];
                spieleBenutzerAktionAnimation(aktion, x, y);
            }
        });
    }

    /**
     * Spielt eine Animation auf einer temporären ImageView über einer Pflanze ab,
     * um eine Benutzerinteraktion zu visualisieren.
     * Die Art der Animation und das angezeigte Icon hängen von der ausgelösten
     * {@link BenutzerAktion} ab. Nach der Animation wird die ImageView wieder ausgeblendet.
     *
     * @param aktion Die BenutzerAktion (z.B. GIESSEN, PFLANZE_GEKAUFT), die die Animation auslöst.
     * @param x Die x-Koordinate der Pflanze im Gartengrid.
     * @param y Die y-Koordinate der Pflanze im Gartengrid.
     * @author Jasper Groetzner
     */
    private void spieleBenutzerAktionAnimation(BenutzerAktion aktion, int x, int y) {
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        FrameLayout topfMitPflanze = gartengrid.findViewWithTag("x: " + x + " y: " + y);
        if (topfMitPflanze == null) return;

        ImageView animationsIcon = topfMitPflanze.findViewById(R.id.animation_icon);
        Animation animation;
        int werkzeugIconId;

        switch (aktion) {
            case GIESSEN:
                animation = AnimationUtils.loadAnimation(this, R.anim.drehen);
                werkzeugIconId = R.drawable.giesskanne;
                break;
            case DUENGEN:
                animation = AnimationUtils.loadAnimation(this, R.anim.schuetteln);
                werkzeugIconId = R.drawable.duenger;
                break;
            case PFLANZE_GEKAUFT:
                animation = AnimationUtils.loadAnimation(this, R.anim.schuetteln2);
                werkzeugIconId = R.drawable.samen_pack; //platzhalter
                break;
            case PFLANZE_VERKAUFT:
                animation = AnimationUtils.loadAnimation(this, R.anim.schuetteln);
               werkzeugIconId = R.drawable.ic_launcher_foreground; //platzhalter
                break;
            case PFLANZE_VERSCHOBEN:
                animation = AnimationUtils.loadAnimation(this, R.anim.schuetteln);
              werkzeugIconId = R.drawable.ic_launcher_foreground; //platzhalter
                break;
            default:

                return; // Keine Animation für unbekannte Aktionen
        }

        animationsIcon.setImageResource(werkzeugIconId);
        animationsIcon.setVisibility(View.VISIBLE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animationsIcon.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animationsIcon.startAnimation(animation);
    }
}
