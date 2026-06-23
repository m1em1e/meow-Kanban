package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/23 11:13
 */
@Data
@NoArgsConstructor
public class TaskSectionVO {
	private Long taskSectionId;
	private String sectionTitle;
	private List<TaskVO> tasks;
}
