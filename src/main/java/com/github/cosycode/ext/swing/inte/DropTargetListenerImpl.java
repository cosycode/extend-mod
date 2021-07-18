package com.github.cosycode.ext.swing.inte;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <b>Description : </b> 拖拽实现监听包装类, 方便swing拖拽组件的使用
 * <p>
 * <b>created in </b> 2019/12/16
 *
 * @author CPF
 **/
public interface DropTargetListenerImpl extends DropTargetListener {

    /*
     * 1. 文件: 判断拖拽目标是否支持文件列表数据（即拖拽的是否是文件或文件夹, 支持同时拖拽多个）
     */
    @SuppressWarnings("unchecked")
    static List<File> getFileList(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以文件集合的形式获取数据
            return (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        }
        return Collections.emptyList();
    }

    /**
     * 获取文件名
     */
    static String getFilePath(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        final List<File> fileList = getFileList(dtde);
        if (fileList == null || fileList.isEmpty()) {
            return null;
        }
        return fileList.get(0).getAbsolutePath();
    }

    /*
     * 2. 文本: 判断拖拽目标是否支持文本数据（即拖拽的是否是文本内容, 或者是否支持以文本的形式获取）
     */
    static String getText(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以文本的形式获取数据
            return dtde.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();
        }
        return null;
    }

    /*
     * 3. 图片: 判断拖拽目标是否支持图片数据。注意: 拖拽图片不是指以文件的形式拖拽图片文件,
     *          而是指拖拽一个正在屏幕上显示的并且支持拖拽的图片（例如网页上显示的图片）。
     */
    static Object getPic(DropTargetDropEvent dtde) throws IOException, UnsupportedFlavorException {
        if (dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            // 接收拖拽目标数据
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            // 以图片的形式获取数据
            return dtde.getTransferable().getTransferData(DataFlavor.imageFlavor);
        }
        return null;
    }

    @Override
    default void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    default void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    default void dragExit(DropTargetEvent dte) {
    }

    @Override
    default void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    default void drop(DropTargetDropEvent dtde) {
        final boolean rst = dragHappen(dtde);
        // 如果此次拖拽的数据是被接受的, 则必须设置拖拽完成（否则可能会看到拖拽目标返回原位置, 造成视觉上以为是不支持拖拽的错误效果）
        dtde.dropComplete(rst);
    }

    boolean dragHappen(DropTargetDropEvent dropTargetDropEvent);
}