package de.hsos.prog3.projekt.zengarden.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.hsos.prog3.projekt.zengarden.R;
import de.hsos.prog3.projekt.zengarden.model.AusgewaehltesWerkzeug;
import de.hsos.prog3.projekt.zengarden.model.Pflanze;
import de.hsos.prog3.projekt.zengarden.model.PflanzenEvent;

/**
 * Stellt einen Blumentopf mit einer Pflanze dar.
 *
 * Diese View ist für die visuelle Darstellung eines Topfes im Zen-Garten zuständig.
 * Sie zeigt den Zustand des Topfes an, also ob er leer ist oder eine Pflanze enthält.
 * Ist eine Pflanze vorhanden, werden ihre Art, Wachstumsphase und aktuelle Bedürfnisse
 * (z. B. Wasser oder Dünger) durch entsprechende Bilder visualisiert.
 * @author Jasper Groetzner, Erik
 * @author Jasper Groetzner, Erik Döremkämper
 */
public class TopfMitPflanzeView extends FrameLayout {

    /**
     * ImageView zur Anzeige des Pflanzenbildes.
     */
    private ImageView pflanzeImageView;

    /**
     * ImageView zur Anzeige des aktuellen Bedürfnisses der Pflanze.
     */
    private ImageView beduerfnissImageView;
    /**
     * Konstanten für die Transparenz.
     */

    private static final float DIMMED_ALPHA = 0.7f;
    private static final float NORMAL_ALPHA = 1.0f;

    /**
     * Konstruktor für die programmatische Erstellung der View.
     *
     * @param context      Der Kontext, in dem die View ausgeführt wird.
     * @param attrs        Attribute aus dem XML-Layout.
     * @param defStyleAttr Ein Attribut im aktuellen Theme, das eine Referenz auf einen Stil für diese View enthält.
     */
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

    /**
     * Initialisiert die View.
     * Bläst das Layout auf und holt Referenzen auf die Child-Views.
     *
     * @param context Der Kontext, in dem die View ausgeführt wird.
     * @author Jasper Groetzner
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.topf_mit_pflanze, this, true);
        pflanzeImageView = findViewById(R.id.pflanze);
        beduerfnissImageView = findViewById(R.id.beduerfniss);
    }

    /**
     * Gibt die ImageView der Pflanze zurück.
     *
     * @return Die ImageView-Instanz, die das Bild der Pflanze anzeigt.
     * @author Jasper Groetzner
     */
    public ImageView getPflanzeImageView() {
        return pflanzeImageView;
    }

    /**
     * Stellt die Ansicht für einen Topf dar, einschließlich der Pflanze und ihrer Bedürfnisse.
     * Wenn die übergebene Pflanze null ist, wird der Topf als leer angezeigt.
     * Andernfalls werden die Pflanzenart, die Wachstumsphase und alle aktuellen Bedürfnisse (wie Gießen oder Düngen) dargestellt.
     * Die Methode handhabt auch die Hervorhebung des Topfes basierend auf dem aktuell ausgewählten Werkzeug.
     *
     * @param pflanze               Das darzustellende Pflanzenobjekt. Kann null sein, wenn der Topf leer ist.
     * @param ausgewaehltesWerkzeug Das vom Benutzer aktuell ausgewählte Werkzeug, welches die Hervorhebung der Ansicht beeinflusst.
     * @author jasper groetzner
     */
    public void render(Pflanze pflanze, AusgewaehltesWerkzeug ausgewaehltesWerkzeug) {
        if (pflanze == null) {
            pflanzeImageView.setVisibility(View.GONE);
            beduerfnissImageView.setVisibility(View.GONE);
            pflanzeImageView.clearAnimation();
        } else {
            pflanzeImageView.setVisibility(View.VISIBLE);
            pflanzenartDarstellen(pflanze);
            wachstumsphaseDarstellen(pflanze);
            aktuellesEventDarstellen(pflanze);
        }
        pflanzenHervorhebung(pflanze, ausgewaehltesWerkzeug);
    }

    /**
     * Passt die visuelle Hervorhebung (Alpha-Wert) der View an, basierend auf dem ausgewählten Werkzeug
     * und dem Zustand der Pflanze. Die View wird gedimmt (reduzierte Deckkraft), wenn die aktuelle
     * Interaktion mit dem ausgewählten Werkzeug nicht sinnvoll ist, und normal dargestellt (volle Deckkraft),
     * wenn eine Interaktion möglich oder kein spezifisches Werkzeug ausgewählt ist.
     *
     * Die Logik ist wie folgt:
     * - GIESSKANNE: Hebt hervor, wenn die Pflanze gegossen werden muss.
     * - DUENGER: Hebt hervor, wenn die Pflanze gedüngt werden muss.
     * - SAMEN: Hebt hervor, wenn der Topf leer ist (keine Pflanze).
     * - VERSCHIEBEN oder VERKAUFEN: Hebt hervor, wenn eine Pflanze im Topf ist.
     * - Kein Werkzeug ausgewählt: Alle Töpfe werden normal hervorgehoben.
     *
     * @param pflanze Das Pflanze-Objekt in dieser View, kann null sein, wenn der Topf leer ist.
     * @param ausgewaehltesWerkzeug Das aktuell vom Benutzer ausgewählte Werkzeug.
     * @author jasper groetzner
     */
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

    /**
     * Stellt das aktuelle Bedürfnis der Pflanze (z.B. Gießen oder Düngen) visuell dar.
     * Abhängig vom PflanzenEvent, das von der Pflanze zurückgegeben wird,
     * wird ein entsprechendes Icon (z.B. Wassertropfen oder Düngersack) über der Pflanze angezeigt.
     * Wenn kein aktuelles Ereignis ansteht (d.h. die Pflanze hat keine Bedürfnisse),
     * wird das Bedürfnis-Icon ausgeblendet.
     *
     * @param pflanze Das Pflanzenobjekt, dessen aktuelles Ereignis dargestellt werden soll.
     * @author Jasper Groetzner
     */
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

    /**
     * Stellt die aktuelle Wachstumsphase der Pflanze dar, indem die Größe und das Bild
     * des ImageViews angepasst werden.
     * Je nach Wachstumsphase (KEIMLING, SAEMLING, KLEIN, AUSGEWACHSEN) werden unterschiedliche
     * Skalierungen und Margins gesetzt, um ein visuelles Wachstum zu simulieren.
     *
     * @param pflanze Das Pflanzenobjekt, dessen Wachstumsphase dargestellt werden soll.
     * @author jasper groetzner
     */
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

    /**
     * Stellt das Bild der Pflanze basierend auf ihrer Art ein.
     * Abhängig von der {@link de.hsos.prog3.projekt.zengarden.model.Pflanzenart} des übergebenen Pflanze-Objekts
     * wird das entsprechende Drawable-Ressource für das {@code pflanzeImageView} gesetzt.
     * Dies ändert das visuelle Erscheinungsbild der Pflanze im Topf.
     *
     * @param pflanze Das {@link Pflanze}-Objekt, dessen Art dargestellt werden soll.
     * @author Jasper Groetzner, Erik Dörenkämper
     */
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
