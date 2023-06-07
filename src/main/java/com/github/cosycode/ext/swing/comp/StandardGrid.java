package com.github.cosycode.ext.swing.comp;

import com.github.cosycode.common.base.IMapGetter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
public class StandardGrid<T extends IMapGetter<String, Object>> extends JPanel {

    @Getter
    private final JTable table;

    @Getter
    private final JScrollPane scrollPane;

    @Getter
    private final List<ColumnConfig> columnConfigList = new ArrayList<>();

    @Getter
    private final List<T> data = new ArrayList<>();

    @Getter
    private final AbstractTableModel tableModel = new StandardTableModel();

    public StandardGrid() {
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

    public void add(int index, T t) {
        data.add(index, t);
        tableModel.fireTableDataChanged();
    }

    public void addBatch(int index, Collection<T> collection) {
        data.addAll(index, collection);
        tableModel.fireTableRowsInserted(index, index + collection.size() - 1);
    }

    public void insert(int index, T t) {
        data.add(index, t);
        tableModel.fireTableRowsInserted(index, index);
    }

    public void addAll(Collection<T> c) {
        data.addAll(c);
        tableModel.fireTableDataChanged();
    }

    public void update(int index, T t) {
        data.set(index, t);
        tableModel.fireTableRowsUpdated(index, index);
    }

    public void delete(int index) {
        delete(index, index);
    }

    public void delete(int firstRow, int lastRow) {
        data.removeAll(data.subList(firstRow, lastRow));
        tableModel.fireTableRowsDeleted(firstRow, lastRow);
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

    /**
     * 设置标准table显示的行的数量, 根据行的数量设置滚动面板的高度
     *
     * @param rowSizeVisible 滚动面板设置显示行的数量
     */
    public void setPreferredScrollableViewportRowSize(int rowSizeVisible) {
        int cols = table.getColumnModel().getTotalColumnWidth();
        int rows = table.getRowHeight() * rowSizeVisible;
        Dimension d = new Dimension(cols,rows);
        table.setPreferredScrollableViewportSize(d);
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
    public class StandardTableModel extends AbstractTableModel {

        /**
         * 是否有序号列
         */
        @Setter
        private boolean serialNumberAble = true;

        /**
         * 表格是否可以编辑
         */
        @Setter
        private boolean cellEditable = true;

        @Getter
        @Setter
        private String orderHeaderName = "";

        @Override
        public String getColumnName(int column) {
            if (serialNumberAble) {
                if (column == 0) {
                    return orderHeaderName;
                } else {
                    column -= 1;
                }
            }
            return columnConfigList.get(column).getText();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            if (serialNumberAble) {
                return columnConfigList.size() + 1;
            }
            return columnConfigList.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (serialNumberAble) {
                if (col == 0) {
                    return row + 1;
                } else {
                    col -= 1;
                }
            }
            final T t = data.get(row);
            final String key = columnConfigList.get(col).getKey();
            return t.get(key);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (serialNumberAble && column == 0) {
                return false;
            }
            return cellEditable;
        }

        @Override
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
