package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

import java.time.LocalDate;

/**
 *
 * @author mkdir
 * @since 2026/06/25 16:55
 */
@Data
public class TaskCardModifyParam {
	private Long id;
	private Long sectionId;
	private String title;
	private String description;
	private LocalDate dueDate;
	private Integer priority;
	private Integer blocked;
	private Integer sort;
}
