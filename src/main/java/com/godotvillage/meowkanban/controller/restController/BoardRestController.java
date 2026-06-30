package com.godotvillage.meowkanban.controller.restController;


import com.godotvillage.meowkanban.common.result.Result;
import com.godotvillage.meowkanban.common.result.PageResult;
import com.godotvillage.meowkanban.domain.param.*;
import com.godotvillage.meowkanban.domain.vo.BoardDetailVO;
import com.godotvillage.meowkanban.domain.vo.BoardInfoVO;
import com.godotvillage.meowkanban.service.IBoardSectionService;
import com.godotvillage.meowkanban.service.IBoardService;
import com.godotvillage.meowkanban.service.ITaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/board")
public class BoardRestController {

    @Resource
    private IBoardService boardService;

	@Resource
	private ITaskService taskService;

	@Resource
	private IBoardSectionService boardSectionService;

    @GetMapping("/list")
    public Result<PageResult<BoardInfoVO>> list(BoardInfoQueryParam param) {
        return Result.success(boardService.listBoardInfo(param));
    }

    @GetMapping("/recent")
    public Result<List<BoardInfoVO>> recent(IdParam param) {
        return Result.success(boardService.listRecentParticipatedBoards(param));
    }

    @PostMapping("/new")
    public Result newBoard(@RequestBody NewBoardParam param) {
        boardService.newBoard(param);
        return Result.success();
    }

	@GetMapping("/detail")
	public Result<BoardDetailVO> getBoardDetail(IdParam param) {
		return Result.success(boardService.getBoardDetail(param));
	}

	@PostMapping("/add-section-card")
	public Result addSectionCard(@RequestBody BoardSectionAddParam param) {
		boardSectionService.addSectionCard(param);
		return Result.success();
	}

	@PutMapping("/modify-section-card")
	public Result modifySectionCard(@RequestBody BoardSectionModifyParam param) {
		boardSectionService.modifySectionSort(param);
		return Result.success();
	}

	@PutMapping("/rename-section-card")
	public Result renameSectionCard(@RequestBody BoardSectionModifyParam param) {
		boardSectionService.renameSectionCard(param);
		return Result.success();
	}

	@DeleteMapping("/del-section-card")
	public Result deleteSectionCard(@RequestBody IdParam param) {
		boardSectionService.deleteById(param.getId());
		return Result.success();
	}

}
