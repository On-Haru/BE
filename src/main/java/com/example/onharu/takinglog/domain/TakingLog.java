package com.example.onharu.takinglog.domain;

import com.example.onharu.medicineschedule.domain.MedicineSchedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "taking_logs")
public class TakingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MedicineSchedule schedule;

    @Column(name = "scheduled_datetime", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime scheduledDateTime;

    @Column(name = "taken_datetime", columnDefinition = "DATETIME(0)")
    private LocalDateTime takenDateTime;

    @Column(name = "is_taken", nullable = false)
    private boolean taken;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    private TakingLog(MedicineSchedule schedule, LocalDateTime scheduledDateTime,
            LocalDateTime takenDateTime,
            boolean taken, Integer delayMinutes) {
        this.schedule = schedule;
        this.scheduledDateTime = scheduledDateTime;
        this.takenDateTime = takenDateTime;
        this.taken = taken;
        this.delayMinutes = delayMinutes;
    }

    public static TakingLog create(MedicineSchedule schedule, LocalDateTime scheduledDateTime,
            LocalDateTime takenDateTime, Boolean taken, Integer delayMinutes) {
        return new TakingLog(schedule, scheduledDateTime, takenDateTime, taken, delayMinutes);
    }

    public static TakingLog testInstance(Long id, MedicineSchedule schedule) {
        TakingLog log = new TakingLog(schedule, LocalDateTime.now(), LocalDateTime.now(), true, 0);
        log.id = id;
        return log;
    }

    public void markAsTaken() {
        this.taken = true;
        this.takenDateTime = LocalDateTime.now();
        this.delayMinutes = (int) java.time.Duration.between(scheduledDateTime, takenDateTime)
                .toMinutes();
    }
}
