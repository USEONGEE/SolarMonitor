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

    @Column(nullable = false)
    private double pvVoltage;
    @Column(nullable = false)
    private double pvCurrent;
    @Column(nullable = false)
    private double power;
    @Column(nullable = false)
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
        junctionBoxData.validate();
        return junctionBoxData;
    }


    private void validate() {
        if (this.pvVoltage < 0) {
            throw new IllegalArgumentException("pvVoltage must be greater than or equal to 0");
        }
        if (this.pvCurrent < 0) {
            throw new IllegalArgumentException("pvCurrent must be greater than or equal to 0");
        }
        if (this.power < 0) {
            throw new IllegalArgumentException("power must be greater than or equal to 0");
        }
        if (this.timestamp == null) {
            throw new IllegalArgumentException("timestamp must not be null");
        }
        if (this.junctionBox == null) {
            throw new IllegalArgumentException("junctionBox must not be null");
        }
    }
}
