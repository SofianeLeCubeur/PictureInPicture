package fr.sofianelecubeur.pictureinpicture.sources;

import fr.sofianelecubeur.pictureinpicture.PictureSource;
import fr.sofianelecubeur.pictureinpicture.utils.PausablePlayer;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

/**
 * Created by Sofiane on 25/02/2018.
 *
 * @author Sofiane
 */
public class SoundPlayerSource implements PictureSource {

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

    private PausablePlayer player;
    private String name;
    private long total, lastPosition;

    private Cursor old, resize = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    public SoundPlayerSource(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        this.player = new PausablePlayer(in);
        this.total = PausablePlayer.getSoundDuration(in);
        this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    public void setTrack(File file){
        try {
            stop();
            FileInputStream in = new FileInputStream(file);
            this.player = new PausablePlayer(in);
            this.total = PausablePlayer.getSoundDuration(in);
            this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        player.play();
    }

    public void pause(){
        if(player.getState() == PausablePlayer.PAUSED){
            player.resume();
        } else {
            player.pause();
        }
    }

    public void stop(){
        player.stop();
    }

    @Override
    public void init(JComponent component) {
        component.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Rectangle bounds = new Rectangle(0, 0, component.getWidth(), 10);
                Cursor cur = component.getCursor();
                if(bounds.contains(e.getPoint())){
                    if(old == null){
                        old = cur;
                    }
                    if(cur != resize) {
                        component.setCursor(resize);
                    }
                } else if(cur == resize && old != null){
                    component.setCursor(old);
                }
            }
        });
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Rectangle bounds = new Rectangle(0, 0, component.getWidth(), 10);
                if(bounds.contains(e.getPoint())){
                    int percent = (int)(e.getX() / (component.getWidth() * 1f) * 100);
                    long time = (long)player.getPosition();
                    lastPosition = time;
                    long remains = (percent * total / 100);
                    player.skip(player.millisToFrames(remains - time));
                }

            }
        });
    }

    @Override
    public void draw(Graphics g, Dimension currentSize) {
        Graphics2D g2 = (Graphics2D) g;
        int w = currentSize.width, h = currentSize.height;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.decode("#F5F7FA"));
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.decode("#CCD1D9"));
        g2.fillRect(0, 0, w, 10);

        long time = (long)player.getPosition();
        lastPosition = time;
        int percent = (int)(time / (total * 1f) * 100);

        g2.setColor(Color.decode("#656D78"));
        int pixels = (percent * w / 100);
        g2.fillRect(0, 0, pixels, 10);

        final Font font = g2.getFont();
        FontMetrics fm = SwingUtilities2.getFontMetrics(null, font.deriveFont(12f));
        String s = timeFormat.format(time);
        g2.setFont(font.deriveFont(12f));
        g2.setColor(Color.decode("#656D78").darker());
        g2.drawString(name, w /2 - fm.stringWidth(name) / 2, 21);
        g2.setFont(font);
        fm = SwingUtilities2.getFontMetrics(null, font);
        g2.drawString(s, w / 2 - fm.stringWidth(s) / 2, 21 + fm.getHeight());
    }

    @Override
    public int getUpdateRate() {
        return 100;
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public Dimension getPrefferedSize() {
        return new Dimension(250, 64);
    }

    @Override
    public Dimension getMaxmimumSize() {
        return null;
    }

    public void setFinishListener(Runnable runnable){
        player.setOnFinishListener(runnable);
    }
}