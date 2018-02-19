package fr.sofianelecubeur.pictureinpicture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Sofiane on 18/02/2018.
 *
 * @author Sofiane
 */
public class PictureInPicture {

    private static final Dimension MIN_SIZE = new Dimension(192, 128);

    private JWindow frm;
    private ComponentResizer cr;
    private PictureToolbar toolbar;
    private PictureSource source;

    private Point pressed;
    private JPanel sc;
    private Timer updater;
    private Rectangle dragRectangle;

    public PictureInPicture(PictureSource source) {
        this.source = source;
        this.cr = new ComponentResizer();
        this.toolbar = new PictureToolbar();
    }

    public void show(){
        frm = new JWindow();
        frm.setName("PictureInPicture");
        frm.setAlwaysOnTop(true);
        frm.setFocusableWindowState(false);
        frm.setLocationRelativeTo(null);

        frm.setSize(source.getPrefferedSize());
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frm.getGraphicsConfiguration());
        frm.setLocation(scrSize.width - 5 - frm.getWidth(), scrSize.height - toolHeight.bottom - 5 - frm.getHeight());

        JLayeredPane contentPane = new JLayeredPane();

        toolbar.setForeground(Color.RED);
        toolbar.setBounds(frm.getWidth() / 2 - toolbar.getWidth() / 2, frm.getHeight() / 2 - toolbar.getHeight() / 2, toolbar.getWidth(), toolbar.getHeight());
        contentPane.add(toolbar, JLayeredPane.POPUP_LAYER);

        sc = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                source.draw(g, frm.getSize());
            }
        };
        sc.setBounds(0, 0, frm.getWidth(), frm.getHeight());
        contentPane.add(sc);

        frm.setContentPane(contentPane);

        frm.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                sc.setBounds(0, 0, frm.getWidth(), frm.getHeight());
                toolbar.setBounds(frm.getWidth() / 2 - toolbar.getWidth() / 2, frm.getHeight() / 2 - toolbar.getHeight() / 2, toolbar.getWidth(), toolbar.getHeight());
            }
        });

        cr.registerComponent(frm);
        cr.setSnapSize(new Dimension(10, 10));
        cr.setMinimumSize(MIN_SIZE);
        if(source.getMaxmimumSize() != null){
            cr.setMaximumSize(source.getMaxmimumSize());
        }
        frm.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                toolbar.fadeIn();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(e.getX() < 0 || e.getY() < 0 || e.getX() > frm.getWidth() || e.getY() > frm.getHeight()) {
                    toolbar.fadeOut();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = e.getPoint();
            }
        });
        frm.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(dragRectangle != null && dragRectangle.contains(e.getPoint())) {
                    Component component = e.getComponent();
                    Point location = frm.getLocation();
                    int x = location.x - (int) pressed.getX() + e.getX();
                    int y = location.y - (int) pressed.getY() + e.getY();
                    component.setLocation(x, y);
                }
            }
        });

        frm.setVisible(true);
        if(source.getUpdateRate() > 0){
            updater = new Timer(source.getUpdateRate(), e -> sc.repaint());
            updater.start();
        }
    }

    public void hide(){
        if(frm != null){
            frm.setVisible(false);
        }
    }

    public PictureToolbar getToolbar() {
        return toolbar;
    }

    public void setDragRectangle(Rectangle dragRectangle) {
        this.dragRectangle = dragRectangle;
    }

    public Rectangle getDragRectangle() {
        return dragRectangle;
    }

    public void setSource(PictureSource source) {
        this.source = source;
        if(updater != null){
            updater.stop();
        }
        if(source.getUpdateRate() > 0){
            updater = new Timer(source.getUpdateRate(), e -> sc.repaint());
            updater.start();
        }
        if(frm != null){
            frm.setSize(source.getPrefferedSize());
            frm.revalidate();
            frm.repaint();
        }
        if(source.getMaxmimumSize() != null){
            cr.setMaximumSize(source.getMaxmimumSize());
        }
    }

    public PictureSource getSource() {
        return source;
    }
}