package de.hsos.prog3.projekt.zengarden.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;
import de.hsos.prog3.projekt.zengarden.model.PflanzenEvent;

public class TopfMitPflanzeView extends FrameLayout {

    private ImageView pflanzeImageView;
    private ImageView beduerfnissImageView;

    private static final float DIMMED_ALPHA = 0.7f;
    private static final float NORMAL_ALPHA = 1.0f;

    public TopfMitPflanzeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TopfMitPflanzeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopfMitPflanzeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.topf_mit_pflanze, this, true);
        pflanzeImageView = findViewById(R.id.pflanze);
        beduerfnissImageView = findViewById(R.id.beduerfniss);
    }

    public void render(Pflanze pflanze, AusgewaehltesWerkzeug ausgewaehltesWerkzeug) {
        if (pflanze == null) {
            pflanzeImageView.setVisibility(View.GONE);
            beduerfnissImageView.setVisibility(View.GONE);
            pflanzeImageView.clearAnimation();
        } else {
            pflanzeImageView.setVisibility(View.VISIBLE);
            atmungsAnimationDarstellen(pflanze);
            pflanzenartDarstellen(pflanze);
            wachstumsphaseDarstellen(pflanze);
            aktuellesEventDarstellen(pflanze);
        }
        pflanzenHervorhebung(pflanze, ausgewaehltesWerkzeug);
    }

    private void atmungsAnimationDarstellen(Pflanze pflanze) {
        if (pflanze.getAktuellesEvent() == null) {
            if (pflanzeImageView.getAnimation() == null) {
                Animation atmung = AnimationUtils.loadAnimation(getContext(), R.anim.pflanzenatmung);
                atmung.setStartOffset((long) (Math.random() * 1500));
                pflanzeImageView.startAnimation(atmung);
            }
        } else {
            pflanzeImageView.clearAnimation();
        }
    }

    private void pflanzenHervorhebung(Pflanze pflanze, AusgewaehltesWerkzeug ausgewaehltesWerkzeug) {
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
        setAlpha(highlight ? NORMAL_ALPHA : DIMMED_ALPHA);
    }

    private void aktuellesEventDarstellen(Pflanze pflanze) {
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

    private void wachstumsphaseDarstellen(Pflanze pflanze) {
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

    private void pflanzenartDarstellen(Pflanze pflanze) {
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
            case EISGAENSEBLUEMCHEN:
                pflanzeImageView.setImageResource(R.drawable.marigold_eis);
                break;
            case EISSONNENBLUME:
                pflanzeImageView.setImageResource(R.drawable.sonnenblume_eis);
                break;
        }
    }
}
