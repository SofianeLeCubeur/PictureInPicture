package fr.sofianelecubeur.pictureinpicture;

import fr.sofianelecubeur.pictureinpicture.sources.ImageSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sofiane on 18/02/2018.
 *
 * @author Sofiane
 */
public class Main {

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PictureSource source = new PictureSource() {
            @Override
            public void draw(Graphics g, Dimension currentSize) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, currentSize.width, currentSize.height);
            }

            @Override
            public Dimension getPrefferedSize() {
                return new Dimension(480, 320);
            }

            @Override
            public int getUpdateRate() {
                return 50;
            }

            @Override
            public Dimension getMaxmimumSize() {
                return null;
            }
        };
        PictureInPicture pip = new PictureInPicture(source);

        try {
            //InputStream in = Main.class.getResourceAsStream("res/upload.png");
           // BufferedImage i = ImageIO.read(in);
            /*pip.getToolbar().add(new ToolButton("upload", new ImageIcon(ImageHelper.replace(i, Color.decode("#F5F7FA"))),
                    new ImageIcon(ImageHelper.replace(i, Color.decode("#9E9E9E"))), e -> {
                JFileChooser chooser = new JFileChooser();
                int a = chooser.showOpenDialog(null);
                if (a == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        if(img != null) {
                            pip.setSource(new ImageSource(new Dimension(480, 320), img));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }));*/
            InputStream in = Main.class.getResourceAsStream("res/close.png");
            BufferedImage i = ImageIO.read(in);
            pip.getControlToolbar().add(new ToolButton("exit", new ImageIcon(ImageHelper.replace(i, Color.decode("#F5F7FA"))),
                    new ImageIcon(ImageHelper.replace(i, Color.decode("#9E9E9E"))), e -> {
                pip.hide();
                System.exit(0);
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pip.show();
    }

}