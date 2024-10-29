package com.energy.outsourcing.entity;

import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class JunctionBoxChannelData extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double voltage; // 0.1[V] 단위
    private Double current; // 0.01[A] 단위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "junction_box_channel_id")
    private JunctionBoxChannel junctionBoxChannel;

    // DTO 데이터를 엔티티로 변환하는 메서드
    public static JunctionBoxChannelData fromDTO(JunctionBoxChannelDataDto dto, JunctionBoxChannel channel) {
        JunctionBoxChannelData data = new JunctionBoxChannelData();
        data.setVoltage(dto.getVoltage());
        data.setCurrent(dto.getCurrent());
        data.setJunctionBoxChannel(channel);
        return data;
    }
}
