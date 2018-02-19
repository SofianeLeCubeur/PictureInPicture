package fr.sofianelecubeur.pictureinpicture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sofiane on 18/02/2018.
 *
 * @author Sofiane
 */
public class Main {

    public static void main(String[] args){
        PictureSource source = new PictureSource() {
            @Override
            public void draw(Graphics g) {
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, getPrefferedSize().width, getPrefferedSize().height);
            }

            @Override
            public Dimension getPrefferedSize() {
                return new Dimension(480, 320);
            }
        };
        PictureInPicture pip = new PictureInPicture(source);

        try {
            InputStream in = Main.class.getResourceAsStream("res/upload.png");
            BufferedImage i = ImageIO.read(in);
            pip.getToolbar().add(new ToolButton("upload", new ImageIcon(IconFactory.replace(i, Color.decode("#616161"))),
                    new ImageIcon(IconFactory.replace(i, Color.decode("#212121"))), e -> {
                System.out.println("fr.sofianelecubeur.pictureinpicture.Main.main()");
                JFileChooser chooser = new JFileChooser();
                chooser.setVisible(true);
            }));
            in = Main.class.getResourceAsStream("res/exit.png");
            i = ImageIO.read(in);
            pip.getToolbar().add(new ToolButton("exit", new ImageIcon(IconFactory.replace(i, Color.decode("#616161"))),
                    new ImageIcon(IconFactory.replace(i, Color.decode("#212121"))), e -> {
                pip.hide();
                System.exit(0);
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pip.show();
    }

}