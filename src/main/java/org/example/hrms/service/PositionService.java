package org.example.hrms.service;

import jakarta.annotation.Resource;
import org.example.hrms.entity.Position;
import org.example.hrms.mapper.PositionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {
    @Resource
    private PositionMapper positionMapper;

    public List<Position> findAll() {
        return positionMapper.findAll();
    }

    public int addPosition(Position position) {
        return positionMapper.addPosition(position);
    }

    public int updatePosition(Position position) {
        return positionMapper.updatePosition(position);
    }

    public int deletePosition(Long id) {
        return positionMapper.deletePosition(id);
    }
}