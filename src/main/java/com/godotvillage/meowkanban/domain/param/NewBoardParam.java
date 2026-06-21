package com.godotvillage.meowkanban.domain.param;

import lombok.Data;

@Data
public class NewBoardParam {
    private String name;

    private String description;

    private Long userId;

    private Long coverResourceId;

    private Integer visibility;
}
