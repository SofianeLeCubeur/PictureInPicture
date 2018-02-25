package fr.sofianelecubeur.pictureinpicture.sources;

import fr.sofianelecubeur.pictureinpicture.PictureSource;

import java.awt.*;

/**
 * Created by Sofiane on 19/02/2018.
 *
 * @author Sofiane
 */
public class ImageSource implements PictureSource {

    private Dimension prefferedSize;
    private Image image;

    public ImageSource(Dimension prefferedSize, Image image) {
        this.prefferedSize = (prefferedSize == null ? new Dimension(image.getWidth(null), image.getHeight(null)) : prefferedSize);
        this.image = image;
    }

    @Override
    public void draw(Graphics g, Dimension currentSize) {
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public int getUpdateRate() {
        return 0;
    }

    @Override
    public Dimension getPrefferedSize() {
        return prefferedSize;
    }

    @Override
    public Dimension getMaxmimumSize() {
        return (image == null ? null : new Dimension(image.getWidth(null), image.getHeight(null)));
    }

    public void setPrefferedSize(Dimension prefferedSize) {
        this.prefferedSize = (prefferedSize == null ? new Dimension(image.getWidth(null), image.getHeight(null)) : prefferedSize);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}