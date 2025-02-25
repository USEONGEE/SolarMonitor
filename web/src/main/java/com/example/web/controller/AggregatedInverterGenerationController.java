package com.example.web.controller;

import com.example.web.dto.AggregatedInverterGenerationDto;
import com.example.web.service.AggregatedInverterGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inverters/aggregated")
@RequiredArgsConstructor
public class AggregatedInverterGenerationController {

    private final AggregatedInverterGenerationService aggregatedService;



    @GetMapping("/generation")
    public ResponseEntity<AggregatedInverterGenerationDto> getAggregatedGeneration() {
        return ResponseEntity.ok(aggregatedService.getAggregatedGeneration());
    }
}
