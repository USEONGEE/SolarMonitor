package com.example.fetcher.schedular;

import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.*;
import com.example.web.repository.InverterDataRepository;
import com.example.web.repository.InverterRepository;
import com.example.web.repository.JunctionBoxDataRepository;
import com.example.web.repository.JunctionBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Service
@RequiredArgsConstructor
public class DataRequesterImpl implements DataRequester {

    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final JunctionBoxDataRepository junctionBoxDataRepository;
    private final ModbusClient modbusClient; // MODBUS 요청 (접속함)
    private final RemsClient remsClient; // REMS 요청 (인버터)

    @Override
    @Transactional
    public SinglePhaseInverterDto requestSinglePhaseData(Long inverterId) {
        Inverter inverter = inverterRepository.findById(inverterId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 단상 인버터가 존재하지 않습니다."));

        byte[] response = remsClient.requestSinglePhase(inverter.getId()); // REMS 요청
        SinglePhaseInverterDto dto = parseSinglePhaseResponse(response);

        // 데이터를 DB에 저장
        SinglePhaseInverterData data = SinglePhaseInverterData.fromDTO(dto);
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);

        return dto;
    }

    @Override
    @Transactional
    public ThreePhaseInverterDto requestThreePhaseData(Long inverterId) {
        Inverter inverter = inverterRepository.findById(inverterId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 삼상 인버터가 존재하지 않습니다."));

        byte[] response = remsClient.requestThreePhase(inverter.getId()); // REMS 요청
        ThreePhaseInverterDto dto = parseThreePhaseResponse(response);

        // 데이터를 DB에 저장
        ThreePhaseInverterData data = ThreePhaseInverterData.fromDTO(dto, LocalDateTime.now());
        data.setInverter(inverter);
        data.setTimestamp(LocalDateTime.now());
        inverterDataRepository.save(data);

        return dto;
    }

    @Override
    @Transactional
    public JunctionBoxDataRequestDto requestJunctionBoxData(Long inverterId) {
        // 1️⃣ 인버터 ID로 접속함 조회
        List<JunctionBox> junctionBoxes = junctionBoxRepository.findByInverterId(inverterId);

        if (junctionBoxes.isEmpty()) {
            throw new RuntimeException("해당 ID의 접속함이 존재하지 않습니다.");
        }

        // 2️⃣ MODBUS 요청
        byte[] response = modbusClient.requestJunctionBox(inverterId, junctionBoxes.size());

        // 3️⃣ 응답 데이터 파싱
        List<JunctionBoxDataRequestDto> dataList = parseJunctionBoxResponse(response);

        // 4️⃣ 파싱된 데이터를 데이터베이스에 저장
        LocalDateTime timestamp = LocalDateTime.now();
        List<JunctionBoxData> savedDataList = new ArrayList<>();

        for (int i = 0; i < junctionBoxes.size(); i++) {
            JunctionBox junctionBox = junctionBoxes.get(i);
            JunctionBoxDataRequestDto dto = dataList.get(i);

            JunctionBoxData junctionBoxData = JunctionBoxData.fromDTO(junctionBox, dto, timestamp);
            savedDataList.add(junctionBoxData);
        }

        junctionBoxDataRepository.saveAll(savedDataList);

        return null; // 반환값 없음.
    }


    @Override
    public SeasonalPanelDataDto requestSeasonal() {
        return null;
    }

    private SinglePhaseInverterDto parseSinglePhaseResponse(byte[] response) {
        if (response.length < 26) {
            throw new IllegalArgumentException("Invalid response length for single-phase inverter");
        }

        int index = 4; // 데이터 시작 위치
        int pvVoltage = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
        int pvCurrent = ((response[index + 2] & 0xFF) << 8) | (response[index + 3] & 0xFF);
        int pvPower = ((response[index + 4] & 0xFF) << 8) | (response[index + 5] & 0xFF);
        int gridVoltage = ((response[index + 6] & 0xFF) << 8) | (response[index + 7] & 0xFF);
        int gridCurrent = ((response[index + 8] & 0xFF) << 8) | (response[index + 9] & 0xFF);
        int currentOutput = ((response[index + 10] & 0xFF) << 8) | (response[index + 11] & 0xFF);
        double powerFactor = (((response[index + 12] & 0xFF) << 8) | (response[index + 13] & 0xFF)) / 10.0;
        double frequency = (((response[index + 14] & 0xFF) << 8) | (response[index + 15] & 0xFF)) / 10.0;
        long cumulativeEnergy = ((response[index + 16] & 0xFFL) << 24) | ((response[index + 17] & 0xFFL) << 16) |
                ((response[index + 18] & 0xFFL) << 8) | (response[index + 19] & 0xFFL);
        int faultStatus = ((response[index + 20] & 0xFF) << 8) | (response[index + 21] & 0xFF);

        return new SinglePhaseInverterDto(
                (double) pvVoltage, (double) pvCurrent, (double) pvPower, (double) gridVoltage,
                (double) gridCurrent, (double) currentOutput, powerFactor, frequency, (double) cumulativeEnergy, faultStatus
        );
    }

    private ThreePhaseInverterDto parseThreePhaseResponse(byte[] response) {
        if (response.length < 38) {
            throw new IllegalArgumentException("Invalid response length for three-phase inverter");
        }

        int index = 4; // 데이터 시작 위치 (SOP, ID, Command, Data Length 제외)

        // 1. 기본 전력 데이터
        int pvVoltage = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
        int pvCurrent = ((response[index + 2] & 0xFF) << 8) | (response[index + 3] & 0xFF);
        int pvPower = ((response[index + 4] & 0xFF) << 24) | ((response[index + 5] & 0xFF) << 16) |
                ((response[index + 6] & 0xFF) << 8) | (response[index + 7] & 0xFF);

        // 2. 삼상 전압 데이터 (RS, ST, TR 선간 전압)
        int gridVoltageRS = ((response[index + 8] & 0xFF) << 8) | (response[index + 9] & 0xFF);
        int gridVoltageST = ((response[index + 10] & 0xFF) << 8) | (response[index + 11] & 0xFF);
        int gridVoltageTR = ((response[index + 12] & 0xFF) << 8) | (response[index + 13] & 0xFF);

        // 3. 삼상 전류 데이터 (R, S, T 상 전류)
        int gridCurrentR = ((response[index + 14] & 0xFF) << 8) | (response[index + 15] & 0xFF);
        int gridCurrentS = ((response[index + 16] & 0xFF) << 8) | (response[index + 17] & 0xFF);
        int gridCurrentT = ((response[index + 18] & 0xFF) << 8) | (response[index + 19] & 0xFF);

        // 4. 현재 출력 (4Byte)
        int currentOutput = ((response[index + 20] & 0xFF) << 24) | ((response[index + 21] & 0xFF) << 16) |
                ((response[index + 22] & 0xFF) << 8) | (response[index + 23] & 0xFF);

        // 5. 역률 (0.1% 단위 변환 필요)
        double powerFactor = (((response[index + 24] & 0xFF) << 8) | (response[index + 25] & 0xFF)) / 10.0;

        // 6. 주파수 (0.1Hz 단위 변환 필요)
        double frequency = (((response[index + 26] & 0xFF) << 8) | (response[index + 27] & 0xFF)) / 10.0;

        // 7. 누적 발전량 (8Byte)
        long cumulativeEnergy = ((response[index + 28] & 0xFFL) << 56) | ((response[index + 29] & 0xFFL) << 48) |
                ((response[index + 30] & 0xFFL) << 40) | ((response[index + 31] & 0xFFL) << 32) |
                ((response[index + 32] & 0xFFL) << 24) | ((response[index + 33] & 0xFFL) << 16) |
                ((response[index + 34] & 0xFFL) << 8) | (response[index + 35] & 0xFFL);

        // 8. 고장 상태 (2Byte)
        int faultStatus = ((response[index + 36] & 0xFF) << 8) | (response[index + 37] & 0xFF);

        return new ThreePhaseInverterDto(
                (double) pvVoltage, (double) pvCurrent, (double) pvPower, (double) gridVoltageRS,
                (double) gridVoltageST, (double) gridVoltageTR, (double) gridCurrentR, (double) gridCurrentS,
                (double) gridCurrentT, (double) currentOutput, powerFactor, frequency, (double) cumulativeEnergy, faultStatus
        );
    }


    private List<JunctionBoxDataRequestDto> parseJunctionBoxResponse(byte[] response) {
        if (response.length < 8) {
            throw new IllegalArgumentException("Invalid response length for junction box");
        }

        int index = 3; // 데이터 시작 위치 (ID, Function Code, Byte Count 제외)
        int numChannels = (response.length - 5) / 4; // 한 채널당 4Byte (전압 2Byte + 전류 2Byte)

        List<JunctionBoxDataRequestDto> dataList = new ArrayList<>();

        for (int i = 0; i < numChannels; i++) {
            int voltage = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
            int current = ((response[index + 2] & 0xFF) << 8) | (response[index + 3] & 0xFF);

            dataList.add(new JunctionBoxDataRequestDto(voltage / 10.0, current / 100.0));
            index += 4;
        }

        return dataList;
    }
}
