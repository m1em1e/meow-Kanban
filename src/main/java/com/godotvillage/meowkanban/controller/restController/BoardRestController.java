package com.godotvillage.meowkanban.controller.restController;


import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.param.BoardInfoQueryParam;
import com.godotvillage.meowkanban.domain.vo.BoardInfo;
import com.godotvillage.meowkanban.service.IBoardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/board")
public class BoardRestController {

    @Resource
    private IBoardService boardService;

    @GetMapping("/list")
    public Result<PageResult<BoardInfo>> list(BoardInfoQueryParam param) {
        return Result.success(boardService.listBoardInfo(param));
    }

}
