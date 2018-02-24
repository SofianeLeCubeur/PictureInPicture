package fr.sofianelecubeur.pictureinpicture.sources;

import fr.sofianelecubeur.pictureinpicture.ImageHelper;
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
        this.prefferedSize = prefferedSize;
        this.image = image;
    }

    @Override
    public void draw(Graphics g, Dimension currentSize) {
        Dimension scale = ImageHelper.getScaledDimension(new Dimension(image.getWidth(null), image.getHeight(null)), currentSize);
        g.drawImage(image.getScaledInstance(scale.width, scale.height, Image.SCALE_DEFAULT), 0, 0, null);
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
        this.prefferedSize = prefferedSize;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}