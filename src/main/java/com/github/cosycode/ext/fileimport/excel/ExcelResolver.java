package com.github.cosycode.ext.fileimport.excel;

import com.github.cosycode.ext.fileimport.base.FieldMapping;
import com.github.cosycode.ext.fileimport.base.RecordMapping;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> 解析Excel的工具类
 * <p>
 * <b>created in </b> 2019/8/15
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class ExcelResolver {

    /**
     * 判断Excel的版本,获取Workbook
     */
    public static Workbook getMatchWorkbook(@NonNull ExcelType excelType, InputStream in) throws IOException {
        if (excelType == ExcelType.XLS) {
            return new HSSFWorkbook(in);
        } else if (excelType == ExcelType.XLSX) {
            return new XSSFWorkbook(in);
        }
        throw new RuntimeException("不支持的文件类型");
    }

    /**
     * 使用规定的解析器解析excel文件, 并将解析的结果返回
     *
     * @param is              excel 文件流
     * @param excelType       excel 文件类型
     * @param adapterClassSet 适配器 Set
     * @return Excel 解析后的 Map<AbstractSheetBeanMappingAdapter, List<Bean>>
     * @throws IOException               文件流异常
     * @throws InstantiationException    class.newInstants 异常
     * @throws IllegalAccessException    map转Bean异常
     * @throws InvocationTargetException map转Bean异常
     */
    public static List<ParsedSheetHandler<?>> resolveRecord(@NonNull InputStream is, @NonNull ExcelType excelType,
                                                            @NonNull Set<Class<? extends AbstractSheetBeanMappingAdapter<?>>> adapterClassSet)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // 初始化适配器
        List<AbstractSheetBeanMappingAdapter<?>> adapters = adapterClassSet.stream().map(it -> {
            AbstractSheetBeanMappingAdapter<?> abstractSheetBeanMappingAdapter = null;
            try {
                abstractSheetBeanMappingAdapter = it.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("new Instance error", e);
            }
            return abstractSheetBeanMappingAdapter;
        }).collect(Collectors.toList());
        return resolveRecord(is, excelType, adapters);
    }

    /**
     * 使用规定的解析器解析excel文件, 并将解析的结果返回
     *
     * @param is        excel 文件流
     * @param excelType excel 文件类型
     * @param adapters  适配器 adapters
     * @return Excel 解析后的 Map<AbstractSheetBeanMappingAdapter, List<Bean>>
     * @throws IOException               文件流异常
     * @throws InstantiationException    class.newInstants 异常
     * @throws IllegalAccessException    map转Bean异常
     * @throws InvocationTargetException map转Bean异常
     */
    public static List<ParsedSheetHandler<?>> resolveRecord(@NonNull InputStream is, @NonNull ExcelType excelType, List<AbstractSheetBeanMappingAdapter<?>> adapters)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // 结果集
        List<ParsedSheetHandler<?>> resultList = new ArrayList<>();
        try (Workbook workbook = getMatchWorkbook(excelType, is)) {
            for (int index = 0, len = workbook.getNumberOfSheets(); index < len; index++) {
                Sheet sheet = workbook.getSheetAt(index);
                if (sheet == null) {
                    continue;
                }

                // 根据 sheet名称来查找指定的适配器
                String sheetName = sheet.getSheetName();
                List<AbstractSheetBeanMappingAdapter<?>> collect = adapters.stream().filter(it -> it.isMatchSheetName(sheetName)).collect(Collectors.toList());
                if (collect.isEmpty()) {
                    continue;
                }
                if (collect.size() > 1) {
                    throw new RuntimeException("函数调用错误, AbstractSheetBeanMappingAdapter 冲突, resolveQuoteExcel 解析时发现匹配的 SheetBeanMapping 有多个");
                }
                AbstractSheetBeanMappingAdapter<?> relateAdapter = collect.get(0);
                SheetInfo sheetInfo = new SheetInfo();
                sheetInfo.setSheetName(sheetName);
                log.info("resolveQuoteExcel start!!! -> sheetName : {}", sheetName);
                RecordMapping recordMapping = relateAdapter.getSheetBeanMapping();
                // 解析sheet, 封装为 List<Map<String, Object>> 形式
                List<Map<String, Object>> maps = ExcelResolver.analyzeSheet(sheet, recordMapping);
                // 对从excel中解析出的list进行二次处理, 返回 list
                ParsedSheetHandler<?> parsedSheetHandler = relateAdapter.disposeParsedSheet(sheetInfo, maps);
                // 封入Map
                resultList.add(parsedSheetHandler);
            }
        }
        return resultList;
    }

    /**
     * 解析单个 sheet
     *
     * @param sheet                表格
     * @param sheetBeanMappingRule 规则
     * @return 从sheet里面读取并转换成的List集合
     */
    private static List<Map<String, Object>> analyzeSheet(Sheet sheet, @NonNull RecordMapping sheetBeanMappingRule) {
        if (sheet == null) {
            return new ArrayList<>();
        }
        // 1. 验证sheet里面的数据是否有效
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        if (firstRowNum == lastRowNum) {
            // 数据为空
            return new ArrayList<>();
        }
        final String sheetName = sheet.getSheetName();
        final Row firstRow = sheet.getRow(firstRowNum);
        final short firstCellNum = firstRow.getFirstCellNum();
        final short lastCellNum = firstRow.getLastCellNum();

        log.info("sheetName : {}, (topRowNum, lastRowNum)=>({}, {})", sheetName, firstRowNum, lastRowNum);

        // 2. 解析映射字段
        List<Integer> columnIndexList = new ArrayList<>();
        for (int i = firstCellNum; i < lastCellNum; i++) {
            Cell cell = firstRow.getCell(i);
            String stringCellValue = cell.getStringCellValue();
            if (StringUtils.isNotBlank(stringCellValue)) {
                columnIndexList.add(cell.getColumnIndex());
                sheetBeanMappingRule.putPositionMapping(cell.getColumnIndex(), stringCellValue);
            }
        }
        // 检查映射字段是否有异常情况, 是否有缺少
        List<String> notFoundHeaderName = sheetBeanMappingRule.getNotFoundHeaderName();
        if (!notFoundHeaderName.isEmpty()) {
            final String temp = "excel中未发现必要的列, 请检查模板, <br> => 表名 : %s<br> => 列 : [%s]";
            String msg = String.format(temp, sheetName, StringUtils.join(notFoundHeaderName, ", "));
            log.warn(msg);
            throw new RuntimeException(msg);
        }

        // 3. 解析sheet数据
        List<Map<String, Object>> records = new ArrayList<>();
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
            Row sheetRow = sheet.getRow(rowNum);
            // 解析行
            Map<String, Object> record = analyzeRow(sheetBeanMappingRule, sheetName, columnIndexList, sheetRow);
            records.add(record);
        }
        return records;
    }

    /**
     * 分析一行, 将一行数据解析成一个 Map<String, Object>
     *
     * @param sheetBeanMappingRule 规则
     * @param sheetName            sheet名
     * @param columnIndexList      有效数据集合
     * @param sheetRow             行
     */
    private static Map<String, Object> analyzeRow(@NonNull RecordMapping sheetBeanMappingRule, String sheetName, List<Integer> columnIndexList, Row sheetRow) {
        Map<String, Object> record = new HashMap<>();
        columnIndexList.forEach(colIndex -> {
            if (sheetRow.getPhysicalNumberOfCells() == 0) {
                return;
            }
            Cell cell = sheetRow.getCell(colIndex);
            FieldMapping<?> fieldMapping = sheetBeanMappingRule.getFieldMappingByCellNum(colIndex);
            // 未配置的行不管
            if (fieldMapping == null) {
                return;
            }
            try {
                Object obj = null;
                if (cell != null) {
                    // 解析cell值
                    obj = resolveCell(cell, fieldMapping.getType());
                    // String类型进行正则检查
                    obj = fieldMapping.resolveValue(obj);
                }
                // 填充数据
                if (obj != null) {
                    record.put(fieldMapping.getFieldName(), obj);
                    return;
                }
                if (fieldMapping.getDefaultObj() != null) {
                    record.put(fieldMapping.getFieldName(), fieldMapping.getDefaultObj());
                    return;
                }
                if (fieldMapping.isRequire()) {
                    throw new ParseException("不能为空", 0);
                }
            } catch (RuntimeException | ParseException e) {
                CellAddress cellAddress = new CellAddress(sheetRow.getRowNum(), colIndex);
                String msg = String.format("解析单元格错误<br>=> 表名 : %s<br>=> 位置 : %s<br>=> 值 : %s<br>=> error : %s", sheetName, cellAddress, cell,
                        e.getMessage());
                String desc = String.format("regex : %s", fieldMapping.getRuleRegex());
                throw new RuntimeException(msg + desc);
            }
        });
        return record;
    }

    /**
     * 解析一个单元格
     *
     * @param cell   单元格
     * @param tClass 解析类型
     * @return 解析后的对象
     * @throws ParseException
     */
    public static Object resolveCell(Cell cell, Class<?> tClass) throws ParseException {
        //跳过空单元格
        if (cell == null) {
            return null;
        }
        CellType cellTypeEnum = cell.getCellType();
        switch (cellTypeEnum) {
            case NUMERIC:
                double numericCellValue = cell.getNumericCellValue();
                if (tClass.equals(String.class)) {
                    return String.valueOf(numericCellValue);
                } else if (tClass.equals(Integer.class) || tClass.equals(int.class)) {
                    return (int) numericCellValue;
                } else if (tClass.equals(Date.class)) {
                    return cell.getDateCellValue();
                } else if (tClass.equals(BigDecimal.class)) {
                    return BigDecimal.valueOf(numericCellValue);
                } else if (tClass.equals(Double.class) || tClass.equals(double.class)) {
                    return numericCellValue;
                }
                break;
            case STRING:
                String stringCellValue = cell.getStringCellValue();
                if (tClass.equals(String.class)) {
                    return stringCellValue;
                } else if (tClass.equals(Boolean.class) || tClass.equals(boolean.class)) {
                    return Boolean.parseBoolean(stringCellValue);
                } else if (tClass.equals(int.class) || tClass.equals(Integer.class)) {
                    return Integer.parseInt(stringCellValue);
                } else if (tClass.equals(Date.class)) {
                    return DateUtils.parseDate(stringCellValue);
                } else if (tClass.equals(BigDecimal.class)) {
                    return new BigDecimal(stringCellValue);
                }
                break;
            case BOOLEAN:
                boolean booleanCellValue = cell.getBooleanCellValue();
                if (tClass.equals(String.class)) {
                    return Boolean.toString(booleanCellValue);
                } else if (tClass.equals(boolean.class) || tClass.equals(Boolean.class)) {
                    return booleanCellValue;
                }
                break;
            case FORMULA:
                return cell.getRichStringCellValue().getString();
            case ERROR:
                return cell.getErrorCellValue();
            case BLANK:
            case _NONE:
            default:
                return null;
        }
        return null;
    }


    /**
     * excel 的类型, 当前类支持解析的 Excel 文件类型枚举类
     */
    public enum ExcelType {
        XLS,
        XLSX;

        public static ExcelType getByJudgeSuffix(String fileName) {
            if (fileName.endsWith(".xls")) {
                return XLS;
            } else if (fileName.endsWith(".xlsx")) {
                return XLSX;
            } else {
                return null;
            }
        }
    }

}
