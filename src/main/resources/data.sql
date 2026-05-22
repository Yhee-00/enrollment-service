-- 강의 2개 (DRAFT, OPEN)
INSERT INTO classes (title, description, price, capacity, start_date, end_date, status, creator_id, created_at)
VALUES ('Spring Boot 완전 정복', 'JPA부터 배포까지', 99000, 2, '2025-07-01', '2025-08-31', 'OPEN', 1, NOW());
INSERT INTO classes (title, description, price, capacity, start_date, end_date, status, creator_id, created_at)
VALUES ('React 기초', '프론트엔드 입문', 59000, 10, '2025-07-01', '2025-08-31', 'DRAFT', 2, NOW());

-- OPEN 강의에 수강신청 몇 개
INSERT INTO enrollments (class_id, user_id, status, created_at) VALUES (1, 1, 'PENDING', now());
INSERT INTO enrollments (class_id, user_id, status, created_at) VALUES (1, 2, 'CONFIRMED', now());
INSERT INTO enrollments (class_id, user_id, status, created_at) VALUES (1, 3, 'CANCELLED', now());