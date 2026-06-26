package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author mkdir
 * @since 2026/06/26 16:46
 */
@Data
@NoArgsConstructor
public class CommentVO {
	private Long commentId;
	private String content;
	private Long userId;
	private String nickname;
	private Long avatarResourceId;
	private LocalDateTime commentTime;
}
