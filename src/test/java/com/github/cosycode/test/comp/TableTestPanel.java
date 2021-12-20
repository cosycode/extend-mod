package com.github.cosycode.test.comp;

import com.github.cosycode.common.ext.bean.Record;
import com.github.cosycode.common.ext.hub.LazySingleton;
import com.github.cosycode.common.ext.hub.Throws;
import com.github.cosycode.ext.swing.comp.StandardGrid;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2021/9/30
 *
 * @author CPF
 * @since 1.0
 **/
public class TableTestPanel extends JPanel {

    private final LazySingleton<StandardGrid<Record>> tableLazySingleton = LazySingleton.of(() -> {
        final StandardGrid<Record> standardTable = new StandardGrid<>();
        ((StandardGrid<?>.StandardTableModel) standardTable.getTableModel()).setOrderHeaderName("order");
        standardTable.setColumnConfigList(Arrays.asList(
                new StandardGrid.ColumnConfig("id", "id"),
                new StandardGrid.ColumnConfig("type", "type"),
                new StandardGrid.ColumnConfig("deTime", "deTime")
        ));
        standardTable.setPreferredScrollableViewportRowSize(10);
        return standardTable;
    });


    private List<Record> getRecords(int start, int end, String tag) {
        return IntStream.range(start, end).mapToObj(n -> {
            Record record = new Record();
            record.put("id", n);
            record.put("type", tag);
            record.put("deTime", LocalDateTime.now());
            return record;
        }).collect(Collectors.toList());
    }

    public TableTestPanel() {
        setLayout(new BorderLayout(0, 0));
        // 透明
        this.setBackground(Color.gray);
        this.setOpaque(false);
        final StandardGrid<Record> instance = tableLazySingleton.instance();
        add(instance, BorderLayout.CENTER);
        extracted();
    }



    private void extracted() {
        final StandardGrid<Record> instance = tableLazySingleton.instance();

        instance.setData(getRecords(10, 20, "init"));



        new Thread(() -> {
            final AbstractTableModel tableModel = instance.getTableModel();
            final List<Record> data = instance.getData();
            Throws.con(1500, Thread::sleep);
            {
                final List<Record> collect = getRecords(31, 35, "add");
                data.addAll(8, collect);
                tableModel.fireTableRowsInserted(8, 1);
            }
            Throws.con(1500, Thread::sleep);
            {
                final List<Record> collect = getRecords(41, 45, "insert");
                data.addAll(8, collect);
                tableModel.fireTableRowsInserted(0, 0);
            }

        }).start();
    }
}
