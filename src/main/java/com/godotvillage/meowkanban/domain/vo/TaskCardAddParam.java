package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 *
 * @author mkdir
 * @since 2026/06/25 16:26
 */
@Data
public class TaskCardAddParam {
	private Long boardId;
	private Long sectionId;
	private String SectionCode;
	private String title;
	private String description;
	private LocalDate dueDate;
	private Integer priority;
	private Integer blocked;
	private Integer sort;
}
