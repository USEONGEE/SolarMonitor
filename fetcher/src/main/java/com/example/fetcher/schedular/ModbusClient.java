package com.example.fetcher.schedular;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModbusClient {

    private final CrcCalculater crcCalculater;



    public byte[] requestJunctionBox(Long inverterId, int junctionBoxLength) {
        byte[] request = createModbusRequest(inverterId, junctionBoxLength); // 요청 패킷 생성
        return sendRequest(request); // 실제 요청 전송 후 응답 수신
    }

    private byte[] createModbusRequest(Long inverterId, int junctionBoxLength) {
        byte slaveId = inverterId.byteValue(); // 인버터 ID를 1Byte로 변환
        byte functionCode = 0x03; // Read Holding Registers
        int startAddress = 0x0105; // 시작 주소 (접속함 데이터)
        int registerCount = junctionBoxLength * 2; // 각 접속함당 전압/전류(2개) × 접속함 개수

        byte[] request = new byte[8]; // MODBUS 요청 패킷 (8바이트)

        request[0] = slaveId;
        request[1] = functionCode;
        request[2] = (byte) (startAddress >> 8); // 시작 주소 (상위 바이트)
        request[3] = (byte) (startAddress & 0xFF); // 시작 주소 (하위 바이트)
        request[4] = (byte) (registerCount >> 8); // 요청할 레지스터 개수 (상위 바이트)
        request[5] = (byte) (registerCount & 0xFF); // 요청할 레지스터 개수 (하위 바이트)

        // CRC 계산 (2바이트)
        int crc = crcCalculater.calculateCRC(request, 6);
        request[6] = (byte) (crc & 0xFF);  // CRC Low
        request[7] = (byte) ((crc >> 8) & 0xFF);  // CRC High

        return request;
    }



    private byte[] sendRequest(byte[] request) {
        // RS-485 또는 TCP/IP 통신을 통해 요청 전송 후 응답 수신
        return new byte[]{ /* 응답 데이터 */ };
    }
}
