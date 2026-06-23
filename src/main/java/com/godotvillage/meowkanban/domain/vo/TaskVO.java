package com.godotvillage.meowkanban.domain.vo;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/23 11:13
 */
public class TaskVO {
	private Long taskId;
	private String taskNo;
	private String title;
	private String description;
	private Long createrId;
	private LocalDateTime createTime;
	private List<Long> tagIds;
	private List<Long> referUserIds;
	private Long currentUserId;
	private Integer sort;
}
