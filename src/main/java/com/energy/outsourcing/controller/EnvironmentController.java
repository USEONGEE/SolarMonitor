package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.EnvironmentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/environment")
public class EnvironmentController {

    @GetMapping("/realtime")
    public ResponseEntity<EnvironmentResponseDto> getEnvironment() {
        // TODO: Implement this method
        return ResponseEntity.ok(EnvironmentResponseDto.of(10.1f,11.2f,700.2f,710.8f));
    }
}
