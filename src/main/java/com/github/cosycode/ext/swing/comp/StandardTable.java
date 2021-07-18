package com.github.cosycode.ext.swing.comp;

import com.github.cosycode.common.base.IMapGetter;
import lombok.Data;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <b>Description : </b> swing 标准表格类
 * <p>
 * <b>created in </b> 2020/11/18
 * </p>
 *
 * @author CPF
 **/
public class StandardTable<T extends IMapGetter<String, Object>> extends JPanel {

    @Getter
    private final JTable table;

    @Getter
    private final JScrollPane scrollPane;

    @Getter
    private final List<ColumnConfig> columnConfigList = new ArrayList<>();

    @Getter
    private final List<T> data = new ArrayList<>();

    private final AbstractTableModel tableModel = new StandardTableModel();

    public StandardTable() {
        table = new JTable(tableModel);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setAutoscrolls(true);
        setLayout(new BorderLayout());
        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        add(table.getTableHeader(), BorderLayout.NORTH);

        // 把 表格内容 添加到容器中心
        scrollPane = new JScrollPane();
        scrollPane.getViewport().add(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void add(T t) {
        data.add(t);
        tableModel.fireTableDataChanged();
    }

    public void addAll(Collection<T> c) {
        data.addAll(c);
        tableModel.fireTableDataChanged();
    }

    public void setData(Collection<T> c) {
        data.clear();
        data.addAll(c);
        tableModel.fireTableDataChanged();
    }

    public void setColumnConfigList(List<ColumnConfig> list) {
        columnConfigList.clear();
        columnConfigList.addAll(list);
        tableModel.fireTableStructureChanged();
    }

    public void fireTableDataChanged() {
        tableModel.fireTableDataChanged();
    }

    public void fireTableStructureChanged() {
        tableModel.fireTableStructureChanged();
    }

    @Data
    public static class ColumnConfig {
        private final String key;
        private int index;
        private int width;
        private String text;

        public ColumnConfig(String key, String text) {
            this(key, text, -1, -1);
        }

        public ColumnConfig(String key, String text, int index, int width) {
            this.key = key;
            this.index = index;
            this.width = width;
            this.text = text;
        }
    }

    /**
     * 标准 Table 模板
     */
    private class StandardTableModel extends AbstractTableModel {

        /**
         * 是否有序号列
         */
        private boolean serialNumberAble = true;

        /**
         * 表格是否可以编辑
         */
        private boolean cellEditable = true;

        public String getColumnName(int column) {
            if (serialNumberAble) {
                if (column == 0) {
                    return "序号";
                } else {
                    column -= 1;
                }
            }
            return columnConfigList.get(column).getText();
        }

        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            if (serialNumberAble) {
                return columnConfigList.size() + 1;
            }
            return columnConfigList.size();
        }

        public Object getValueAt(int row, int col) {
            if (serialNumberAble) {
                if (col == 0) {
                    return row;
                } else {
                    col -= 1;
                }
            }
            final T t = data.get(row);
            final String key = columnConfigList.get(col).getKey();
            return t.get(key);
        }

        public boolean isCellEditable(int row, int column) {
            if (serialNumberAble && column == 0) {
                return false;
            }
            return cellEditable;
        }

        public void setValueAt(Object value, int row, int col) {
            if (serialNumberAble) {
                if (col == 0) {
                    return;
                } else {
                    col -= 1;
                }
            }
            final T t = data.get(row);
            final String key = columnConfigList.get(col).getKey();
            t.put(key, value);
            fireTableCellUpdated(row, col);
        }

    }

}
