package com.energy.outsourcing.schedular;

import com.energy.outsourcing.collector.DataCollector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DataCollectionScheduler {

    private final DataCollector dataCollector;

    public DataCollectionScheduler(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    @Scheduled(fixedRate = 5000)
    public void collectAndSaveData() {
        dataCollector.collectAndSaveData();
    }
}
