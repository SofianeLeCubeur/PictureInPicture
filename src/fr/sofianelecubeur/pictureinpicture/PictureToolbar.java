package fr.sofianelecubeur.pictureinpicture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Sofiane on 19/02/2018.
 *
 * @author Sofiane
 */
public class PictureToolbar extends JComponent {

    public static final int BUTTON_SIZE = 22, PADDING = 2;

    private List<AbstractAction> actions;
    private Timer localTimer;
    private boolean animated = true;
    private float alpha = 0f;
    private int hoverIndex = -1;

    private final Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR), old;

    public PictureToolbar() {
        this(new ArrayList<>());
    }

    public PictureToolbar(List<AbstractAction> actions) {
        this.actions = actions;
        this.old = getCursor();
        setOpaque(false);
        setSize((BUTTON_SIZE + PADDING) * actions.size(), BUTTON_SIZE);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int index = x / (BUTTON_SIZE + PADDING);
                if(index >= 0 && index < actions.size() && actions.get(index) != null){
                    hoverIndex = index;
                    setCursor(hand);
                    repaint();
                } else {
                    if(getCursor() == hand){
                        setCursor(old);
                    }
                }
            }
        });
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                alpha = 0;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int index = x / (BUTTON_SIZE + PADDING);
                if(index >= 0 && index < actions.size() && actions.get(index) != null){
                    actions.get(index).actionPerformed(new ActionEvent(this, index, "click"));
                }
            }
        });
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public boolean isAnimated() {
        return animated;
    }

    protected void fadeIn(){
        if(!animated || alpha >= 1f) return;
        if(localTimer != null){
            localTimer.stop();
        }
        localTimer = new Timer(50, e1 -> {
            if(alpha >= 1f){
                localTimer.stop();
                return;
            }
            alpha += 0.15f;
            if(alpha >= 1f) alpha = 1;
            repaint();
        });
        localTimer.start();
    }

    protected void fadeOut(){
        if(!animated || alpha <= 0f) return;
        if(localTimer != null){
            localTimer.stop();
        }
        localTimer = new Timer(50, e1 -> {
            if(alpha <= 0f){
                localTimer.stop();
                return;
            }
            alpha -= 0.15f;
            if(alpha <= 0f) alpha = 0;
            repaint();
        });
        localTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(1f));
        if(animated) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        g2.setColor(getForeground());
        int i = 0;
        for (AbstractAction action : actions){
            if(action.isEnabled()){
                Object icon = action.getValue(Action.SMALL_ICON);
                if(icon != null && icon instanceof Icon){
                    if(i >= 0 && hoverIndex == i){
                        if(action.getValue(ToolButton.HOVER_ICON) != null && action.getValue(ToolButton.HOVER_ICON) instanceof Icon){
                            ((Icon) action.getValue(ToolButton.HOVER_ICON)).paintIcon(this, g2, 2 + (i * (BUTTON_SIZE + PADDING / 2)), 2);
                        } else {
                            ((Icon) icon).paintIcon(this, g2, 2 + (i * (BUTTON_SIZE + PADDING / 2)), 2);
                            g2.drawRect(1 + (i * (BUTTON_SIZE + PADDING / 2)), 1, 22, 22);
                        }
                    } else {
                        ((Icon) icon).paintIcon(this, g2, 2 + (i * (BUTTON_SIZE + PADDING / 2)), 2);
                    }
                }
            }
            i++;
        }
    }

    public void add(ToolButton action){
        actions.add(action.toAbstractAction());
        setSize((BUTTON_SIZE + PADDING) * actions.size(), BUTTON_SIZE + PADDING);
    }

    public List<AbstractAction> getActions() {
        return actions;
    }
}