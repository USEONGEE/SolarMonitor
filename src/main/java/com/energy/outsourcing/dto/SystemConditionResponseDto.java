package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.BaseTimeEntity;

public class SystemConditionResponseDto {

    public BaseTimeEntity.SystemConditionType malfunction; // 고장
    public BaseTimeEntity.SystemConditionType fire; // 화재
    public BaseTimeEntity.SystemConditionType communication; // 통신

}
