package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.JunctionBoxChannel;
import com.energy.outsourcing.entity.JunctionBoxChannelData;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(indexes = {
        @Index(name = "idx_junction_box_channel_id_created_at", columnList = "junction_box_channel_id, created_at DESC")
})
public class JunctionBoxChannelDataDto {

    private Integer channelNumber;
    private Double voltage; // 0.1[V] 단위 변환된 값
    private Double current; // 0.01[A] 단위 변환된 값

    // 원시 데이터를 스케일에 맞게 변환하여 DTO로 설정
    public JunctionBoxChannelDataDto(Integer channelNumber, int rawVoltage, int rawCurrent) {
        this.channelNumber = channelNumber;
        this.voltage = rawVoltage * 0.1; // 변환 후 실제 전압
        this.current = rawCurrent * 0.01; // 변환 후 실제 전류
    }

    // DTO 데이터를 엔티티로 변환하는 메서드
    public static JunctionBoxChannelData fromDTO(JunctionBoxChannelDataDto dto, JunctionBoxChannel channel) {
        JunctionBoxChannelData data = new JunctionBoxChannelData();
        data.setVoltage(dto.getVoltage());
        data.setCurrent(dto.getCurrent());
        data.setJunctionBoxChannel(channel);
        return data;
    }
}