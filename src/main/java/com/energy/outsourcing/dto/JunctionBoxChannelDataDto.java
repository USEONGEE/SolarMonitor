package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.JunctionBoxChannel;
import com.energy.outsourcing.entity.JunctionBoxChannelData;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import com.energy.outsourcing.entity.JunctionBoxChannel;
import com.energy.outsourcing.entity.JunctionBoxChannelData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(indexes = {
        @Index(name = "idx_junction_box_channel_id_created_at", columnList = "junction_box_channel_id, created_at DESC")
})
@Schema(description = "접속함 채널 데이터 DTO")
public class JunctionBoxChannelDataDto {

    @Schema(description = "채널 번호", example = "1")
    private Integer channelNumber;

    @Schema(description = "변환된 전압 (0.1[V] 단위)", example = "12.3")
    private Double voltage;

    @Schema(description = "변환된 전류 (0.01[A] 단위)", example = "5.6")
    private Double current;

    /**
     * 원시 데이터를 스케일에 맞게 변환하여 DTO로 설정하는 생성자
     *
     * @param channelNumber 채널 번호
     * @param rawVoltage 원시 전압 데이터
     * @param rawCurrent 원시 전류 데이터
     */
    public JunctionBoxChannelDataDto(Integer channelNumber, int rawVoltage, int rawCurrent) {
        this.channelNumber = channelNumber;
        this.voltage = rawVoltage * 0.1; // 변환 후 실제 전압
        this.current = rawCurrent * 0.01; // 변환 후 실제 전류
    }

    /**
     * DTO 데이터를 엔티티로 변환하는 메서드
     *
     * @param dto DTO 데이터
     * @param channel 접속함 채널 엔티티
     * @return JunctionBoxChannelData 엔티티
     */
    public static JunctionBoxChannelData fromDTO(JunctionBoxChannelDataDto dto, JunctionBoxChannel channel) {
        JunctionBoxChannelData data = new JunctionBoxChannelData();
        data.setVoltage(dto.getVoltage());
        data.setCurrent(dto.getCurrent());
        data.setJunctionBoxChannel(channel);
        return data;
    }
}
