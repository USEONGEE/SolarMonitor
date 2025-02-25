package com.example.fetcher.schedular;

import org.springframework.stereotype.Component;

@Component
public class RemsClient {

    public byte[] requestSinglePhase(String deviceId) {
        byte[] request = new byte[]{ 0x7E, 0x01, 0x01, 0x00, 0x00 }; // CRC 계산 필요
        return sendRequest(request);
    }

    public byte[] requestThreePhase(String deviceId) {
        byte[] request = new byte[]{ 0x7E, 0x02, 0x07, 0x00, 0x00 }; // CRC 계산 필요
        return sendRequest(request);
    }

    private byte[] sendRequest(byte[] request) {
        // RS-485 또는 TCP/IP 통신을 통해 데이터 요청
        return new byte[]{ /* 응답 데이터 */ };
    }
}
