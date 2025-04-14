package com.example.fetcher.schedular;

import com.example.fetcher.schedular.utils.ModbusClient;
import com.example.fetcher.schedular.utils.RemsClient;
import com.example.web.dto.JunctionBoxDataRequestDto;
import com.example.web.dto.SeasonalPanelDataDto;
import com.example.web.dto.SinglePhaseInverterDto;
import com.example.web.dto.ThreePhaseInverterDto;
import com.example.web.entity.*;
import com.example.web.repository.InverterDataRepository;
import com.example.web.repository.InverterRepository;
import com.example.web.repository.JunctionBoxDataRepository;
import com.example.web.repository.JunctionBoxRepository;
import com.fazecast.jSerialComm.SerialPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class DataRequesterImpl implements DataRequester {

    private final InverterRepository inverterRepository;
    private final InverterDataRepository inverterDataRepository;
    private final JunctionBoxRepository junctionBoxRepository;
    private final JunctionBoxDataRepository junctionBoxDataRepository;
//    private final ModbusClient modbusClient; // MODBUS 요청 (접속함)
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
        log.info(String.valueOf(dto));

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
//        // 1️⃣ 인버터 ID로 접속함 조회
//        List<JunctionBox> junctionBoxes = junctionBoxRepository.findByInverterId(inverterId);
//
//        if (junctionBoxes.isEmpty()) {
//            throw new RuntimeException("해당 ID의 접속함이 존재하지 않습니다.");
//        }
//
//        // 2️⃣ MODBUS 요청
//        byte[] response = modbusClient.requestJunctionBox(inverterId, junctionBoxes.size());
//
//        // 3️⃣ 응답 데이터 파싱
//        List<JunctionBoxDataRequestDto> dataList = parseJunctionBoxResponse(response);
//
//        // 4️⃣ 파싱된 데이터를 데이터베이스에 저장
//        LocalDateTime timestamp = LocalDateTime.now();
//        List<JunctionBoxData> savedDataList = new ArrayList<>();
//
//        for (int i = 0; i < junctionBoxes.size(); i++) {
//            JunctionBox junctionBox = junctionBoxes.get(i);
//            JunctionBoxDataRequestDto dto = dataList.get(i);
//
//            JunctionBoxData junctionBoxData = JunctionBoxData.fromDTO(junctionBox, dto, timestamp);
//            savedDataList.add(junctionBoxData);
//        }
//
//        junctionBoxDataRepository.saveAll(savedDataList);

        return null; // 반환값 없음.
    }


    @Override
    public SeasonalPanelDataDto requestSeasonal() {
        // COM10 포트를 사용 (필요에 따라 포트 이름과 통신 파라미터 조정)
        SerialPort port = SerialPort.getCommPort("COM10");
        port.setBaudRate(9600);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        // 읽기 타임아웃을 1000ms로 설정
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        if (!port.openPort()) {
            throw new RuntimeException("시리얼 포트를 열 수 없습니다: COM10");
        }

        try {
            // 요청 패킷 전송: "$haeulengcom#"
            String command = "$haeulengcom#";
            byte[] commandBytes = command.getBytes(StandardCharsets.US_ASCII);
            port.writeBytes(commandBytes, commandBytes.length);
            log.info("전송한 명령: " + command);

            // 응답 지연 시간 5ms 대기
            Thread.sleep(5);

            // 예상 응답 길이만큼 데이터 읽기 (예: 100바이트)
            byte[] buffer = new byte[100];
            int bytesRead = port.readBytes(buffer, buffer.length);
            if (bytesRead <= 0) {
                throw new RuntimeException("응답을 받지 못했습니다.");
            }
            String response = new String(buffer, 0, bytesRead, StandardCharsets.US_ASCII).trim();
            log.info("수신된 응답: " + response);

            // 응답 문자열 전처리: 끝에 '#' 기호가 있다면 제거
            if (response.endsWith("#")) {
                response = response.substring(0, response.length() - 1).trim();
            }

            // 응답 포맷: 예) "$D2        83       4.3       114       5.2"
            int idx = response.indexOf("D2");
            if (idx == -1) {
                throw new RuntimeException("응답 포맷이 올바르지 않습니다.");
            }
            String dataPart = response.substring(idx + 2).trim();
            // 공백을 기준으로 나누기
            String[] parts = dataPart.split("\\s+");
            if (parts.length < 4) {
                throw new RuntimeException("응답 데이터 필드가 부족합니다. 필드 개수: " + parts.length);
            }

            // 각 필드를 Double로 파싱
            double verticalInsolation = Double.parseDouble(parts[0]);
            double externalTemperature = Double.parseDouble(parts[1]);
            double horizontalInsolation = Double.parseDouble(parts[2]);
            double moduleSurfaceTemperature = Double.parseDouble(parts[3]);

            // SeasonalPanelDataDto 생성
            SeasonalPanelDataDto dto = new SeasonalPanelDataDto(
                    verticalInsolation, externalTemperature, horizontalInsolation, moduleSurfaceTemperature
            );
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("데이터 요청 및 파싱 중 오류 발생", e);
        } finally {
            port.closePort();
        }
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
        // 최소 36바이트는 존재해야 한다(사진 기준: 고장 상태 2바이트가 없을 수 있음)
        if (response.length < 36) {
            throw new IllegalArgumentException(
                    "Invalid response length for three-phase inverter. Must be at least 36 bytes, but got "
                            + response.length
            );
        }

        int index = 4; // 데이터 시작 위치 (SOP, ID, Command, Data Length 제외)

        // 1. PV 전압(2Byte) + PV 전류(2Byte) + PV 출력(4Byte) = 총 8Byte
        int pvVoltage = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
        int pvCurrent = ((response[index + 2] & 0xFF) << 8) | (response[index + 3] & 0xFF);
        int pvPower = ((response[index + 4] & 0xFF) << 24) | ((response[index + 5] & 0xFF) << 16)
                | ((response[index + 6] & 0xFF) << 8) | (response[index + 7] & 0xFF);

        // 2. 삼상 전압 (RS, ST, TR) = 2Byte × 3 = 6Byte
        int gridVoltageRS = ((response[index + 8] & 0xFF) << 8) | (response[index + 9] & 0xFF);
        int gridVoltageST = ((response[index + 10] & 0xFF) << 8) | (response[index + 11] & 0xFF);
        int gridVoltageTR = ((response[index + 12] & 0xFF) << 8) | (response[index + 13] & 0xFF);

        // 3. 삼상 전류 (R, S, T) = 2Byte × 3 = 6Byte
        int gridCurrentR = ((response[index + 14] & 0xFF) << 8) | (response[index + 15] & 0xFF);
        int gridCurrentS = ((response[index + 16] & 0xFF) << 8) | (response[index + 17] & 0xFF);
        int gridCurrentT = ((response[index + 18] & 0xFF) << 8) | (response[index + 19] & 0xFF);

        // 4. 현재 출력(4Byte)
        int currentOutput = ((response[index + 20] & 0xFF) << 24) | ((response[index + 21] & 0xFF) << 16)
                | ((response[index + 22] & 0xFF) << 8) | (response[index + 23] & 0xFF);

        // 5. 역률(0.1% 단위 변환 필요) (2Byte)
        double powerFactor = (((response[index + 24] & 0xFF) << 8) | (response[index + 25] & 0xFF)) / 10.0;

        // 6. 주파수(0.1Hz 단위 변환 필요) (2Byte)
        double frequency = (((response[index + 26] & 0xFF) << 8) | (response[index + 27] & 0xFF)) / 10.0;

        // 7. 누적 발전량(8Byte)
        long cumulativeEnergy = ((response[index + 28] & 0xFFL) << 56) | ((response[index + 29] & 0xFFL) << 48)
                | ((response[index + 30] & 0xFFL) << 40) | ((response[index + 31] & 0xFFL) << 32)
                | ((response[index + 32] & 0xFFL) << 24) | ((response[index + 33] & 0xFFL) << 16)
                | ((response[index + 34] & 0xFFL) << 8) | (response[index + 35] & 0xFFL);

        // 8. 고장 상태(2Byte) - 없을 수도 있으므로 기본값을 0으로 설정
        int faultStatus = 0;
        // 응답 길이가 38바이트 이상이라면, 마지막 2바이트를 faultStatus로 파싱
        if (response.length >= 38) {
            faultStatus = ((response[index + 36] & 0xFF) << 8) | (response[index + 37] & 0xFF);
        }

        return new ThreePhaseInverterDto(
                (double) pvVoltage,
                (double) pvCurrent,
                (double) pvPower,
                (double) gridVoltageRS,
                (double) gridVoltageST,
                (double) gridVoltageTR,
                (double) gridCurrentR,
                (double) gridCurrentS,
                (double) gridCurrentT,
                (double) currentOutput,
                powerFactor,
                frequency,
                (double) cumulativeEnergy,
                faultStatus
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
