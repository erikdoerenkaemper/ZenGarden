package de.hsos.prog3.projekt.zengarden.view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.BenutzerAktion;

public class AnimationManager {

    private final Context context;
    private final GridLayout gartengrid;

    public AnimationManager(Context context, GridLayout gartengrid) {
        this.context = context;
        this.gartengrid = gartengrid;
    }

    public void spielePflanzenAtmung(ImageView pflanzeImageView, boolean start) {
        if (start) {
            if (pflanzeImageView.getAnimation() == null) {
                Animation atmung = AnimationUtils.loadAnimation(context, R.anim.pflanzenatmung);
                atmung.setStartOffset((long) (Math.random() * 1500));
                pflanzeImageView.startAnimation(atmung);
            }
        } else {
            pflanzeImageView.clearAnimation();
        }
    }

    public void spieleBenutzerAktionAnimation(BenutzerAktion aktion, int x, int y, Integer betrag) {
        FrameLayout topfMitPflanze = gartengrid.findViewWithTag("x: " + x + " y: " + y);
        if (topfMitPflanze == null) return;

        ImageView animationsIcon = topfMitPflanze.findViewById(R.id.animation_icon);
        TextView preisAnzeige = topfMitPflanze.findViewById(R.id.preis_anzeige);
        Animation animation;
        View viewFuerAnimation;

        switch (aktion) {
            case GIESSEN:
                animation = AnimationUtils.loadAnimation(context, R.anim.drehen);
                animationsIcon.setImageResource(R.drawable.giesskanne);
                viewFuerAnimation = animationsIcon;
                break;
            case DUENGEN:
                animation = AnimationUtils.loadAnimation(context, R.anim.schuetteln);
                animationsIcon.setImageResource(R.drawable.duenger);
                viewFuerAnimation = animationsIcon;
                break;
            case PFLANZE_GEKAUFT:
                Animation preisAnimation = AnimationUtils.loadAnimation(context, R.anim.preis_animation);
                preisAnzeige.setText(context.getString(R.string.dollar_minus, betrag != null ? betrag : 0));
                preisAnzeige.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                startAnimation(preisAnzeige, preisAnimation);

                animation = AnimationUtils.loadAnimation(context, R.anim.drehen);
                animationsIcon.setImageResource(R.drawable.samen_pack);
                viewFuerAnimation = animationsIcon;
                break;
            case PFLANZE_VERKAUFT:
                animation = AnimationUtils.loadAnimation(context, R.anim.preis_animation);
                preisAnzeige.setText(context.getString(R.string.dollar, betrag != null ? betrag : 0));
                preisAnzeige.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                viewFuerAnimation = preisAnzeige;
                break;
            case PFLANZE_VERSCHOBEN:
                animation = AnimationUtils.loadAnimation(context, R.anim.rotate_counter_clockwise);
                animationsIcon.setImageResource(R.drawable.verschieben);
                viewFuerAnimation = animationsIcon;
                break;
            case PFLANZE_WIEDEREINGEPFLANZT:
                animation = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise);
                animationsIcon.setImageResource(R.drawable.verschieben);
                viewFuerAnimation = animationsIcon;
                break;
            default:
                return;
        }

        startAnimation(viewFuerAnimation, animation);
    }

    private void startAnimation(View view, Animation animation) {
        view.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new HideViewOnAnimationEndListener(view));
        view.startAnimation(animation);
    }

    private static class HideViewOnAnimationEndListener implements Animation.AnimationListener {
        private final View viewToHide;

        HideViewOnAnimationEndListener(View view) {
            this.viewToHide = view;
        }

        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (viewToHide != null) {
                viewToHide.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    }
}
