package com.godotvillage.meowkanban.domain.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    ADMIN("ROLE_ADMIN", "拥有系统管理权限和业务操作权限"),
    USER("ROLE_USER", "拥有基础业务操作权限"),
    VIEWER("ROLE_VIEWER", "仅拥有基础查看权限"),
    ;

    private final String code;
    private final String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
