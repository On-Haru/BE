package com.example.onharu.medicineschedule.domain;

import com.example.onharu.medicine.domain.Medicine;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "medicine_schedules")
public class MedicineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private ScheduleType scheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_type", nullable = false, length = 20)
    private RepeatType repeatType;

    @Column(name = "days_bitmask")
    private Integer daysBitmask;

    @Column(name = "notify_time", nullable = false)
    private LocalTime notifyTime;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private MedicineSchedule(Medicine medicine, ScheduleType scheduleType, RepeatType repeatType,
            Integer daysBitmask, LocalTime notifyTime,
            LocalDate startDate, LocalDate endDate) {
        this.medicine = medicine;
        this.scheduleType = scheduleType;
        this.repeatType = repeatType;
        this.daysBitmask = daysBitmask;
        this.notifyTime = notifyTime;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static MedicineSchedule create(Medicine medicine, ScheduleType scheduleType, RepeatType repeatType,
            Integer daysBitmask, LocalTime notifyTime,
            LocalDate startDate, LocalDate endDate) {
        return new MedicineSchedule(medicine, scheduleType, repeatType, daysBitmask, notifyTime,
                startDate, endDate);
    }

    public static MedicineSchedule testInstance(Long id, Medicine medicine) {
        MedicineSchedule schedule = new MedicineSchedule(
                medicine,
                ScheduleType.MORNING,
                RepeatType.DAILY,
                127,
                LocalTime.of(8, 0),
                LocalDate.now(),
                LocalDate.now().plusMonths(1)
        );
        schedule.id = id;
        return schedule;
    }
}
