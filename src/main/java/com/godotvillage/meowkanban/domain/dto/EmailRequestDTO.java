package com.godotvillage.meowkanban.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author mkdir
 * @since 2026/06/22 10:38
 */
@Data
@NoArgsConstructor
public class EmailRequestDTO implements Serializable {

	/**
	 * 收件人邮箱地址
	 * 例如：user@example.com
	 */
	private String to;
	/**
	 * 邮件类型（对应 EmailTypeEnum）
	 * 例如：SUBMISSION_SUCCESS
	 */
	private String emailType;
	/**
	 * 企业ID（用于查询业务数据）
	 * 例如：BIZ123456
	 */
	private String businessId;
	/**
	 * 模板变量（动态内容）
	 * 例如：{"companyName": "ABC Corp", "submissionTime": "2025-10-31 10:00:00"}
	 */
	private Map<String, String> templateVariables;
	/**
	 * 附加信息（如审核备注）
	 * 例如："缺少营业执照扫描件"
	 */
	private String additionalInfo;

}
