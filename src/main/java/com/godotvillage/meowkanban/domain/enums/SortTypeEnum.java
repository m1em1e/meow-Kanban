package com.godotvillage.meowkanban.domain.enums;

public enum SortTypeEnum {

    ASC("升序", 1, "按指定字段升序排序"),
    DESC("降序", 0, "按指定字段降序排序");

    private final String name;

    private final Integer code;

    private final String desc;

    SortTypeEnum(String name, Integer code, String desc) {
        this.name = name;
        this.code = code;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
