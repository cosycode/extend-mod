package com.github.cosycode.ext.swing.comp;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.Serializable;

/**
 * 自定义实现log4j2的输出源
 *
 * @Plugin..注解：这个注解，是为了在之后配置log4j2.xml时，指定的Appender Tag。
 * 构造函数：除了使用父类的以外，也可以增加一些自己的配置。
 * 重写append()方法：这里面需要实现具体的逻辑，日志的去向。
 * createAppender()方法：主要是接收log4j2.xml中的配置项。
 *
 * 若此类失效, 则看下配置文件是否被正确读入, 编译classpath路径下是否有log4j文件
 */
@Plugin(name = "JTextAreaAppender", category = "Core", elementType = "appender", printObject = true)
public class JTextAreaAppender extends AbstractAppender {

    @Getter
    protected final JTextPane jTextPane;

    protected JTextAreaAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                                final boolean ignoreExceptions, JTextPane jTextPane) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
        this.jTextPane = jTextPane;

        Style def = jTextPane.getStyledDocument().addStyle(null, null);
        StyleConstants.setFontFamily(def, "verdana");
        StyleConstants.setFontSize(def, 12);

        Style info = jTextPane.addStyle(Level.INFO.name(), def);
        StyleConstants.setForeground(info, Color.black);
        jTextPane.setParagraphAttributes(info, true);

        Style debug = jTextPane.addStyle(Level.DEBUG.name(), def);
        StyleConstants.setForeground(debug, Color.GRAY);
        jTextPane.setParagraphAttributes(debug, true);

        Style error = jTextPane.addStyle(Level.ERROR.name(), def);
        StyleConstants.setForeground(error, Color.RED);
        jTextPane.setParagraphAttributes(error, true);

        Style warn = jTextPane.addStyle(Level.WARN.name(), def);
        StyleConstants.setForeground(warn, Color.YELLOW);
        jTextPane.setParagraphAttributes(warn, true);
    }

    @Override
    public void append(LogEvent event) {
        //日志二进制文件，输出到指定位置就行
        try {
            final byte[] bytes = getLayout().toByteArray(event);
            Level level = event.getLevel();
            Style style = jTextPane.getStyle(level.name());
            Document document = jTextPane.getDocument();
            document.insertString(document.getLength(), new String(bytes), style);
            //下面这个是要实现的自定义逻辑
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        }
    }

    @Setter
    protected static JTextPane defaultJTextPane;

    /**
     * log4j2 使用 appender 插件工厂，因此传参可以直接通过 PluginAttribute 注解注入
     */
    @PluginFactory
    public static JTextAreaAppender createAppender(@PluginAttribute("name") String name,
                                                   @PluginElement("Filter") final Filter filter,
                                                   @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                   @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        if (defaultJTextPane == null) {
            throw new RuntimeException("事先请配置好 defaultJTextPane");
        } else {
            return new JTextAreaAppender(name, filter, layout, ignoreExceptions, defaultJTextPane);
        }
    }
}