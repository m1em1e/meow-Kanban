package com.godotvillage.meowkanban.controller.restController;

import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.util.LoginUtil;
import com.godotvillage.meowkanban.domain.param.IdParam;
import com.godotvillage.meowkanban.service.IBoardFavoriteService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorite")
public class BoardFavoriteRestController {

    @Resource
    private IBoardFavoriteService boardFavoriteService;

    @PostMapping("/add-favorite")
    public Result addFavorite(@RequestBody IdParam param) {
        boardFavoriteService.addFavorite(param, LoginUtil.getLoginId());
        return Result.success();
    }

    @DeleteMapping("/del-favorite")
    public Result delFavorite(@RequestBody IdParam param) {
        boardFavoriteService.delFavorite(param);
        return Result.success();
    }

}
