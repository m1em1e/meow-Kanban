package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

/**
 *
 * @author mkdir
 * @since 2026/06/25 10:53
 */
@Data
public class BoardSectionModifyParam {
	private Long boardId;
	private Long id;
	private String title;
	private Integer sourceSort;
	private Integer targetSort;
}
