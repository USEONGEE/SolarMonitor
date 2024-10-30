package com.energy.outsourcing.controller;

import com.energy.outsourcing.service.JunctionBoxDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/junctionBoxData")
public class JunctionBoxDataController {
    private final JunctionBoxDataService junctionBoxDataService;
}
