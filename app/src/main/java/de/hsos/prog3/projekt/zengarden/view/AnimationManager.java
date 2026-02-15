package de.hsos.prog3.projekt.zengarden.view;

import android.content.Context;
import android.media.MediaPlayer;
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

/**
 * Verwaltet die Animationen, die in der Gartenansicht angezeigt werden.
 *
 * Diese Klasse ist verantwortlich für das Starten und Stoppen von Animationen, die sich auf
 * Pflanzeninteraktionen und Benutzeraktionen innerhalb des Gartengitters beziehen. Sie behandelt
 * Animationen für das Gießen, Düngen, Kaufen, Verkaufen und Verschieben von Pflanzen sowie
 * eine kontinuierliche "Atmungs"-Animation für die Pflanzen.
 * @author Jasper Groetzner
 */
public class AnimationManager {

    /**
     * Der Anwendungskontext, der für den Zugriff auf Ressourcen wie Animationen und Strings verwendet wird.
     */
    private final Context context;
    /**
     * Das GridLayout, das den Garten mit allen Töpfen darstellt.
     */
    private final GridLayout gartengrid;

    /**
     * Konstruktor für den AnimationManager.
     * Initialisiert einen neuen Manager zum Abspielen von Animationen innerhalb des Gartens.
     *
     * @param context    Der Anwendungskontext, der zum Laden von Animationen und Ressourcen benötigt wird.
     * @param gartengrid Das GridLayout, das den Garten darstellt und in dem die Animationen abgespielt werden sollen.
     */
    public AnimationManager(Context context, GridLayout gartengrid) {
        this.context = context;
        this.gartengrid = gartengrid;
    }

    /**
     * Startet oder stoppt die "Atmungs"-Animation für eine bestimmte Pflanze.
     * Die Animation lässt die Pflanze leicht pulsieren, um Lebendigkeit zu simulieren.
     * Wenn die Animation gestartet wird, erhält sie einen zufälligen Start-Offset,
     * damit nicht alle Pflanzen im Garten synchron "atmen".
     *
     * @param pflanzeImageView Die ImageView der Pflanze, auf die die Animation angewendet werden soll.
     * @param start            true, um die Animation zu starten; false, um sie zu stoppen und zu entfernen.
     */
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

    /**
     * Spielt eine Animation ab, die eine Benutzeraktion auf einem bestimmten Feld im Gartenraster darstellt.
     * Je nach Aktion wird ein anderes Symbol (z.B. Gießkanne, Dünger) angezeigt und eine entsprechende
     * Animation (z.B. Drehen, Schütteln) abgespielt. Bei Kauf- oder Verkaufsaktionen wird zusätzlich
     * der Preis animiert.
     *
     * @param aktion Die durchzuführende Benutzeraktion (z.B. GIESSEN, DUENGEN).
     * @param x      Die x-Koordinate des Zielfeldes im Raster.
     * @param y      Die y-Koordinate des Zielfeldes im Raster.
     * @param betrag Der Geldbetrag, der bei Kauf- oder Verkaufsaktionen angezeigt werden soll.
     *               Kann für andere Aktionen null sein.
     */
    public void spieleBenutzerAktionAnimation(BenutzerAktion aktion, int x, int y, Integer betrag) {
        FrameLayout topfMitPflanze = gartengrid.findViewWithTag("x: " + x + " y: " + y);
        if (topfMitPflanze == null) return;

        ImageView animationsIcon = topfMitPflanze.findViewById(R.id.animation_icon);
        TextView preisAnzeige = topfMitPflanze.findViewById(R.id.preis_anzeige);
        Animation animation;
        View viewFuerAnimation;
        MediaPlayer mp;

        switch (aktion) {
            case GIESSEN:
                mp = MediaPlayer.create(context, R.raw.wassersound);
                mp.start();
                animation = AnimationUtils.loadAnimation(context, R.anim.drehen);
                animationsIcon.setImageResource(R.drawable.giesskanne);
                viewFuerAnimation = animationsIcon;
                break;
            case DUENGEN:
                mp = MediaPlayer.create(context, R.raw.duengersound);
                mp.start();
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


    /**
     * Startet eine Animation auf einer gegebenen View und macht diese nach Beendigung der Animation wieder unsichtbar.
     * Die Methode macht die View zuerst sichtbar, setzt dann einen AnimationListener, der
     * die View nach Abschluss der Animation wieder ausblendet (setzt die Sichtbarkeit auf GONE).
     * Schließlich wird die Animation auf der View gestartet.
     *
     * @param view      Die View, auf der die Animation abgespielt werden soll.
     * @param animation Die Animation, die gestartet werden soll.
     */
    private void startAnimation(View view, Animation animation) {
        view.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new HideViewOnAnimationEndListener(view));
        view.startAnimation(animation);
    }

    /**
     * Verwaltet und steuert alle visuellen Animationen innerhalb der App.
     * Diese Klasse ist verantwortlich für das Starten und Stoppen von Animationen,
     * die auf Benutzeraktionen oder Spielzustandsänderungen reagieren.
     */
    private static class HideViewOnAnimationEndListener implements Animation.AnimationListener {
        /**
         * Die Ansicht, die nach Beendigung der Animation ausgeblendet werden soll.
         */
        private final View viewToHide;


        /**
         * Konstruktor für den AnimationManager.
         *
         *
         * @param view die ViewtoHide
         */
        HideViewOnAnimationEndListener(View view) {
            this.viewToHide = view;
        }

        @Override
        public void onAnimationStart(Animation animation) { }

        /**
         * Wird aufgerufen, wenn eine Animation endet. Diese Implementierung blendet die zugehörige
         * Ansicht aus, nachdem die Animation abgeschlossen ist.
         *
         * @param animation Die Animation, die ihr Ende erreicht hat.
         */
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
