package com.example.web.service;
import com.example.web.entity.WeatherData2;
import com.example.web.repository.WeatherData2Repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Weather2Service {

    private final WeatherData2Repository repository;
    private static final String API_URL =
            "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php" +
                    "?tm={tm}&stn=95&authKey=5UE8LfqfRPGBPC36n5Tx4A";

    @Transactional
    public void fetchAndSave(String tm) {
        // 1) API 호출
        RestTemplate restTemplate = new RestTemplate();
        String raw = restTemplate.getForObject(API_URL, String.class);

        // 2) 유효한 데이터 줄 추출 (첫 번째 숫자로 시작하는 줄)
        List<String> lines = Arrays.asList(raw.split("\\r?\\n"));
        String dataLine = lines.stream()
                .filter(l -> l.matches("^\\d{12}.*"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("데이터 라인을 찾을 수 없습니다."));

        // 3) 공백으로 토큰 분리
        String[] tok = dataLine.trim().split("\\s+");
        // tok[0]=202505191300, tok[1]=95, tok[2]=20, tok[3]=2.9, ...

        // 4) Entity에 매핑
        WeatherData2 wd = WeatherData2.builder()
                // 관측 시각: YYMMDDHHMI
                .obsTime(tok[0])
                // 관측소 ID
                .stationId(Integer.valueOf(tok[1]))
                // 풍향(16방위)
                .windDir(parseInt(tok[2]))
                // 풍속(m/s)
                .windSpeed(parseDouble(tok[3]))
                // 돌풍 풍향
                .gustDir(parseInt(tok[4]))
                // 돌풍 풍속(m/s)
                .gustSpeed(parseDouble(tok[5]))
                // 돌풍 발생 시각
                .gustTime(parseDouble(tok[6]))
                // 현지기압(hPa)
                .pressureAtm(parseDouble(tok[7]))
                // 해면기압(hPa)
                .pressureSea(parseDouble(tok[8]))
                // 이슬점온도(PT) ※ tok[9]
                .tempDew(parseDouble(tok[9]))
                // 기압 경향(PR) ※ tok[10]
                .pr(parseDouble(tok[10]))
                // 기온(°C)
                .temperature(parseDouble(tok[11]))
                // 이슬점(°C)
                .dewPoint(parseDouble(tok[12]))
                // 습도(%)
                .humidity(parseDouble(tok[13]))
                // 수증기압(hPa)
                .vaporPressure(parseDouble(tok[14]))
                // 일강수량(mm)
                .rainfallDay(parseDouble(tok[15]))
                // 6시간 누적강수량(mm)
                .rainfall6h(parseDouble(tok[16]))
                // 강수유무(Intensity) ※ tok[17]
                .rainIntensity(parseDouble(tok[17]))
                // 3시간 강수량(mm)
                .rain3h(parseDouble(tok[18]))
                // 일일 가시거리(km)
                .sdDay(parseDouble(tok[19]))
                // 총가시거리(km)
                .sdTotal(parseDouble(tok[20]))
                // 해수면 수온(°C) — 샘플 데이터에서 tok[35]=23.6이므로 예시로 사용
                .seaSurfaceTemp(parseDouble(tok[35]))
                .build();

        // 5) 저장
        repository.save(wd);
    }

    private Integer parseInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String s) {
        try {
            double v = Double.parseDouble(s);
            // KMA API에서 '-9', '-9.0', '-99.0' 등 무의미값은 null 처리
            if (v <= -9.0) return null;
            return v;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
