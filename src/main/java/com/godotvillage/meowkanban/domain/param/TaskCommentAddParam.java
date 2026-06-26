package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:38
 */
@Data
public class TaskCommentAddParam {
	private Long taskId;
	private String content;
}
