package com.github.cosycode.ext.swing.comp;

import com.github.cosycode.ext.swing.inte.DropTargetListenerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.IOException;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2020/11/18
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class JPathTextField extends JTextField {

    public JPathTextField() {
        this(null);
    }

    public JPathTextField(String text) {
        super(text);
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, (DropTargetListenerImpl) dropTargetDropEvent -> {
            try {
                final String filePath = DropTargetListenerImpl.getFilePath(dropTargetDropEvent);
                if (StringUtils.isNoneBlank(filePath)) {
                    setText(filePath);
                    return true;
                }
            } catch (IOException | UnsupportedFlavorException e) {
                log.error("failed to drop file", e);
            }
            return false;
        });
    }

}
