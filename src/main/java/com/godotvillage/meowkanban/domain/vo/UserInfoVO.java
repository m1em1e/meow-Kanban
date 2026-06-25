package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mkdir
 * @since 2026/06/24 09:44
 */
@Data
@NoArgsConstructor
public class UserInfoVO {
	private Long id;
	private String nickname;
	private String BoardRoleCode;
	private Long avatarResourceId;
}
