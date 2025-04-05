package com.example.fetcher.schedular;

import com.fazecast.jSerialComm.SerialPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class ModbusClient {

    private final CrcCalculater crcCalculater;

    // 시리얼 포트 관련 설정 (RemsClient와 동일한 포트 사용 또는 별도로 설정)
    @Value("${modbus.serialPort}")
    private String serialPortName;

    @Value("${modbus.baudRate}")
    private int baudRate;

    @Value("${modbus.dataBits}")
    private int dataBits;

    @Value("${modbus.parity}")
    private int parity;

    @Value("${modbus.stopBits}")
    private int stopBits;

    // 응답 대기 및 타임아웃(ms)
    private static final int READ_TIMEOUT_MS = 2000;
    // 요청 후 안전하게 500ms 대기하도록 수정
    private static final int RESPONSE_DELAY_MS = 100;
    private static final int ENABLE_OFF_DELAY_MS = 10;

    /**
     * 접속함 데이터를 요청합니다.
     * @param inverterId 인버터 ID (접속함의 슬레이브 ID)
     * @param junctionBoxLength 접속함의 개수
     * @return 요청에 따른 응답 바이트 배열
     */
    public byte[] requestJunctionBox(Long inverterId, int junctionBoxLength) {
        byte[] request = createModbusRequest(inverterId, junctionBoxLength);
        return sendRequestSerial(request);
    }

    private byte[] createModbusRequest(Long inverterId, int junctionBoxLength) {
        byte slaveId = inverterId.byteValue();
        byte functionCode = 0x03; // Read Holding Registers
        int startAddress = 0x0105;
        int registerCount = junctionBoxLength * 2; // 각 접속함당 전압/전류(2개)

        byte[] request = new byte[8];
        request[0] = slaveId;
        request[1] = functionCode;
        request[2] = (byte) (startAddress >> 8);
        request[3] = (byte) (startAddress & 0xFF);
        request[4] = (byte) (registerCount >> 8);
        request[5] = (byte) (registerCount & 0xFF);

        int crc = crcCalculater.calculateCRC(request, 6);
        request[6] = (byte) (crc & 0xFF);  // CRC Low
        request[7] = (byte) ((crc >> 8) & 0xFF);  // CRC High

        return request;
    }

    private byte[] sendRequestSerial(byte[] request) {
        SerialPort port = SerialPort.getCommPort(serialPortName);
        port.setComPortParameters(baudRate, dataBits, stopBits, parity);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, READ_TIMEOUT_MS, 0);

        if (!port.openPort()) {
            throw new RuntimeException("시리얼 포트를 열 수 없습니다: " + serialPortName);
        }

        try {
            // 요청 패킷 전송
            port.writeBytes(request, request.length);
            port.flushIOBuffers();

            // 요청 전송 후 500ms 대기
            try {
                Thread.sleep(RESPONSE_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            // MODBUS 응답의 길이는 응답 패킷에 따라 달라질 수 있으므로,
            // 여기서는 예시로 고정된 길이(예: 8바이트 + 데이터 길이)를 가정합니다.
            int expectedResponseLength = 8;  // 실제 환경에 맞게 수정 필요
            byte[] response = new byte[expectedResponseLength];
            int totalRead = 0;
            while (totalRead < expectedResponseLength) {
                int bytesRead = port.readBytes(response, expectedResponseLength - totalRead, totalRead);
                if (bytesRead <= 0) break;
                totalRead += bytesRead;
            }
            if (totalRead < expectedResponseLength) {
                throw new RuntimeException("응답이 불완전합니다. 기대 바이트: " + expectedResponseLength + ", 읽은 바이트: " + totalRead);
            }

            try {
                Thread.sleep(ENABLE_OFF_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return response;
        } finally {
            port.closePort();
        }
    }
}
