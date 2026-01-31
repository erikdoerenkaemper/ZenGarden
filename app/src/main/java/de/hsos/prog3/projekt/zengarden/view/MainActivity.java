package de.hsos.prog3.projekt.zengarden.view;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root);

        gartenViewModel = new ViewModelProvider(this).get(GartenViewModel.class);

        gartenViewModel.initialisiereViewModel(getSharedPreferences("garten", MODE_PRIVATE));
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
        topfMitPflanze.setOnClickListener(v -> {
            gartenViewModel.topfWirdAngeklickt(x,y);
        });

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
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                FrameLayout topfMitPflanze = gartengrid.findViewWithTag("x: " + i + " y: " + j);
                topfMitPflanzeDartstellen(topfMitPflanze, garten.getPflanze(i,j));
            }
        }

        // Geld anzeige
        TextView geldButton = findViewById(R.id.geld_button);
        geldButton.setText(garten.getGeld() + "$");
    }


    /**
     * Passt die Views in topfMitPflanze anhand des Models an.
     * @param topfMitPflanze FrameLayout, das die Views des Topfes, der Pflanze und der Bedüfnisse enthält.
     * @param pflanze Model der Pflanze.
     * @author Erik Dörenkämper
     */
    private void topfMitPflanzeDartstellen(FrameLayout topfMitPflanze, Pflanze pflanze){
        ImageView pflanzeImageView = topfMitPflanze.findViewById(R.id.pflanze);

        // Topf leer oder nicht leer
        if (pflanze == null){
            pflanzeImageView.setVisibility(View.GONE);
            return;
        } else {
            pflanzeImageView.setVisibility(View.VISIBLE);
        }


        // Pflanzenart
        switch (pflanze.getPflanzenart()){
            case GAENSEBLUEMCHEN:
                pflanzeImageView.setImageResource(R.drawable.marigold);
                break;

            case SONNENBLUME:
                // pflanzeImageView.setBackgroundResource(R.drawable.tulip);
                break;

            case ROSE:
                // pflanzeImageView.setBackgroundResource(R.drawable.rose);
                break;
        }



        // Wachstumsphase
        switch (pflanze.getWachstumsphase()) {
            case KEIMLING:
                pflanzeImageView.setScaleX(0.25f);
                pflanzeImageView.setScaleY(0.25f);
                //pflanzeImageView.setImageResource(R.drawable.keimling);
                break;
            case SAEMLING:
                pflanzeImageView.setScaleX(0.4f);
                pflanzeImageView.setScaleY(0.4f);
                break;
            case KLEIN:
                pflanzeImageView.setScaleX(0.75f);
                pflanzeImageView.setScaleY(0.75f);
                break;
            case AUSGEWACHSEN:
                pflanzeImageView.setScaleX(1.0f);
                pflanzeImageView.setScaleY(1.0f);

        }


        // Aktuelles Event
        ImageView beduerfnissImageView = topfMitPflanze.findViewById(R.id.beduerfniss);
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
                    break;

                default:
                    beduerfnissImageView.setVisibility(View.GONE);
                    break;
            }
        }
    }


    /**
     * Stellt die Werkzeug Buttons dar.
     * Der Button mit dem aktuell ausgewählten Werkzeug wird hervorgehoben.
     * @param ausgewaehltesWerkzeug Werkzeug das aktuell ausgewählt wurde.
     * @author -
     */
    private void werkzeugButtonsDarstellen(AusgewaehltesWerkzeug ausgewaehltesWerkzeug){
        //TODO
    }








    // Button Listeners setzen

    /**
     * Setzt die OnClickListener für die Werkzeugbuttons.
     * Listeners für die topfMitPflanzen Views werden bereits bei der Erstellung der Pflanzen hinzugefügt
     * @author Erik Dörenkämper
     */
    private void buttonListenersSetzen() {

        // Listeners für die Werkzeugbuttons
        Button wasserButton = findViewById(R.id.wasser_button);
        wasserButton.setOnClickListener(v -> {
            gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.GIESSKANNE);
        });

        Button duengerButton = findViewById(R.id.duenger_button);
        duengerButton.setOnClickListener(v -> {
            gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.DUENGER);
        });

        Button samenButton = findViewById(R.id.samen_button);
        samenButton.setOnClickListener(v -> {
            gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.SAMEN);
        });

        Button verschiebenButton = findViewById(R.id.verschieben_button);
        verschiebenButton.setOnClickListener(v -> {
            gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERSCHIEBEN);
        });

        Button verkaufenButton = findViewById(R.id.verkaufen_button);
        verkaufenButton.setOnClickListener(v -> {
            gartenViewModel.setWerkzeug(AusgewaehltesWerkzeug.VERKAUFEN);
        });

        // Listeners für die topfMitPflanzen Views werden bereits bei der Erstellung der Pflanzen hinzugefügt
    }







    // Observables setzen

    /**
     * Setzt die Observables für das ViewModel.
     * @author Erik Dörenkämper
     */
    private void observablesSetzen(){
        // Werkzeug Anzeige
        gartenViewModel.getWerkzeug().observe(this, this::werkzeugButtonsDarstellen);

        // Garten
        gartenViewModel.getGartenLiveData().observe(this, this::gartenDarstellen);
    }
}