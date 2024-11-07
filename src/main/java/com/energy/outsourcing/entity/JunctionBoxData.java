package com.energy.outsourcing.entity;

import com.energy.outsourcing.dto.JunctionBoxDataRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@Table(indexes = {
        @Index(name = "junction_box_data_idx_timestamp", columnList = "timestamp")
})
@NoArgsConstructor
public class JunctionBoxData {

    @Id
    @GeneratedValue(generator = "increment")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "junction_box_id")
    private JunctionBox junctionBox;
    private double pvVoltage;
    private double pvCurrent;
    private double power;
    private LocalDateTime timestamp;

    // null 객체 반환하는 static method
    public static JunctionBoxData empty() {
        JunctionBoxData junctionBoxData = new JunctionBoxData();
        junctionBoxData.setPvVoltage(0.0);
        junctionBoxData.setPvCurrent(0.0);
        junctionBoxData.setPower(0.0);
        return junctionBoxData;
    }

    public static JunctionBoxData fromDTO(JunctionBox junctionBox, JunctionBoxDataRequestDto dto, LocalDateTime timestamp) {
        JunctionBoxData junctionBoxData = new JunctionBoxData();
        junctionBoxData.setJunctionBox(junctionBox);
        junctionBoxData.setPvVoltage(dto.getPvVoltage());
        junctionBoxData.setPvCurrent(dto.getPvCurrent());
        junctionBoxData.setPower(junctionBoxData.getPvCurrent() * junctionBoxData.getPvVoltage());
        junctionBoxData.setTimestamp(timestamp);
        return junctionBoxData;
    }

}
