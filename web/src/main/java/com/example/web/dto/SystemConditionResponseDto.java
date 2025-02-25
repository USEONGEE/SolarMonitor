package com.example.web.dto;

import com.example.web.entity.SystemConditionType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemConditionResponseDto {

    public SystemConditionType fire; // 화재
    public SystemConditionType malfunction; // 고장
    public SystemConditionType communication; // 통신

    public static SystemConditionResponseDto of(SystemConditionType fire, SystemConditionType malfunction, SystemConditionType communication) {
        SystemConditionResponseDto dto = new SystemConditionResponseDto();
        dto.fire = fire;
        dto.malfunction = malfunction;
        dto.communication = communication;
        return dto;
    }
}
