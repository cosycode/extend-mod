package com.github.cosycode.ext.swing.comp;

import com.github.cosycode.ext.swing.inte.DropTargetListenerImpl;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.IOException;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * @date 2020/11/18 15:33
 **/
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
                e.printStackTrace();
            }
            return false;
        });
    }

}
