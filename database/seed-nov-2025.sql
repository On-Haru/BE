usa sa;

-- OnHaru test seed: November 2025 sample data
-- Inserts a fresh senior (ID 1202) with prescription, medicines, schedules, and 1-month taking logs.
-- Existing data is preserved; INSERT ... ON DUPLICATE KEY UPDATE is used where appropriate.

USE sa;

-- 1) Senior user
INSERT INTO users (id, user_name, phone, year, password, code, role)
VALUES (
           1202,
           '박복약',
           '01022223333',
           '1952',
           '8f6c3b67b4f90c23b58724df1dea7e4ddb30c300a54a03f87bd401d7280ead48db75e45e46f9648f68431ce279cd272f',
           1234,
           'SENIOR'
       )
    ON DUPLICATE KEY UPDATE user_name = VALUES(user_name);

-- 2) Prescription (November plan)
INSERT INTO prescriptions (id, senior_id, issued_date, hospital_name, doctor_name, note)
VALUES (
           5201,
           1202,
           '2025-10-31',
           '온하루가정의학과',
           '이의사',
           '2025년 11월 복약 플랜'
       )
    ON DUPLICATE KEY UPDATE note = VALUES(note);

-- 3) Medicines (morning/lunch/evening)
INSERT INTO medicines (id, prescription_id, medicine_name, daily_dose_count,
                       administration_method, memo, total_count, duration_days, ai_description)
VALUES
    (7201, 5201, '혈압약C', 1, '정제', '아침 식전', 60, 30, '혈압 안정화'),
    (7202, 5201, '당뇨약D', 1, '정제', '점심 식후', 60, 30, '혈당 조절'),
    (7203, 5201, '진통제E', 1, '정제', '저녁 필요 시', 60, 30, '통증 완화')
    ON DUPLICATE KEY UPDATE memo = VALUES(memo);

-- 4) Schedules (daily from Nov 1 to Nov 30)
INSERT INTO medicine_schedules (id, medicine_id, schedule_type, repeat_type,
                                days_bitmask, notify_time, start_date, end_date)
VALUES
    (9301, 7201, 'MORNING', 'DAILY', 127, '07:30:00', '2025-11-01', '2025-11-30'),
    (9302, 7202, 'LUNCH',   'DAILY', 127, '12:45:00', '2025-11-01', '2025-11-30'),
    (9303, 7203, 'EVENING', 'DAILY', 127, '20:30:00', '2025-11-01', '2025-11-30')
    ON DUPLICATE KEY UPDATE notify_time = VALUES(notify_time);

-- 5) Taking logs for November 2025
-- Days 1-15: all taken within 5 minutes
-- Days 16-24: morning/evening taken with 20 min delay, lunch missed
-- Days 25-30: all missed
INSERT INTO taking_logs (schedule_id, scheduled_datetime, taken_datetime, is_taken, delay_minutes)
SELECT s.schedule_id,
    TIMESTAMP(DATE_ADD('2025-11-01', INTERVAL d.day_offset DAY), s.slot_time) AS scheduled_dt,
    CASE
    WHEN d.day_no BETWEEN 1 AND 15 THEN
    DATE_ADD(TIMESTAMP(DATE_ADD('2025-11-01', INTERVAL d.day_offset DAY), s.slot_time),
    INTERVAL 5 MINUTE)
    WHEN d.day_no BETWEEN 16 AND 24 AND s.schedule_id IN (9301, 9303) THEN
    DATE_ADD(TIMESTAMP(DATE_ADD('2025-11-01', INTERVAL d.day_offset DAY), s.slot_time),
    INTERVAL 20 MINUTE)
    ELSE NULL
END AS taken_dt,
       CASE
           WHEN d.day_no BETWEEN 1 AND 15 THEN 1
           WHEN d.day_no BETWEEN 16 AND 24 AND s.schedule_id IN (9301, 9303) THEN 1
           ELSE 0
END AS is_taken,
       CASE
           WHEN d.day_no BETWEEN 1 AND 15 THEN 5
           WHEN d.day_no BETWEEN 16 AND 24 AND s.schedule_id IN (9301, 9303) THEN 20
           ELSE NULL
END AS delay_minutes
FROM (
         SELECT 0 AS day_offset, 1 AS day_no
         UNION ALL SELECT 1, 2
         UNION ALL SELECT 2, 3
         UNION ALL SELECT 3, 4
         UNION ALL SELECT 4, 5
         UNION ALL SELECT 5, 6
         UNION ALL SELECT 6, 7
         UNION ALL SELECT 7, 8
         UNION ALL SELECT 8, 9
         UNION ALL SELECT 9, 10
         UNION ALL SELECT 10, 11
         UNION ALL SELECT 11, 12
         UNION ALL SELECT 12, 13
         UNION ALL SELECT 13, 14
         UNION ALL SELECT 14, 15
         UNION ALL SELECT 15, 16
         UNION ALL SELECT 16, 17
         UNION ALL SELECT 17, 18
         UNION ALL SELECT 18, 19
         UNION ALL SELECT 19, 20
         UNION ALL SELECT 20, 21
         UNION ALL SELECT 21, 22
         UNION ALL SELECT 22, 23
         UNION ALL SELECT 23, 24
         UNION ALL SELECT 24, 25
         UNION ALL SELECT 25, 26
         UNION ALL SELECT 26, 27
         UNION ALL SELECT 27, 28
         UNION ALL SELECT 28, 29
         UNION ALL SELECT 29, 30
     ) d
         CROSS JOIN (
    SELECT 9301 AS schedule_id, TIME('07:30:00') AS slot_time
    UNION ALL SELECT 9302, TIME('12:45:00')
    UNION ALL SELECT 9303, TIME('20:30:00')
) s;


