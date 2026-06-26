package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

/**
 *
 * @author mkdir
 * @since 2026/06/26 11:26
 */
@Data
public class TaskActivityAddParam {
	private Long taskId;
	private String actionCode;
	private Long loginId;
}
