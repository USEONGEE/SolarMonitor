package com.example.web.entity;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "weather_observation", schema = "energy_db")
public class WeatherObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ww", length = 22)
    private String ww;

    @Column(name = "ws")
    private Float ws;

    @Column(name = "wp")
    private Integer wp;

    @Column(name = "wh")
    private Float wh;

    @Column(name = "wd")
    private Integer wd;

    @Column(name = "wc")
    private Integer wc;

    @Column(name = "vs")
    private Integer vs;

    @Column(name = "ts")
    private Float ts;

    @Column(name = "tm")
    private LocalDateTime tm;

    @Column(name = "te_03")
    private Float te03;

    @Column(name = "te_02")
    private Float te02;

    @Column(name = "te_01")
    private Float te01;

    @Column(name = "te_005")
    private Float te005;

    @Column(name = "td")
    private Float td;

    @Column(name = "ta")
    private Float ta;

    @Column(name = "stn")
    private Integer stn;

    @Column(name = "st_sea")
    private Integer stSea;

    @Column(name = "st_gd")
    private Integer stGd;

    @Column(name = "ss")
    private Float ss;

    @Column(name = "si")
    private Float si;

    @Column(name = "sd_tot")
    private Float sdTot;

    @Column(name = "sd_hr3")
    private Float sdHr3;

    @Column(name = "sd_day")
    private Float sdDay;

    @Column(name = "rn_jun")
    private Float rnJun;

    @Column(name = "rn_int")
    private Float rnInt;

    @Column(name = "rn_day")
    private Float rnDay;

    @Column(name = "rn")
    private Float rn;

    @Column(name = "pv")
    private Float pv;

    @Column(name = "pt")
    private Integer pt;

    @Column(name = "ps")
    private Float ps;

    @Column(name = "pr")
    private Float pr;

    @Column(name = "pa")
    private Float pa;

    @Column(name = "ix")
    private Integer ix;

    @Column(name = "ir")
    private Integer ir;

    @Column(name = "hm")
    private Float hm;

    @Column(name = "gst_ws")
    private Float gstWs;

    @Column(name = "gst_wd")
    private Integer gstWd;

    @Column(name = "gst_tm", length = 4)
    private String gstTm;

    @Column(name = "ct_top")
    private Integer ctTop;

    @Column(name = "ct_mid")
    private Integer ctMid;

    @Column(name = "ct_low")
    private Integer ctLow;

    @Column(name = "ct", length = 8)
    private String ct;

    @Column(name = "ch_min")
    private Integer chMin;

    @Column(name = "ca_tot")
    private Integer caTot;

    @Column(name = "ca_mid")
    private Integer caMid;

    @Column(name = "bf")
    private Integer bf;
}