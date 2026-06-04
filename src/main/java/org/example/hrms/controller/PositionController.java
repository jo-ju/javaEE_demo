package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.common.Result;
import org.example.hrms.config.RequireManage;
import org.example.hrms.entity.Position;
import org.example.hrms.service.PositionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/position")
public class PositionController {
    @Resource
    private PositionService positionService;

    @GetMapping("/list")
    public Result list() {
        return Result.ok(positionService.findAll());
    }

    @RequireManage
    @PostMapping("/add")
    public Result add(@RequestBody Position position) {
        positionService.addPosition(position);
        return Result.ok("新增成功", null);
    }

    @RequireManage
    @PutMapping("/update")
    public Result update(@RequestBody Position position) {
        positionService.updatePosition(position);
        return Result.ok("修改成功", null);
    }

    @RequireManage
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        positionService.deletePosition(id);
        return Result.ok("删除成功", null);
    }
}
