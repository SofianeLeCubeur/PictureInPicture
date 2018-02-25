package fr.sofianelecubeur.pictureinpicture;

import sun.swing.SwingUtilities2;

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
    private PictureToolbar quickToolbar, controlToolbar;
    private PictureSource source;

    private Point pressed;
    private JPanel sc;
    private Timer updater;
    private Rectangle draggable;
    private boolean dragging;
    private Cursor moveCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), old;

    public PictureInPicture(PictureSource source) {
        this.source = source;
        this.cr = new ComponentResizer();
        this.quickToolbar = new PictureToolbar();
        this.controlToolbar = new PictureToolbar();
    }

    public void show(){
        frm = new JWindow();
        frm.setName("PictureInPicture");
        frm.setAlwaysOnTop(true);
        frm.setFocusableWindowState(false);
        frm.setType(Window.Type.POPUP);
        frm.setLocationRelativeTo(null);

        frm.setSize(source.getPrefferedSize());
        final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frm.getGraphicsConfiguration());
        frm.setLocation(scrSize.width - 5 - frm.getWidth(), scrSize.height - toolHeight.bottom - 5 - frm.getHeight());

        this.draggable = new Rectangle(10, 10, frm.getWidth() - 20, 20);

        JLayeredPane contentPane = new JLayeredPane();

        quickToolbar.setBounds(frm.getWidth() / 2 - quickToolbar.getWidth() / 2, frm.getHeight() - quickToolbar.getHeight() - 3,
                quickToolbar.getWidth(), quickToolbar.getHeight());
        contentPane.add(quickToolbar, JLayeredPane.POPUP_LAYER);

        controlToolbar.setBounds(frm.getWidth() - controlToolbar.getWidth() - 6, 5, controlToolbar.getWidth(), controlToolbar.getHeight());
        contentPane.add(controlToolbar, JLayeredPane.POPUP_LAYER);

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
                final int w = frm.getWidth(), h = frm.getHeight();
                System.out.println(w + "/" + h);
                sc.setBounds(0, 0, w, h);
                quickToolbar.setBounds(w / 2 - quickToolbar.getWidth() / 2, h - quickToolbar.getHeight() - 3, quickToolbar.getWidth(), quickToolbar.getHeight());
                System.out.println(controlToolbar.getHeight());
                controlToolbar.setBounds(w - controlToolbar.getWidth() - 6, 5, controlToolbar.getWidth(), controlToolbar.getHeight());
                draggable = new Rectangle(10, 10, w - 20, 20);
            }
        });

        cr.registerComponent(frm);
        cr.setSnapSize(new Dimension(10, 10));
        cr.setMinimumSize(MIN_SIZE);
        if(source.getMaxmimumSize() != null){
            cr.setMaximumSize(source.getMaxmimumSize());
        } else {
            cr.setMaximumSize(new Dimension(scrSize.width, scrSize.height - toolHeight.bottom));
        }
        frm.addMouseListener(new MouseAdapter() {

            boolean a;

            @Override
            public void mouseEntered(MouseEvent e) {
                if(!a) {
                    controlToolbar.fadeIn();
                    quickToolbar.fadeIn();
                    a = true;
                }
                if(old != null){
                    e.getComponent().setCursor(old);
                    old = null;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(a && !frm.getBounds().contains(e.getPoint())) {
                    controlToolbar.fadeOut();
                    quickToolbar.fadeOut();
                    a = false;
                }
                if(old != null){
                    e.getComponent().setCursor(old);
                    old = null;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressed = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });
        frm.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if(draggable.contains(e.getPoint())){
                    if(old == null){
                        old = frm.getCursor();
                    } else if(frm.getCursor() != moveCursor) {
                        frm.setCursor(moveCursor);
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                final Component component = e.getComponent();
                if((dragging || draggable.contains(e.getPoint())) && !cr.isResizing()) {
                    Point location = frm.getLocation();
                    int x = location.x - (int) pressed.getX() + e.getX();
                    int y = location.y - (int) pressed.getY() + e.getY();

                    if(x <= 5){
                        x = 2;
                    }
                    if(y <= 5){
                        y = 2;
                    }

                    if(x >= (scrSize.width - frm.getWidth() - 5)){
                        x = scrSize.width - frm.getWidth() - 2;
                    }

                    if(y >= (scrSize.height - frm.getHeight() - 5)){
                        y = scrSize.height - frm.getHeight() - 2;
                    }

                    component.setLocation(x, y);
                    dragging = true;
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
            if(updater != null){
                updater.stop();
            }
        }
    }

    public PictureToolbar getControlToolbar() {
        return controlToolbar;
    }

    public PictureToolbar getQuickToolbar() {
        return quickToolbar;
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
            cr.deregisterComponent(frm);
            frm.setSize(source.getPrefferedSize());
            frm.revalidate();
            frm.repaint();
        }
        if(source.isResizable()){
            cr.registerComponent(frm);
        }
        if(source.getMaxmimumSize() != null){
            cr.setMaximumSize(source.getMaxmimumSize());
        }
    }

    public PictureSource getSource() {
        return source;
    }
}