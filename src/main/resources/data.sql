-- Inverter 데이터 삽입
INSERT INTO inverter (id, inverter_type, device_id) VALUES (1, 'SINGLE', 'SINGLE_PHASE_1');
INSERT INTO inverter (id, inverter_type, device_id) VALUES (2, 'THREE', 'THREE_PHASE_1');

-- JunctionBox 데이터 삽입
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (1, 'JUNCTION_BOX_1', 1);
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (2, 'JUNCTION_BOX_2', 1);


