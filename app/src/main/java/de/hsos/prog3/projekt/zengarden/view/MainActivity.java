package de.hsos.prog3.projekt.zengarden.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

    private GartenViewModel gartenViewModel;
    private AnimationManager animationManager;

    private static final float DIMMED_ALPHA = 0.7f;
    private static final float NORMAL_ALPHA = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root);

        gartenViewModel = new ViewModelProvider(this).get(GartenViewModel.class);
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        animationManager = new AnimationManager(this, gartengrid);

        gartenViewModel.initialisiereViewModel(getPreferences(MODE_PRIVATE));
        initialisiereGrid();
        buttonListenersSetzen();
        observablesSetzen();
    }

    private void initialisiereGrid() {
        GridLayout gartengrid = findViewById(R.id.gartengrid);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 3; j++) {
                TopfMitPflanzeView topfMitPflanze = topfMitPflanzeLayoutErstellen(i, j);
                gartengrid.addView(topfMitPflanze);
            }
        }
    }

    private TopfMitPflanzeView topfMitPflanzeLayoutErstellen(int x, int y) {
        TopfMitPflanzeView topfMitPflanze = new TopfMitPflanzeView(this);
        topfMitPflanze.setTag("x: " + x + " y: " + y);
        topfMitPflanze.setOnClickListener(v -> gartenViewModel.topfWirdAngeklickt(x, y));
        return topfMitPflanze;
    }

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
        TextView geldButton = findViewById(R.id.geld_button);
        geldButton.setText(getString(R.string.dollar, garten.getGeld()));
    }

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

    private void buttonListenersSetzen() {
        setzeWerkzeugButtonListener(R.id.wasser_button, AusgewaehltesWerkzeug.GIESSKANNE);
        setzeWerkzeugButtonListener(R.id.duenger_button, AusgewaehltesWerkzeug.DUENGER);
        setzeWerkzeugButtonListener(R.id.samen_button, AusgewaehltesWerkzeug.SAMEN);
        setzeWerkzeugButtonListener(R.id.verschieben_button, AusgewaehltesWerkzeug.VERSCHIEBEN);
        setzeWerkzeugButtonListener(R.id.verkaufen_button, AusgewaehltesWerkzeug.VERKAUFEN);
    }

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
