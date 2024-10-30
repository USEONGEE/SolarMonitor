package com.energy.outsourcing.schedular;

import com.energy.outsourcing.dto.SinglePhaseInverterDto;
import com.energy.outsourcing.dto.ThreePhaseInverterDto;
import com.energy.outsourcing.dto.JunctionBoxChannelDataDto;
import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.InverterType;
import com.energy.outsourcing.entity.JunctionBox;
import com.energy.outsourcing.entity.JunctionBoxData;
import com.energy.outsourcing.repository.InverterRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import com.energy.outsourcing.service.JunctionBoxDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledDataService {
    private final InverterRepository inverterRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final DataRequester dataRequester;
    private final DataProcessor dataProcessor;

    // 매분마다 데이터를 요청하고 저장하는 스케줄러 메서드
    @Scheduled(fixedRate = 10000) // 10초마다 실행
    @Transactional
    public void fetchDataAndProcess() {
        LocalDateTime timestamp = LocalDateTime.now();

        // 모든 인버터를 조회하고 단상, 삼상 데이터 요청
        for (Inverter inverter : inverterRepository.findAll()) {
            if (inverter.getInverterType() == InverterType.SINGLE) {
                SinglePhaseInverterDto singlePhaseData = dataRequester.requestSinglePhaseData(inverter.getId());
                dataProcessor.processSinglePhaseData(singlePhaseData, timestamp);
            } else if (inverter.getInverterType() == InverterType.THREE) {
                ThreePhaseInverterDto threePhaseData = dataRequester.requestThreePhaseData(inverter.getId());
                dataProcessor.processThreePhaseData(threePhaseData, timestamp);
            }
        }

        // 모든 접속함을 조회하고 접속함별 데이터 요청
        for (JunctionBox junctionBox : junctionBoxRepository.findAll()) {
            Long junctionBoxId = junctionBox.getId();
            List<JunctionBoxChannelDataDto> junctionBoxDataList = dataRequester.requestJunctionBoxData(junctionBoxId);


            for (JunctionBoxChannelDataDto channelData : junctionBoxDataList) {
                dataProcessor.processJunctionBoxChannelData(junctionBoxId, channelData, timestamp);
            }

            dataProcessor.processJunctionBoxData(junctionBoxId, junctionBoxDataList, timestamp);
        }
    }
}
