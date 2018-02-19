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
    private PictureToolbar toolbar;
    private PictureSource source;

    private Point pressed;
    private Rectangle dragRectangle;

    public PictureInPicture(PictureSource source) {
        this.source = source;
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
        frm.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                toolbar.setBounds(frm.getWidth() / 2 - toolbar.getWidth() / 2, frm.getHeight() / 2 - toolbar.getHeight() / 2, toolbar.getWidth(), toolbar.getHeight());
            }
        });

        frm.setContentPane(contentPane);

        ComponentResizer cr = new ComponentResizer();
        cr.registerComponent(frm);
        cr.setSnapSize(new Dimension(10, 10));
        cr.setMinimumSize(MIN_SIZE);
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
}