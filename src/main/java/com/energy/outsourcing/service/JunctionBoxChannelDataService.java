package com.energy.outsourcing.service;

import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.JunctionBoxChannel;
import com.energy.outsourcing.entity.JunctionBoxChannelData;
import com.energy.outsourcing.repository.JunctionBoxChannelDataRepository;
import com.energy.outsourcing.repository.JunctionBoxChannelRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JunctionBoxChannelDataService {

    private final JunctionBoxRepository junctionBoxRepository;

    private final JunctionBoxChannelRepository junctionBoxChannelRepository;

    private final JunctionBoxChannelDataRepository junctionBoxChannelDataRepository;

    // DTO를 받아 데이터를 저장하는 메서드
    public JunctionBoxChannelData saveChannelData(Long junctionBoxId, JunctionBoxChannelDataDto dto, LocalDateTime localDateTime) {

        // junctionBoxId에 해당하는 JunctionBox를 찾아서 없으면 예외를 발생시킨다.
        JunctionBox junctionBox = junctionBoxRepository.findById(junctionBoxId)
                .orElseThrow(() -> new RuntimeException("JunctionBox not found"));

        // junctionBox에 해당하는 channel을 찾아서 없으면 새로 생성한다.
        Optional<JunctionBoxChannel> channelOpt = junctionBox.getChannels().stream()
                .filter(channel -> channel.getChannelNumber().equals(dto.getChannelNumber()))
                .findFirst();
        // channel이 존재하지 않으면 새로 생성한다.
        JunctionBoxChannel channel = channelOpt.orElseGet(() -> {
            JunctionBoxChannel newChannel = new JunctionBoxChannel();
            newChannel.setChannelNumber(dto.getChannelNumber());
            newChannel.setJunctionBox(junctionBox);
            return junctionBoxChannelRepository.save(newChannel);
        });

        JunctionBoxChannelData data = JunctionBoxChannelData.fromDTO(dto, channel, localDateTime);
        return junctionBoxChannelDataRepository.save(data);
    }
}