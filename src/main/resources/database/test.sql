-- (선택) 기존 더미 데이터 정리
use sa;

DELETE
FROM taking_logs
WHERE schedule_id IN (9001, 9002, 9003)
  AND scheduled_datetime BETWEEN '2025-11-01 00:00:00' AND '2025-11-30 23:59:59';

-- 기본 엔터티
INSERT INTO users (id, user_name, phone, year, password, code, role)
VALUES (1001, '김온하', '01011112222', '1955', '{noop}temp1234', 123456, 'SENIOR')
ON DUPLICATE KEY UPDATE user_name = VALUES(user_name);

INSERT INTO prescriptions (id, senior_id, issued_date, hospital_name, doctor_name, note)
VALUES (5001, 1001, '2025-10-25', '온하루내과', '박진우', '11월 복약 플랜')
ON DUPLICATE KEY UPDATE note = VALUES(note);

INSERT INTO medicines (id, prescription_id, medicine_name, daily_dose_count,
                       administration_method, memo, total_count, duration_days, ai_description)
VALUES (7001, 5001, '고혈압약A', 1, '정제', '아침 복용', NULL, NULL, NULL),
       (7002, 5001, '당뇨약B', 1, '정제', '점심 복용', NULL, NULL, NULL),
       (7003, 5001, '비타민C', 1, '정제', '저녁 복용', NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE memo = VALUES(memo);

INSERT INTO medicine_schedules (id, medicine_id, schedule_type, repeat_type,
                                days_bitmask, notify_time, start_date, end_date)
VALUES (9001, 7001, 'MORNING', 'DAILY', 127, '07:00:00', '2025-10-25', '2025-12-31'),
       (9002, 7002, 'LUNCH', 'DAILY', 127, '12:30:00', '2025-10-25', '2025-12-31'),
       (9003, 7003, 'EVENING', 'DAILY', 127, '20:00:00', '2025-10-25', '2025-12-31')
ON DUPLICATE KEY UPDATE notify_time = VALUES(notify_time);

-- 2025년 11월 TakingLog 더미
INSERT INTO taking_logs (schedule_id, scheduled_datetime, taken_datetime, is_taken, delay_minutes)
SELECT s.schedule_id,
       TIMESTAMP(DATE_ADD(DATE('2025-11-01'), INTERVAL d.day_offset DAY),
                 s.slot_time) AS scheduled_dt,
       CASE
           WHEN d.day_no <= 10 THEN
               DATE_ADD(TIMESTAMP(DATE_ADD(DATE('2025-11-01'), INTERVAL d.day_offset DAY),
                                  s.slot_time), INTERVAL 5 MINUTE)
           WHEN d.day_no BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003) THEN
               DATE_ADD(TIMESTAMP(DATE_ADD(DATE('2025-11-01'), INTERVAL d.day_offset DAY),
                                  s.slot_time), INTERVAL 15 MINUTE)
           ELSE NULL
           END                AS taken_dt,
       CASE
           WHEN d.day_no <= 10 THEN 1
           WHEN d.day_no BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003) THEN 1
           ELSE 0
           END                AS is_taken,
       CASE
           WHEN d.day_no <= 10 THEN 5
           WHEN d.day_no BETWEEN 11 AND 20 AND s.schedule_id IN (9001, 9003) THEN 15
           ELSE NULL
           END                AS delay_minutes
FROM (SELECT 0 AS day_offset, 1 AS day_no
      UNION ALL
      SELECT 1, 2
      UNION ALL
      SELECT 2, 3
      UNION ALL
      SELECT 3, 4
      UNION ALL
      SELECT 4, 5
      UNION ALL
      SELECT 5, 6
      UNION ALL
      SELECT 6, 7
      UNION ALL
      SELECT 7, 8
      UNION ALL
      SELECT 8, 9
      UNION ALL
      SELECT 9, 10
      UNION ALL
      SELECT 10, 11
      UNION ALL
      SELECT 11, 12
      UNION ALL
      SELECT 12, 13
      UNION ALL
      SELECT 13, 14
      UNION ALL
      SELECT 14, 15
      UNION ALL
      SELECT 15, 16
      UNION ALL
      SELECT 16, 17
      UNION ALL
      SELECT 17, 18
      UNION ALL
      SELECT 18, 19
      UNION ALL
      SELECT 19, 20
      UNION ALL
      SELECT 20, 21
      UNION ALL
      SELECT 21, 22
      UNION ALL
      SELECT 22, 23
      UNION ALL
      SELECT 23, 24
      UNION ALL
      SELECT 24, 25
      UNION ALL
      SELECT 25, 26
      UNION ALL
      SELECT 26, 27
      UNION ALL
      SELECT 27, 28
      UNION ALL
      SELECT 28, 29
      UNION ALL
      SELECT 29, 30) d
         CROSS JOIN (SELECT 9001 AS schedule_id, TIME('07:00:00') AS slot_time
                     UNION ALL
                     SELECT 9002, TIME('12:30:00')
                     UNION ALL
                     SELECT 9003, TIME('20:00:00')) s;