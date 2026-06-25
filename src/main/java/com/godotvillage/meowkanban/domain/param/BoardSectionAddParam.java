package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:42
 */
@Data
public class BoardSectionAddParam {
	private Long boardId;
	private String code;
	private String boardName;
	private Integer sort;
}
