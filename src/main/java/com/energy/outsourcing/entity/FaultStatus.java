package com.energy.outsourcing.entity;

public enum FaultStatus {
    INVERTER_OPERATION(0),         // Bit 0
    PV_OVERVOLTAGE(1),             // Bit 1
    PV_UNDERVOLTAGE(2),            // Bit 2
    PV_OVERCURRENT(3),             // Bit 3
    INVERTER_IGBT_ERROR(4),        // Bit 4
    INVERTER_OVERHEAT(5),          // Bit 5
    GRID_OVERVOLTAGE(6),           // Bit 6
    GRID_UNDERVOLTAGE(7),          // Bit 7
    GRID_OVERCURRENT(8),           // Bit 8
    GRID_OVERFREQUENCY(9),         // Bit 9
    GRID_UNDERFREQUENCY(10),       // Bit 10
    ISLANDING(11),                 // Bit 11
    GROUND_FAULT(12);              // Bit 12
    // Bit 13 ~ 15: Reserved

    private int bitPosition;

    FaultStatus(int bitPosition) {
        this.bitPosition = bitPosition;
    }

    public int getBitPosition() {
        return bitPosition;
    }

    public boolean isFault(int faultStatusValue) {
        return (faultStatusValue & (1 << bitPosition)) != 0;
    }
}
