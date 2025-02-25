package com.example.web.controller;

import com.example.web.dto.InverterDto;
import com.example.web.entity.Inverter;
import com.example.web.service.InverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inverter")
public class InverterController {

    private final InverterService inverterService;

    // 인버터 데이터 조회
     @GetMapping
     public ResponseEntity<List<InverterDto>> getInverterData() {
         List<Inverter> inverterData = inverterService.getInverterData();
         List<InverterDto> collect = inverterData.stream()
                 .map(inverter -> new InverterDto(inverter.getId(), inverter.getInverterType())
                 ).collect(Collectors.toList());

         return ResponseEntity.ok(collect);
     }
}
