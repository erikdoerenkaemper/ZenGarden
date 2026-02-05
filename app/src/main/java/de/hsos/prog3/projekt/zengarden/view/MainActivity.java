package de.hsos.prog3.projekt.zengarden.view;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
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

    private static final float DIMMED_ALPHA = 0.8f;
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
     * Passt die Views in topfMitPflanze anhand des Models an.
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

        } else {
            pflanzeImageView.setVisibility(View.VISIBLE);
            beduerfnissImageView.setVisibility(View.VISIBLE);
        }


        // Pflanzenart
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



        // Wachstumsphase
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


        // Aktuelles Event
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

        // Hervorhebung basierend auf dem ausgewählten Werkzeug
        boolean highlight = false;

        if (ausgewaehltesWerkzeug == null || ausgewaehltesWerkzeug == AusgewaehltesWerkzeug.NICHTS) {
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
        if (ausgewaehltesWerkzeug != AusgewaehltesWerkzeug.NICHTS) {
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
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.NICHTS);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.GIESSKANNE);
            }
        });

        Button duengerButton = findViewById(R.id.duenger_button);
        duengerButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.DUENGER) {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.NICHTS);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.DUENGER);
            }
        });

        Button samenButton = findViewById(R.id.samen_button);
        samenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.SAMEN) {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.NICHTS);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.SAMEN);
            }
        });

        Button verschiebenButton = findViewById(R.id.verschieben_button);
        verschiebenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.VERSCHIEBEN) {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.NICHTS);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERSCHIEBEN);
            }
        });

        Button verkaufenButton = findViewById(R.id.verkaufen_button);
        verkaufenButton.setOnClickListener(v -> {
            if (gartenViewModel.getWerkzeug().getValue() == AusgewaehltesWerkzeug.VERKAUFEN) {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.NICHTS);
            } else {
                gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERKAUFEN);
            }
        });
    }


    // Observables setzen

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
    }
}
