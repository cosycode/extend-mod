package com.github.cosycode.ext.se.robot;

import com.github.cosycode.common.ext.hub.SimpleCode;
import com.github.cosycode.common.thread.AsynchronousProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.function.Consumer;

/**
 * <b>Description : </b>
 * 参考自: https://vimsky.com/examples/detail/java-method-java.awt.event.KeyEvent.getExtendedKeyCodeForChar.html
 * <p>
 * <b>created in </b> 2020/8/6
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class KeyPressDecorator {

    private final CharsetEncoder charsetEncoder = Charset.forName("GBK").newEncoder();
    @Getter
    private final AsynchronousProcessor<Character> asynchronousProcessor;
    @Getter
    private Robot robot;

    {
        final Consumer<Character> characterPredicate = ch -> SimpleCode.ignoreException(() -> KeyPressUtils.keyPressWithString(robot, ch, charsetEncoder, -1));
        asynchronousProcessor = AsynchronousProcessor.ofConsumer(characterPredicate).setName("KeyPressDecorator-Default");
        asynchronousProcessor.start();
    }

    public KeyPressDecorator(Robot robot) {
        this.robot = robot;
    }

    public void print(String s) {
        for (char aChar : s.toCharArray()) {
            asynchronousProcessor.add(aChar);
        }
    }

    public void print(char key) {
        asynchronousProcessor.add(key);
    }

}
