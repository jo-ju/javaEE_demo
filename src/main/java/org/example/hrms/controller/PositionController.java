package org.example.hrms.controller;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Position;
import org.example.hrms.service.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/position")
public class PositionController {
    @Resource
    private PositionService positionService;

    @GetMapping("/list")
    public List<Position> list() {
        return positionService.findAll();
    }

    @PostMapping("/add")
    public int add(@RequestBody Position position) {
        return positionService.addPosition(position);
    }

    @PutMapping("/update")
    public int update(@RequestBody Position position) {
        return positionService.updatePosition(position);
    }

    @DeleteMapping("/delete/{id}")
    public int delete(@PathVariable Long id) {
        return positionService.deletePosition(id);
    }
}
