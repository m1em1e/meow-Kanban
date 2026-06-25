package com.godotvillage.meowkanban.domain.enums;

import lombok.Getter;

/**
 *
 * @author mkdir
 * @since 2026/06/24 10:15
 */
@Getter
public enum BoardRoleEnum {

	OWNER("OWNER", "看板拥有者"),
	ADMIN("ADMIN", "看板管理员"),
	MEMBER("MEMBER", "看板成员"),
	VIEWER("VIEWER", "看板观看者"),
	;

	private final String code;

	private final String desc;

	BoardRoleEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
}
