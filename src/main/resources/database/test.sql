-- 0. 기존 데이터 초기화 (선택 사항)
-- TRUNCATE TABLE taking_logs;
-- TRUNCATE TABLE medicine_schedules;
-- TRUNCATE TABLE medicines;
-- TRUNCATE TABLE prescriptions;
-- TRUNCATE TABLE users;

-- 1. 사용자/처방/약/스케줄 기본 데이터
INSERT INTO users (id, role, name, phone, is_senior) VALUES
    (1001, 'SENIOR', '김온하', '010-1111-2222', true)
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO prescriptions (id, senior_id, issued_date, hospital_name, doctor_name, note) VALUES
    (5001, 1001, '2025-10-25', '온하루내과', '박진우', '11월 복약 계획')
    ON DUPLICATE KEY UPDATE note = VALUES(note);

INSERT INTO medicines (id, prescription_id, medicine_name, daily_dose_count, administration_method, memo)
VALUES
    (7001, 5001, '고혈압약A', 1, '정제', '아침'),
    (7002, 5001, '당뇨약B', 1, '정제', '점심'),
    (7003, 5001, '비타민C', 1, '정제', '저녁')
    ON DUPLICATE KEY UPDATE memo = VALUES(memo);

INSERT INTO medicine_schedules (id, medicine_id, schedule_type, repeat_type, days_bitmask,
                                notify_time, start_date, end_date)
VALUES
    (9001, 7001, 'MORNING', 'DAILY', 127, '07:00:00', '2025-10-25', '2025-12-31'),
    (9002, 7002, 'LUNCH',   'DAILY', 127, '12:30:00', '2025-10-25', '2025-12-31'),
    (9003, 7003, 'EVENING', 'DAILY', 127, '20:00:00', '2025-10-25', '2025-12-31')
    ON DUPLICATE KEY UPDATE notify_time = VALUES(notify_time);

-- 2. 2025년 11월 한 달 TakingLog 더미 생성
WITH RECURSIVE calendar AS (
    SELECT DATE('2025-11-01') AS day
UNION ALL
SELECT day + INTERVAL 1 DAY FROM calendar WHERE day < DATE('2025-11-30')
    ),
    slots AS (
SELECT 9001 AS schedule_id, TIME('07:00:00') AS slot_time UNION ALL
SELECT 9002, TIME('12:30:00') UNION ALL
SELECT 9003, TIME('20:00:00')
    ),
    base_logs AS (
SELECT
    ROW_NUMBER() OVER () + 100000 AS id,
    s.schedule_id,
    CAST(CONCAT(c.day, ' ', s.slot_time) AS DATETIME) AS scheduled_datetime,
    CASE
    WHEN DAY(c.day) <= 10 THEN CAST(CONCAT(c.day, ' ', ADDTIME(s.slot_time, '00:05:00')) AS DATETIME)
    WHEN DAY(c.day) BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003)
    THEN CAST(CONCAT(c.day, ' ', ADDTIME(s.slot_time, '00:15:00')) AS DATETIME)
    ELSE NULL
    END AS taken_datetime,
    CASE
    WHEN DAY(c.day) <= 10 THEN 1
    WHEN DAY(c.day) BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003) THEN 1
    ELSE 0
    END AS is_taken,
    CASE
    WHEN DAY(c.day) <= 10 THEN 5
    WHEN DAY(c.day) BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003) THEN 15
    ELSE NULL
    END AS delay_minutes
FROM calendar c
    CROSS JOIN slots s
    )
INSERT INTO taking_logs (id, schedule_id, scheduled_datetime, taken_datetime, is_taken, delay_minutes)
SELECT id, schedule_id, scheduled_datetime, taken_datetime, is_taken, delay_minutes
FROM base_logs
    ON DUPLICATE KEY UPDATE
                         taken_datetime = VALUES(taken_datetime),
                         is_taken = VALUES(is_taken),
                         delay_minutes = VALUES(delay_minutes);