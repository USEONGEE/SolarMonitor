package com.energy.outsourcing.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InverterStatusDto {
    private Long inverterId;
    private Integer status;
}
