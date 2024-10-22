package com.energy.outsourcing.collector;

import com.energy.outsourcing.entity.*;
import com.energy.outsourcing.repository.InverterDataRepository;
import com.energy.outsourcing.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("mock")
@RequiredArgsConstructor
public class MockDataCollector implements DataCollector {

    private final InverterDataRepository inverterDataRepository;
    private final JunctionBoxRepository junctionBoxRepository;

    private Random random = new Random();

    @Override
    public void collectAndSaveData() {
        List<InverterData> inverterDataList = new ArrayList<>();
        List<JunctionBox> junctionBoxList = new ArrayList<>();

        // 단상 인버터 데이터 생성
        for (int i = 1; i <= 3; i++) {
            SinglePhaseInverterData data = new SinglePhaseInverterData();
            data.setDeviceId("SingleInverter-" + i);
            data.setTimestamp(LocalDateTime.now());
            data.setPvVoltage(200 + random.nextDouble() * 50);
            data.setPvCurrent(100 + random.nextDouble() * 50);
            data.setPvPower(data.getPvVoltage() * data.getPvCurrent());
            data.setGridVoltage(220.0);
            data.setGridCurrent(150.0);
            data.setCurrentOutput(30000.0);
            data.setPowerFactor(99.9);
            data.setFrequency(60.0);
            data.setCumulativeEnergy(100000.0 + random.nextDouble() * 1000);
            data.setFaultStatus(0);
            inverterDataList.add(data);
        }

        // 삼상 인버터 데이터 생성
        for (int i = 1; i <= 2; i++) {
            ThreePhaseInverterData data = new ThreePhaseInverterData();
            data.setDeviceId("ThreeInverter-" + i);
            data.setTimestamp(LocalDateTime.now());
            data.setPvVoltage(450.0);
            data.setPvCurrent(150.0);
            data.setPvPower(67500.0);
            data.setGridVoltageRS(220.0);
            data.setGridVoltageST(220.0);
            data.setGridVoltageTR(220.0);
            data.setGridCurrentR(102.0);
            data.setGridCurrentS(102.0);
            data.setGridCurrentT(102.0);
            data.setCurrentOutput(67320.0);
            data.setPowerFactor(99.9);
            data.setFrequency(60.0);
            data.setCumulativeEnergy(200000.0 + random.nextDouble() * 1000);
            data.setFaultStatus(0);
            inverterDataList.add(data);
        }

        // 접속함 데이터 생성
        for (int i = 1; i <= 2; i++) {
            JunctionBox junctionBox = new JunctionBox();
            junctionBox.setDeviceId("JunctionBox-" + i);
            junctionBox.setTimestamp(LocalDateTime.now());
            List<JunctionBoxChannel> channels = new ArrayList<>();

            for (int ch = 1; ch <= 12; ch++) {
                JunctionBoxChannel channel = new JunctionBoxChannel();
                channel.setChannelNumber(ch);
                channel.setVoltage(650.0); // 6500 * 0.1[V]
                channel.setCurrent(10.0 + random.nextDouble()); // 1000 * 0.01[A]
                channel.setJunctionBox(junctionBox);
                channels.add(channel);
            }

            junctionBox.setChannels(channels);
            junctionBoxList.add(junctionBox);
        }

        // 데이터 저장
        inverterDataRepository.saveAll(inverterDataList);
        junctionBoxRepository.saveAll(junctionBoxList);
    }
}
