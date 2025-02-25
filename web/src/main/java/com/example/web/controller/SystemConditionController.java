package com.example.web.controller;

import com.example.web.dto.InverterStatusDto;
import com.example.web.service.InverterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system-condition")
@RequiredArgsConstructor
public class SystemConditionController {

    private final InverterDataService inverterDataService;

    @GetMapping("/realtime")
    // TODO: Implement this method
    public ResponseEntity<List<InverterStatusDto>> getSystemCondition() {
        List<InverterStatusDto> inverterDataByInverterId = inverterDataService.getInverterStatus();

        return ResponseEntity.ok(inverterDataByInverterId);
    }
}
