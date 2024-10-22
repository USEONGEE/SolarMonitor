package com.energy.outsourcing.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
public class JunctionBox { // 접속반

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "junctionBox", cascade = CascadeType.ALL)
    private List<JunctionBoxChannel> channels;

}
