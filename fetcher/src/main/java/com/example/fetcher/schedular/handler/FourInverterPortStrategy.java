package com.example.fetcher.schedular.handler;

import com.example.fetcher.schedular.utils.CrcCalculater;
import com.example.web.dto.JunctionBoxDataRequestDto;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FourInverterPortStrategy extends InverterPortStrategy {

    private final CrcCalculater crcCalculater;      // CRC 계산기
    @Override
    public boolean support(long inverterId) {
        return inverterId == 4L;
    }

    @Override
    public String getPortNameByInverterId(long inverterId) {
        return "COM19";
    }

    @Override
    public List<JunctionBoxDataRequestDto> requestJunctionBoxData(long deviceId, String portName) {

        SerialPort port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(SerialPort.ONE_STOP_BIT);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        try{
            if (!port.openPort()) {
                throw new RuntimeException("시리얼 포트를 열 수 없습니다: " + portName);
            }

            byte[] header = new byte[]{
                    (byte) deviceId,
                    0x03,
                    0x01,
                    0x05,
                    (byte) 0,
                    (byte) 9
            };
            int crc = crcCalculater.calculateCRC(header, header.length);
            byte crcLow = (byte) (crc & 0xFF);
            byte crcHigh = (byte) ((crc >> 8) & 0xFF);

            byte[] request = new byte[8];
            System.arraycopy(header, 0, request, 0, header.length);
            request[6] = crcLow;
            request[7] = crcHigh;

            port.writeBytes(request, request.length);
            log.info("접속함 요청 전송 ({}): {}", portName, DatatypeConverter.printHexBinary(request));

            // 2) 응답 수신 (13바이트)
            byte[] response = new byte[13];
            int read = port.readBytes(response, response.length);
            if (read != 13) {
                throw new RuntimeException("응답 수신 실패: 길이 = " + read);
            }
            log.info("접속함 응답 수신: {}", DatatypeConverter.printHexBinary(response));

            List<JunctionBoxDataRequestDto> dataList = this.parseJunctionBoxResponse(response);
            log.info("접속함 데이터 파싱 결과: {}", dataList);

            return dataList;
        } catch (Exception e) {
            throw new RuntimeException("접속함 데이터 요청 실패", e);
        } finally {
            port.closePort();
        }

    }
}
