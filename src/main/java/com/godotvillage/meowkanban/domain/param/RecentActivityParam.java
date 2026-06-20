package com.godotvillage.meowkanban.domain.param;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecentActivityParam {

    private Long id;

    private Integer pageIndex;

    private Integer pageSize;

}
