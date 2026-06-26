package com.godotvillage.meowkanban.domain.enums;

import lombok.Getter;

/**
 *
 * @author mkdir
 * @since 2026/06/26 11:35
 */
@Getter
public enum TaskActivityEnum {

	CREATE("create", "新建任务"),
	UPDATE("update", "修改任务信息"),
	MOVE("move", "移动任务"),
	COMMENT("comment", "评论"),
	ATTACH("attach", ""),
	DELETE("delete", "删除任务"),
	;

	private final String code;

	private final String desc;

	TaskActivityEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}


}
