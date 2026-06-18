package com.godotvillage.meowkanban.domain.enums;

import lombok.Getter;

@Getter
public enum SortTargetEnum {

    CREATE_TIME("CREATE_TIME", 1, "创建时间"),
    UPDATE_TIME("UPDATE_TIME", 2, "最近活动时间"),
    NAME( "NAME", 3, "名称"),
    RECENT_ACCESS_TIME("RECENT_ACCESS_TIME", 4, "最近访问时间"),
    ;

    private final String target;

    private final Integer code;

    private final String desc;

    SortTargetEnum(String type, Integer code, String desc) {
        this.target = type;
        this.code = code;
        this.desc = desc;
    }

}
