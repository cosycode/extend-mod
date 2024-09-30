package com.github.cosycode.ext.se.robot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * <b>Description : </b> 案件转换工具类
 * <p>
 * <b>created in </b> 2020/8/6
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class KeyPressUtils {

    /**
     * 第一行是字符, 第二行是第一行字符输出的键码值
     */
    private static final int[][] SINGLE_MAPPING = {
            {'\n', '\t', '\b', ' ', '-', '=', ';', '/', ',', '.', '[', ']', '\'', '`'},
            {'\n', '\t', '\b', ' ', '-', '=', ';', '/', ',', '.', '[', ']', KeyEvent.VK_QUOTE, KeyEvent.VK_BACK_QUOTE}
    };
    /**
     * 使用 SHIFT + 第二行字符按键键码值, 便能输出第一行字符
     */
    private static final int[][] SHIFT_AND_MAPPING = {
            {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '<', '>', '?', '|', ':', '{', '}', '_', '+', '"', '~'},
            {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ',', '.', '/', '\\', ';', '[', ']', '-', '=', KeyEvent.VK_QUOTE, KeyEvent.VK_BACK_QUOTE}
    };

    private KeyPressUtils() {
    }

    /**
     * 如果延迟时间 <= 0, 则直接跳过延迟
     *
     * @param delay 延迟时间
     */
    private static void delay(Robot r, int delay) {
        if (delay > 0) {
            r.delay(delay);
        }
    }

    /**
     * 单个 按键
     *
     * @param key   按键键码值
     * @param delay 延迟时间
     */
    public static void keyPressByInt(Robot r, int key, int delay) {
        r.keyPress(key);
        r.keyRelease(key);
        delay(r, delay);
    }

    /**
     * shift+ 按键
     *
     * @param r     Robot 实例对象
     * @param key   按键键码值
     * @param delay 按键之后延迟时间(毫秒)
     */
    public static void keyPressWithShift(Robot r, int key, int delay) {
        r.keyPress(KeyEvent.VK_SHIFT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_SHIFT);
        delay(r, delay);
    }

    /**
     * ctrl+ 按键
     *
     * @param r     Robot 实例对象
     * @param key   按键键码值
     * @param delay 按键之后延迟时间(毫秒)
     */
    public static void keyPressWithCtrl(Robot r, int key, int delay) {
        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_CONTROL);
        delay(r, delay);
    }

    /**
     * alt+ 按键
     *
     * @param r     Robot 实例对象
     * @param key   按键键码值
     * @param delay 按键之后延迟时间(毫秒)
     */
    public static void keyPressWithAlt(Robot r, int key, int delay) {
        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_ALT);
        delay(r, delay);
    }

    /**
     * 单个 按键, 输入 key
     * 调用该方法前需要保证当前系统为英文输入法, 并且没有开 Caps Lock 模式
     *
     * @param r     robot 对象
     * @param key   输入字符
     * @param delay 输入后延时
     */
    public static boolean keyPressForKeyEvent(Robot r, char key, int delay) {
        // 如果是小写字母, 则输入其大写字母所对应的键盘码值, 如果是大写字母, 则按下 shift 键的同时按下其大写字母键值
        if (Character.isLowerCase(key)) {
            keyPressByInt(r, Character.toUpperCase(key), delay);
            return true;
        }
        if (Character.isUpperCase(key)) {
            keyPressWithShift(r, key, delay);
            return true;
        }
        if (Character.isDigit(key)) {
            keyPressByInt(r, key, delay);
            return true;
        }
        final int i = ArrayUtils.indexOf(SINGLE_MAPPING[0], key);
        if (i >= 0) {
            keyPressByInt(r, SINGLE_MAPPING[1][i], delay);
            return true;
        }
        final int j = ArrayUtils.indexOf(SHIFT_AND_MAPPING[0], key);
        if (j >= 0) {
            keyPressWithShift(r, SHIFT_AND_MAPPING[1][j], delay);
            return true;
        }
        return false;
    }

    /**
     * 使用 Robot 利用键盘 alt 码输出字符
     *
     * @param r       Robot 对象
     * @param gbkCode 待打印的字符的国标区位码
     * @param delay   每个字符等待的时间
     */
    public static void keyPressWithString(Robot r, int gbkCode, int delay) {
        r.keyPress(KeyEvent.VK_ALT);
        String s = Integer.toString(gbkCode);
        for (char c : s.toCharArray()) {
            // 由 ASCII 码 0(48) 转为  VK_NUMPAD0(0x60)
            int k = c + 48;
            r.keyPress(k);
            r.keyRelease(k);
            r.delay(0);
        }
        r.keyRelease(KeyEvent.VK_ALT);
        if (delay > 0) {
            r.delay(delay);
        }
    }

    /**
     * 使用 Robot 利用键盘 alt 码输出字符
     *
     * @param r      Robot 对象
     * @param string 待打印的字符串
     * @param delay  每个字符等待的时间
     */
    public static void keyPressWithString(Robot r, String string, @NonNull Charset charset, int delay) throws CharacterCodingException {
        CharsetEncoder ce = charset.newEncoder();
        StringBuilder sb = null;
        for (char c : string.toCharArray()) {
            if (Character.isSurrogate(c)) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append(c);
                if (Character.isLowSurrogate(c)) {
                    log.warn("cannot print characters: {}", sb);
                    sb.setLength(0);
                }
                continue;
            }
            keyPressWithString(r, c, ce, delay);
        }
        if (sb != null && sb.length() > 0) {
            log.warn(sb.toString());
        }
    }

    /**
     * 使用 Robot 利用键盘输出字符,
     * 如果 ch 是ASCII码, 则使用标准键盘的单按键输出或 shift + 按键输出;
     * 如果 ch 是但 char 字符, 例如: 汉字, 则使用 ALT + 数字形式输出.
     * 如果 ch 是双 char 字符的一部分, 例如: 😀 则不输出.
     *
     * @param r              Robot 对象
     * @param ch             打印字符, 需要是单 char 长度, 如果 ch 只是一部分, 则不输出
     * @param charsetEncoder 如果 ch字符非 ASCII 字符, 在打印的时候需要先将 ch 转换为系统默认字符格式, 之后输出,
     *                       例如 ch 是一个汉字, 输出的位置键盘码是 GBK 编码(中国一般都是GBK), 那么在打印前需要先获取 ch 的 GBK 码
     * @param delay          每个字符等待的时间
     * @throws CharacterCodingException 字符转换异常
     */
    public static void keyPressWithString(Robot r, char ch, @NonNull CharsetEncoder charsetEncoder, int delay) throws CharacterCodingException {
        if (Character.isSurrogate(ch)) {
            return;
        }
        // 如果在128个字符之内, 则先以shift + 按键输出为主.
        if (ch >>> 8 == 0) {
            final boolean b = keyPressForKeyEvent(r, ch, delay);
            if (b) {
                return;
            }
            keyPressWithString(r, ch, delay);
            return;
        }
        CharBuffer cb = CharBuffer.wrap(new char[]{ch});
        final byte[] array = charsetEncoder.encode(cb).array();
        int code = Byte.toUnsignedInt(array[0]) << 8 | Byte.toUnsignedInt(array[1]);
        keyPressWithString(r, code, delay);
    }

}
