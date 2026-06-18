package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

@Data
public class BoardInfoQueryParam {
    private String keyword;
    private Integer sortTarget;
    private Integer sortType;
    private Integer pageIndex;
    private Integer pageSize;
    private Long userId;
}
