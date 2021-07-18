package com.github.cosycode.ext.swing.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/12/10
 * </p>
 *
 * @author CPF
 **/
@FunctionalInterface
public interface MouseReleasedListener extends MouseListener {

    @Override
    default void mouseClicked(MouseEvent e) {
    }

    @Override
    default void mouseReleased(MouseEvent e) {
    }

    @Override
    default void mouseEntered(MouseEvent e) {
    }

    @Override
    default void mouseExited(MouseEvent e) {
    }
}
