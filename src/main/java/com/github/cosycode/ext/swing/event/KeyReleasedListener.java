package com.github.cosycode.ext.swing.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/11/10
 * </p>
 *
 * @author CPF
 **/
@FunctionalInterface
public interface KeyReleasedListener extends KeyListener {

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     */
    @Override
    default void keyTyped(KeyEvent e) {
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     */
    @Override
    default void keyPressed(KeyEvent e) {
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     */
    @Override
    public void keyReleased(KeyEvent e);

}
