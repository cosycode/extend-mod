package com.github.cosycode.ext.swing.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/12/10 15:11
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
