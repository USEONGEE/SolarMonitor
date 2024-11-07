-- Inverter 데이터 삽입
INSERT INTO inverter (id, inverter_type, device_id) VALUES (1, 'SINGLE', 'SINGLE_PHASE_1');
INSERT INTO inverter (id, inverter_type, device_id) VALUES (2, 'THREE', 'THREE_PHASE_1');

-- JunctionBox 데이터 삽입
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (1, 'JUNCTION_BOX_1', 1);
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (2, 'JUNCTION_BOX_2', 1);
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (3, 'JUNCTION_BOX_3', 2);
INSERT INTO junction_box (id, device_id, inverter_id) VALUES (4, 'JUNCTION_BOX_4', 2);

INSERT INTO member (id, name, member_type) VALUES (1, 'admin', 'ADMIN');

-- 가정: id가 1인 'ADMIN' 역할의 Member가 이미 생성된 상태입니다.

INSERT INTO notification_post (id, title, member_id, created_date, last_modified_date) VALUES
                                                                                   (1,'Post Title 1', 1, NOW(), NOW()),
                                                                                   (2,'Post Title 2', 1, NOW(), NOW()),
                                                                                   (3,'Post Title 3', 1, NOW(), NOW()),
                                                                                   (4,'Post Title 4', 1, NOW(), NOW()),
                                                                                   (5,'Post Title 5', 1, NOW(), NOW()),
                                                                                   (6,'Post Title 6', 1, NOW(), NOW()),
                                                                                   (7,'Post Title 7', 1, NOW(), NOW()),
                                                                                   (8,'Post Title 8', 1, NOW(), NOW()),
                                                                                   (9,'Post Title 9', 1, NOW(), NOW()),
                                                                                   (10,'Post Title 10', 1, NOW(), NOW());



