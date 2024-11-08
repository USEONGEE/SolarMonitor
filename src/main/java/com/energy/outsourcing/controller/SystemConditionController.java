package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.SystemConditionResponseDto;
import com.energy.outsourcing.entity.SystemConditionType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system-condition")
public class SystemConditionController {

    @GetMapping("/realtime")
    // TODO: Implement this method
    public ResponseEntity<SystemConditionResponseDto> getSystemCondition() {
        return ResponseEntity.ok(SystemConditionResponseDto.of(SystemConditionType.NORMAL,
                SystemConditionType.NORMAL,
                SystemConditionType.NORMAL));
    }
}
