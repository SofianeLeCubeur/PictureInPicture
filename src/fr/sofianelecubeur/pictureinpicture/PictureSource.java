package fr.sofianelecubeur.pictureinpicture;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Sofiane on 18/02/2018.
 *
 * @author Sofiane
 */
public interface PictureSource {

    void init(JComponent component);

    /**
     * This method is called when the source is rendered to the PiP (Picture in Picture).
     * @param g {@link Graphics} instance
     * @param currentSize Current PiP size
     */
    void draw(Graphics g, Dimension currentSize);

    /**
     *
     * @return Update rate in millis
     */
    int getUpdateRate();

    /**
     * Tell the PiP (Picture in Picture) if the source's dimension is modifiable.
     * @return if the source is resizable
     */
    boolean isResizable();

    /**
     * Default source size
     * @return Preffered size
     */
    Dimension getPrefferedSize();

    /**
     * Maximum PiP (Picture in Picture) size.
     * If the dimension is null, the maximum size is the screen one.
     * @return Maxmimum size
     */
    Dimension getMaxmimumSize();
}