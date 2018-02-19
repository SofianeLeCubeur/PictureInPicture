package fr.sofianelecubeur.pictureinpicture;

import java.awt.*;

/**
 * Created by Sofiane on 18/02/2018.
 *
 * @author Sofiane
 */
public interface PictureSource {

    void draw(Graphics g);
    Dimension getPrefferedSize();
}