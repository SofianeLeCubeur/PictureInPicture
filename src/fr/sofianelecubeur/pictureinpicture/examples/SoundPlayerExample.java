package fr.sofianelecubeur.pictureinpicture.examples;

import fr.sofianelecubeur.pictureinpicture.sources.SoundPlayerSource;
import fr.sofianelecubeur.pictureinpicture.utils.ImageHelper;
import fr.sofianelecubeur.pictureinpicture.PictureInPicture;
import fr.sofianelecubeur.pictureinpicture.ToolButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sofiane on 25/02/2018.
 *
 * @author Sofiane
 */
public class SoundPlayerExample {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame f = new JFrame("Choose songs");
        f.setLocation(-42, -42);
        f.setVisible(true);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Paths.get("").toAbsolutePath().normalize().toString()));
        chooser.setMultiSelectionEnabled(true);

        int a = chooser.showOpenDialog(f);
        f.setVisible(false);
        if(a != JFileChooser.APPROVE_OPTION) return;

        try {
            File[] files = chooser.getSelectedFiles();
            AtomicInteger cur = new AtomicInteger(0);

            SoundPlayerSource player = new SoundPlayerSource(files[cur.get()]);
            PictureInPicture pip = new PictureInPicture(player);

            try {
                InputStream in = PictureInPicture.class.getResourceAsStream("res/controls.png");
                BufferedImage i = ImageIO.read(in);
                BufferedImage[] controls = ImageHelper.getImageArray(i, 24, 24, 4);

                final Color col = Color.decode("#9E9E9E"), hover = Color.decode("#848484");
                pip.getQuickToolbar().add(new ToolButton("previous", new ImageIcon(ImageHelper.replace(controls[2], col)),
                        new ImageIcon(ImageHelper.replace(controls[2], hover)), e -> {}));
                pip.getQuickToolbar().add(new ToolButton("pause", new ImageIcon(ImageHelper.replace(controls[1], col)),
                        new ImageIcon(ImageHelper.replace(controls[1], hover)), e -> {
                    player.pause();
                }));
                pip.getQuickToolbar().add(new ToolButton("next", new ImageIcon(ImageHelper.replace(controls[3], col)),
                        new ImageIcon(ImageHelper.replace(controls[3], hover)), e -> {
                    if(files.length <= 1) return;
                    if(cur.incrementAndGet() >= files.length){
                        cur.set(0);
                    }
                    player.setTrack(files[cur.get()]);
                    player.start();
                }));

                player.setFinishListener(() -> {
                    if(cur.incrementAndGet() >= files.length){
                        cur.set(0);
                    }
                    player.setTrack(files[cur.get()]);
                    player.start();
                });

                in = PictureInPicture.class.getResourceAsStream("res/close.png");
                i = ImageIO.read(in);
                pip.getControlToolbar().add(new ToolButton("close", new ImageIcon(ImageHelper.replace(i, col)),
                        new ImageIcon(ImageHelper.replace(i, hover)), e -> {
                    pip.hide();
                    System.exit(0);
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pip.getQuickToolbar().setAnimated(false);
            pip.getControlToolbar().setAnimated(false);
            pip.show();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}