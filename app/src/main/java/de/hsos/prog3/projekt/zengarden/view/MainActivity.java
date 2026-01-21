package de.hsos.prog3.projekt.zengarden.view;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.viewmodel.GartenViewModel;

public class MainActivity extends AppCompatActivity {

    private GartenViewModel gartenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.root);

        gartenViewModel = new ViewModelProvider(this).get(GartenViewModel.class);

        initialisiereViews();
        buttonListenersSetzen();
        observablesSetzen();
    }

    private void initialisiereViews(){

        GridLayout gartengrid = findViewById(R.id.gartengrid);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                FrameLayout topfMitPflanze = new FrameLayout(this);
                LayoutInflater.from(this).inflate(R.layout.topf_mit_pflanze, topfMitPflanze, true);
                int x = i;
                int y = j;

                // Listener setzen
                topfMitPflanze.setOnClickListener(v -> {
                    gartenViewModel.topfWirdAngeklickt(x,y);
                });

                gartengrid.addView(topfMitPflanze);
            }
        }
    }




    // Button Listeners setzen
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

        // Listeners für die Töpfe_mit_Pflanzen werden bereits bei der Erstellung der Pflanzen hinzugefügt
    }







    // Observables setzen
    private void observablesSetzen(){
        // Geld Anzeige
        gartenViewModel.getGeld().observe(this, geld -> {
            TextView geldButton = findViewById(R.id.geld_button);
            geldButton.setText(geld + "$");
        });

        // Werkzeug Anzeige
        gartenViewModel.getWerkzeug().observe(this, ausgewaehltesWerkzeug -> {
            // TODO Werkzeug Anzeige anpassen
        });


        // Pflanzen
    }
}