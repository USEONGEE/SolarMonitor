package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.SolarEffectDTO;
import com.energy.outsourcing.service.SolarEffectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/solar-effect")
public class SolarEffectController {
    private final SolarEffectService solarEffectService;

    @GetMapping
    public ResponseEntity<SolarEffectDTO> getSolarEffects() {
        SolarEffectDTO solarEffectDTO = solarEffectService.calculateEffects();
        return ResponseEntity.ok(solarEffectDTO);
    }
}
