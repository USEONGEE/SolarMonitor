package com.example.web.dto;

import com.example.web.entity.InverterType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InverterDto {
    private Long id;
    private InverterType inverterType;
}
