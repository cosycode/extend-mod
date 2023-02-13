package com.github.cosycode.ext.se.util;

import com.github.cosycode.common.util.otr.PrintTool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/2/3
 * </p>
 *
 * @author pengfchen
 * @since 1.0
 **/
@Slf4j
public class LogRecords {

    @Getter
    private static final LogRecord defaultRecord = new LogRecord();
    @Getter
    private static final List<LogRecord> records = new ArrayList<>();

    public static void addRecord(LogRecord logRecord) {
        if (logRecord != null) {
            records.add(logRecord);
        }
    }

    public static void clear() {
        defaultRecord.clear();
    }

    public LogRecord append(String str, Object... objects) {
        return defaultRecord.append(str, objects);
    }

    public LogRecord logAndAppend(String str, Object... objects) {
        return defaultRecord.logAndAppend(str, objects);
    }

    public void printAllRow() {
        defaultRecord.printAllRow();
    }

    public static class LogRecord {

        private final List<String> rows = new ArrayList<>();

        public void clear() {
            rows.clear();
        }

        public LogRecord append(String str, Object... objects) {
            rows.add(PrintTool.format(str, objects));
            return this;
        }

        public LogRecord logAndAppend(String str, Object... objects) {
            String format = PrintTool.format(str, objects);
            rows.add(format);
            PrintTool.info(format);
            return this;
        }

        public void printAllRow() {
            for (String s : rows) {
                PrintTool.info(s);
            }
        }

    }

}
