package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author mkdir
 * @since 2026/06/23 12:32
 */
@Data
@NoArgsConstructor
public class BoardDetailVO {
	private Long boardId;
	private String boardTitle;
	private Long coverId;
	private String desc;
	private List<Long> memberIds;
	private List<TaskSectionVO> sectionVOS;
}
