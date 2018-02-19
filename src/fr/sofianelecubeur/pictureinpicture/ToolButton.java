package fr.sofianelecubeur.pictureinpicture;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Created by Sofiane on 19/02/2018.
 *
 * @author Sofiane
 */
public class ToolButton {

    public static final String HOVER_ICON = "HoverIcon";

    private final AbstractAction action;

    public ToolButton(String name, Icon icon, Icon hoverIcon, Consumer<ActionEvent> action) {
        this.action = new AbstractAction(name, icon) {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.accept(e);
            }
        };
        this.action.putValue(HOVER_ICON, hoverIcon);
    }

    public AbstractAction toAbstractAction() {
        return action;
    }
}