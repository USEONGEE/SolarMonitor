package com.example.fetcher.schedular.handler;

import com.example.web.dto.JunctionBoxDataRequestDto;

import java.util.ArrayList;
import java.util.List;

public abstract class InverterPortStrategy {
    public abstract boolean support(long inverterId);

    public abstract String getPortNameByInverterId(long inverterId);
    public abstract List<JunctionBoxDataRequestDto> requestJunctionBoxData(long deviceId, String portName);

    protected List<JunctionBoxDataRequestDto> parseJunctionBoxResponse(byte[] response) {
        if (response.length < 8) {
            throw new IllegalArgumentException("Invalid response length for junction box");
        }

        int index = 3; // 데이터 시작 위치 (ID, Function Code, Byte Count 제외)
        int numChannels = (response.length - 5) / 4; // 한 채널당 4Byte (전압 2Byte + 전류 2Byte)

        List<JunctionBoxDataRequestDto> dataList = new ArrayList<>();

        for (int i = 0; i < numChannels; i++) {
            int voltage = ((response[index] & 0xFF) << 8) | (response[index + 1] & 0xFF);
            int current = ((response[index + 2] & 0xFF) << 8) | (response[index + 3] & 0xFF);

            if (current < 0) {
                current = 0;
            }

            dataList.add(new JunctionBoxDataRequestDto(voltage / 10.0, current / 100.0));
            index += 4;
        }

        return dataList;
    }
}
