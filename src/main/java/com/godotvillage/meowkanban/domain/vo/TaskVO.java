package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/23 11:13
 */
@Data
@NoArgsConstructor
public class TaskVO {
	private Long taskId;
	private String taskNo;
	private String title;
	private String description;
	private LocalDate dueDate;
	private Integer priority;
	private Integer blocked;
	private List<String> tags;
	private List<Long> referUserIds;
	private Integer sort;
	private Long createrId;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}
