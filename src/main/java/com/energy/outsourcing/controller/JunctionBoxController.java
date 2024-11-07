package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.JunctionBoxResponseDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.service.JunctionBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/junctionBox")
public class JunctionBoxController {

    private final JunctionBoxService junctionBoxService;

    /**
     * 모든 접속반 조회
     */
    @GetMapping
    public ResponseEntity<List<JunctionBoxResponseDto>> getJunctionBox() {
        List<JunctionBox> allWithInverter = junctionBoxService.findAllWithInverter();
        List<JunctionBoxResponseDto> list = allWithInverter.stream()
                .map(data -> new JunctionBoxResponseDto(data.getId(), data.getInverter().getId()))
                .toList();
        return ResponseEntity.ok(list);
    }

}

