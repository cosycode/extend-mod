package com.github.cosycode.ext.fileimport.excel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <b>Description : </b> 解析后的Excel句柄
 * <p>
 * <b>created in </b> 2019/8/27
 * </p>
 *
 * @author CPF
 **/
@Slf4j
public class ParsedSheetHandler<T> {

    /**
     * 存放解析后的excel信息
     */
    @Getter
    private SheetInfo sheetInfo;

    /**
     * 存放数据信息
     */
    @Getter
    private List<T> dataList;

    @Getter
    private DbOperateHandle<T> dbOperateHandle;

    public ParsedSheetHandler(SheetInfo sheetInfo, List<T> dataList, DbOperateHandle<T> dbOperateHandle) {
        this.sheetInfo = sheetInfo;
        this.dataList = dataList;
        this.dbOperateHandle = dbOperateHandle;
    }

    /**
     * 批量导入方法
     */
    public final int operateDb() {
        return dbOperateHandle.operateDb(dataList);
    }

    @FunctionalInterface
    public interface DbOperateHandle<T> {
        int operateDb(List<T> dataList);
    }

//    private DbOperateHandle<T> dbOperateHandle;
//
//    public ParsedSheetHandler(SheetInfo sheetInfo, List<T> dataList, DbOperateHandle<T> dbOperateHandle) {
//        this.sheetInfo = sheetInfo;
//        this.dataList = dataList;
//        this.dbOperateHandle = dbOperateHandle;
//    }
//
//    ParsedSheetHandler(SheetInfo sheetInfo, List<T> dataList) {
//        this.sheetInfo = sheetInfo;
//        this.dataList = dataList;
//    }
//
//    /**
//     * 批量导入方法
//     */
//    public int operateDb(@NonNull SqlSessionFactory sqlSessionFactory) {
//        return dbOperateHandle.operateDb(sqlSessionFactory, dataList);
//    }
//
//    public static Map<String, Integer> transportToDb(@NonNull SqlSessionFactory sqlSessionFactory, List<ParsedSheetHandler> handlers){
//        // 导入
//        Map<String, Integer> map = new HashMap<>();
//        log.info("excel import start!");
//        handlers.forEach(it -> {
//            try {
//                log.info("sheetInfo -> " + it.getSheetInfo());
//                int success = it.operateDb(sqlSessionFactory);
//                map.put(it.getSheetInfo().getSheetName(), success);
//            } catch (RuntimeException e) {
//                log.error("", e);
//                Throwable cause = e.getCause();
//                while (cause.getCause() != null) {
//                    cause = cause.getCause();
//                }
//                String msg = String.format("导入数据库时发生错误<br> =>sheet : %s <br> =>error : %s", it.getSheetInfo().getSheetName(), cause.getMessage());
//                throw new PostMessageException(msg, Arrays.toString(e.getStackTrace()));
//            }
//        });
//        log.info("excel import end!");
//        return map;
//    }

}
