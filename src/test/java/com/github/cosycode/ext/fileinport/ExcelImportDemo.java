package com.github.cosycode.ext.fileinport;

import java.lang.reflect.Method;

/**
 * <b>Description : </b>
 *
 * @author CPF
 * Date: 2020/7/29 16:05
 */
public class ExcelImportDemo {

    public static void main(String[] args) {
        final Method[] declaredMethods = Cindf.class.getDeclaredMethods();
        System.out.println(declaredMethods);
    }

    public static class Cindf {
        public static void testStatic() {

        }

        public void testCommon() {

        }
    }

    public class Cindf2 {
        public void testCommon() {

        }
    }


//    public String importExcel(@NonNull MultipartFile file) throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
//        // 检查文件参数
//        String originalFilename = file.getOriginalFilename();
//        if (originalFilename == null) {
//            throw new PostException(ECommonPostCode.ILLEGAL_ARGUMENT_EXCEPTION);
//        }
//        ExcelResolver.ExcelType excelType = ExcelResolver.ExcelType.getByJudgeSuffix(originalFilename);
//        if (excelType == null) {
//            throw new PostException(ECommonPostCode.ILLEGAL_ARGUMENT_EXCEPTION);
//        }
//        // 初始化
//        AbstractSheetBeanMappingAdapter adapter = new QqOrganizationExcelImportAdapter(iQqGroup, this, iQqGroupMember);
//        // 解析Excel
//        List<ParsedSheetHandler> handlers = ExcelResolver.resolveQuoteExcel(file.getInputStream(), excelType, Collections.singletonList(adapter));
//
//        // 导入
//        Map<String, Integer> map = ParsedSheetHandler.transportToDb(sqlSessionFactory, handlers);
//
//        return map.entrySet().stream().map(entry -> String.format("<br> =>sheet : %s --> 导入了 %s 条数据", entry.getKey(), entry.getValue())).collect(Collectors.joining(""));
//    }
//
}
