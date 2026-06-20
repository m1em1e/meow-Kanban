package com.godotvillage.meowkanban.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecentActivityVO {

    private Long id;

    private Long boardId;

    private String boardTitle;

    private String action;

    private LocalDateTime createTime;

}
