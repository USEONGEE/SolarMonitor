package com.example.fetcher.schedular.utils;

import com.example.fetcher.schedular.utils.CrcCalculater;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("prod")
public class RemsClient {

    private final CrcCalculater crcCalculater;

    // 시리얼 포트 이름 (예: COM3, /dev/ttyUSB0)

    @Value("${rems.baudRate}")
    private int baudRate;

    @Value("${rems.dataBits}")
    private int dataBits;

    @Value("${rems.parity}")
    private int parity; // SerialPort.NO_PARITY, SerialPort.EVEN_PARITY 등

    @Value("${rems.stopBits}")
    private int stopBits; // SerialPort.ONE_STOP_BIT, SerialPort.TWO_STOP_BITS 등

    // 응답 대기 및 Enable Off 지연 (ms)
    // 기존 5ms에서 500ms로 변경
    private static final int RESPONSE_DELAY_MS = 100;
    private static final int ENABLE_OFF_DELAY_MS = 10;
    // 시리얼 포트 읽기 타임아웃 (ms)
    private static final int READ_TIMEOUT_MS = 2000;

    public RemsClient(CrcCalculater crcCalculater) {
        this.crcCalculater = crcCalculater;
    }

    /**
     * 단상 인버터 데이터 요청:
     * 요청 패킷: SOP(0x7E), 인버터ID, 명령(0x01), CRC High, CRC Low
     * 응답 길이는 단상의 경우 26바이트로 예상합니다.
     */
    public byte[] requestSinglePhase(Long id) {
        byte[] request = new byte[5];
        request[0] = 0x7E;
        try {
            request[1] = id.byteValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 id: " + id, e);
        }
        request[2] = 0x01; // 단상 명령
        int crc = crcCalculater.calculateCRC(request, 3);
        request[3] = (byte) (crc & 0xFF);
        request[4] = (byte) ((crc >> 8) & 0xFF);

        log.info("단상 인버터 요청 패킷: {}", request);

        log.info("단상 인버터 요청 패킷: {}", request);
        if (id == 1L) {
            return sendRequestSerial(request, 38, "COM12");
        } else if (id == 2L) {
            return sendRequestSerial(request, 38, "COM13");
        } else {
            throw new IllegalArgumentException("유효하지 않은 id: " + id);
        }
    }

    /**
     * 삼상 인버터 데이터 요청:
     * 요청 패킷: SOP(0x7E), 인버터ID, 명령(0x07), CRC High, CRC Low
     * 응답 길이는 삼상의 경우 38바이트로 예상합니다.
     */
    public byte[] requestThreePhase(Long id) {
        byte[] request = new byte[5];
        request[0] = 0x7E;
        try {
            request[1] = id.byteValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 id: " + id, e);
        }
        request[2] = 0x07; // 삼상 명령
        int crc = crcCalculater.calculateCRC(request, 3);
        request[3] = (byte) (crc & 0xFF);
        request[4] = (byte) ((crc >> 8) & 0xFF);
        log.info("삼상 인버터 요청 패킷: {}", request);

        if (id == 1L) {
            return sendRequestSerial(request, 38, "COM12");
        } else if (id == 2L) {
            return sendRequestSerial(request, 38, "COM13");
        } else {
            throw new IllegalArgumentException("유효하지 않은 id: " + id);
        }

    }

    /**
     * 시리얼 포트를 통해 요청을 전송하고 응답을 수신합니다.
     *
     * @param request 요청 바이트 배열
     * @param expectedResponseLength 예상 응답 길이 (바이트 단위)
     * @return 수신된 응답 바이트 배열
     */
    private byte[] sendRequestSerial(byte[] request, int expectedResponseLength, String serialPortName) {
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

            // 요청 전송 후 500ms 대기 (안전하게 전체 응답이 도착할 때까지 기다림)
            try {
                Thread.sleep(RESPONSE_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            byte[] response = new byte[expectedResponseLength];
            int totalRead = 0;
            while (totalRead < expectedResponseLength) {
                int bytesRead = port.readBytes(response, expectedResponseLength - totalRead, totalRead);
                if (bytesRead <= 0) {
                    break;
                }
                totalRead += bytesRead;
            }
            if (totalRead < expectedResponseLength) {
                throw new RuntimeException("응답이 불완전합니다. 기대 바이트: " + expectedResponseLength + ", 읽은 바이트: " + totalRead);
            }

            // 응답 수신 후 Enable Off 지연 적용
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
