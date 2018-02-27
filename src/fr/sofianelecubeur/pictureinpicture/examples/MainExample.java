package fr.sofianelecubeur.pictureinpicture.examples;

import fr.sofianelecubeur.pictureinpicture.PictureInPicture;
import fr.sofianelecubeur.pictureinpicture.PictureSource;
import fr.sofianelecubeur.pictureinpicture.ToolButton;
import fr.sofianelecubeur.pictureinpicture.utils.ImageHelper;

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
public class MainExample {

    public static void main(String[] args){
        if(args.length >= 1 && args[0].equalsIgnoreCase("sp")){
            SoundPlayerExample.main(args);
            return;
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        PictureSource source = new PictureSource() {
            @Override
            public void init(JComponent component) {
            }

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
                return 0;
            }

            @Override
            public boolean isResizable() {
                return true;
            }

            @Override
            public Dimension getMaxmimumSize() {
                return null;
            }
        };
        PictureInPicture pip = new PictureInPicture(source);

        try {
            //InputStream in = MainExample.class.getResourceAsStream("res/upload.png");
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
            InputStream in = PictureInPicture.class.getResourceAsStream("res/close.png");
            BufferedImage i = ImageIO.read(in);
            pip.getControlToolbar().add(new ToolButton("close", new ImageIcon(ImageHelper.replace(i, Color.decode("#9E9E9E"))),
                    new ImageIcon(ImageHelper.replace(i, Color.decode("#F5F7FA"))), e -> {
                pip.hide();
                System.exit(0);
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pip.show();
    }

}