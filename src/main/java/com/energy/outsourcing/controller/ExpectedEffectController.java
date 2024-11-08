package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.ExpectedEffectResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/expected-effect")
public class ExpectedEffectController {

    @GetMapping
    public ResponseEntity<ExpectedEffectResponseDto> getExpectedEffect() {
        return ResponseEntity.ok(new ExpectedEffectResponseDto(1L, 2L, 3.0, 4.0, 5.0, 6.0));
    }
}
