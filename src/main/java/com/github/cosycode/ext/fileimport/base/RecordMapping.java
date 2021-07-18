package com.github.cosycode.ext.fileimport.base;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2019/8/15
 * </p>
 *
 * @author CPF
 **/
public class RecordMapping {

    /**
     * key: 字段在excel中的名字, value: 字段映射的对象
     */
    private Map<String, FieldMapping<?>> headerFieldMappingMap;
    /**
     * 存放规则(key: 字段所在excel中的列数, value: 字段在excel中的名字)
     */
    private Map<Integer, String> positionMapping;

    public void addFieldMapping(@NonNull String headerName, @NonNull FieldMapping<?> fieldMapping) {
        if (headerFieldMappingMap == null) {
            headerFieldMappingMap = new HashMap<>();
        }
        headerFieldMappingMap.put(headerName, fieldMapping);
    }

    public FieldMapping<?> getFieldMappingByHeadName(@NonNull String headName) {
        return headerFieldMappingMap.get(headName);
    }

    public FieldMapping<?> getFieldMappingByCellNum(int cellNum) {
        String headName = positionMapping.get(cellNum);
        return headerFieldMappingMap.get(headName);
    }

    public void putPositionMapping(int cellNum, String headName) {
        if (positionMapping == null) {
            positionMapping = new HashMap<>();
        }
        positionMapping.put(cellNum, headName);
    }

    /**
     * 获取在 headerFieldMappingMap 中注册的必要的, 但是却没有在sheet中发现的字段列,
     */
    public List<String> getNotFoundHeaderName() {
        return headerFieldMappingMap.entrySet().stream()
                // 过滤出必要字段, 但是却没有在sheet表中发现的集合
                .filter(entry -> entry.getValue().isRequire() && !positionMapping.containsValue(entry.getKey()))
                // 转换为 headerName
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

}
