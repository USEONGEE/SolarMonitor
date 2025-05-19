package com.example.web.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "weather_data2")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 관측 시각: YYMMDDHHMI (KST) */
    @Column(name = "obs_time", length = 12, nullable = false)
    private String obsTime;

    /** 관측소 코드 STN */
    @Column(name = "station_id", nullable = false)
    private Integer stationId;

    /** 풍향 WD (16방위) */
    @Column(name = "wind_dir")
    private Integer windDir;

    /** 풍속 WS (m/s) */
    @Column(name = "wind_speed")
    private Double windSpeed;

    /** 돌풍 풍향 GST_WD */
    @Column(name = "gust_dir")
    private Integer gustDir;

    /** 돌풍 풍속 GST_WS */
    @Column(name = "gust_speed")
    private Double gustSpeed;

    /** 돌풍 발생 시각 GST_TM */
    @Column(name = "gust_time")
    private Double gustTime;

    /** 현지기압 PA (hPa) */
    @Column(name = "pressure_atm")
    private Double pressureAtm;

    /** 해면기압 PS (hPa) */
    @Column(name = "pressure_sea")
    private Double pressureSea;

    /** 이슬점 온도 PT (°C) */
    @Column(name = "temp_dew")
    private Double tempDew;

    /** 해면온도 PR (hPa) */
    @Column(name = "pr")
    private Double pr;

    /** 기온 TA (°C) */
    @Column(name = "temperature")
    private Double temperature;

    /** 이슬점 온도 TD (°C) */
    @Column(name = "dew_point")
    private Double dewPoint;

    /** 습도 HM (%) */
    @Column(name = "humidity")
    private Double humidity;

    /** 증기압 PV (hPa) */
    @Column(name = "vapor_pressure")
    private Double vaporPressure;

    /** 일강수량 RN_DAY (mm) */
    @Column(name = "rainfall_day")
    private Double rainfallDay;

    /** 6시간 강수량 RN_JUN (mm) */
    @Column(name = "rainfall_6h")
    private Double rainfall6h;

    /** 강수유무 RN_INT */
    @Column(name = "rain_intensity")
    private Double rainIntensity;

    /** 3시간 누적강수 RN_HR3 */
    @Column(name = "rain_3h")
    private Double rain3h;

    /** 일일 가시거리 SD_DAY (km) */
    @Column(name = "sd_day")
    private Double sdDay;

    /** 시정 총량 SD_TOT */
    @Column(name = "sd_total")
    private Double sdTotal;

    /** 해면수온 SEA (°C) */
    @Column(name = "sea_surface_temp")
    private Double seaSurfaceTemp;

    // … 필요에 따라 남은 컬럼들(예: WC, WP, WW, CA_TOT, CA_MID, CA_MIN, CH, CT 등)을
    // @Column(name="…") 과 함께 동일한 패턴으로 추가하세요.

    /**
     * obsTime(String)을 LocalDateTime 으로 변환해서 사용하고 싶다면
     * 아래와 같은 헬퍼 메서드를 추가할 수 있습니다.
     */
    public LocalDateTime getParsedObsTime() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMddHHmm");
        return LocalDateTime.parse(this.obsTime, fmt);
    }
}
