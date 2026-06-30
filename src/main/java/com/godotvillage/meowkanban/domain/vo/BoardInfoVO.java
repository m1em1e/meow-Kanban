package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardInfoVO {

    private Long id;

    private String name;

    private String description;

    private Long coverResourceId;

    private Long ownerId;

    private Integer visibility;

    private Boolean favorited;

}
