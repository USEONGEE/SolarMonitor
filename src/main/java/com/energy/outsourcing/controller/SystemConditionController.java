package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.InverterStatusDto;
import com.energy.outsourcing.dto.SystemConditionResponseDto;
import com.energy.outsourcing.entity.InverterData;
import com.energy.outsourcing.entity.SystemConditionType;
import com.energy.outsourcing.service.InverterDataService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
        List<InverterStatusDto> inverterDataByInverterId = inverterDataService.getInverterDataByInverterId();

        return ResponseEntity.ok(inverterDataByInverterId);
    }
}
