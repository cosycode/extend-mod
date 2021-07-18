package com.github.cosycode.ext.fileinport;

import com.github.cosycode.ext.fileimport.excel.ExcelResolver;
import com.github.cosycode.ext.fileimport.excel.ParsedSheetHandler;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class extractAllFieldOnExcel {

    public static final String path = "E:\\res\\FMBS_DEV_BANK\\新核心改造项目\\接收文档\\定报价平台数据源清单(1)(1).xls";

    public static void main(String[] args) throws IOException {

        File file = new File(path);
        File out = new File("D:\\Users\\CPF\\Desktop\\in\\out.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        List<ParsedSheetHandler<?>> resultList = new ArrayList<>();
        try (Workbook workbook = ExcelResolver.getMatchWorkbook(ExcelResolver.ExcelType.XLS, fileInputStream);
             FileWriter writer = new FileWriter(out)) {
            for (int index = 0, len = workbook.getNumberOfSheets(); index < len; index++) {
                Sheet sheet = workbook.getSheetAt(index);
                if (sheet == null) {
                    continue;
                }

                // 根据 sheet名称来查找指定的适配器
                String sheetName = sheet.getSheetName();

                sheet.rowIterator().forEachRemaining(sheetRow -> {
                    sheetRow.cellIterator().forEachRemaining(it -> {
                        try {
//                            writer.write(sheetName + "\t" + it.getRichStringCellValue() + "\n");
                            final Object o = ExcelResolver.resolveCell(it, String.class);
                            if (o != null) {
                                writer.write(sheetName + "\t" + o + "\n");
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    });
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
