package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardInfo {

    private Long id;

    private String name;

    private String description;

    private Long coverResourceId;

    private Long ownerId;

    private Integer visibility;

}
