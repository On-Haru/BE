package com.example.onharu.alarm.domain;

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
@Table(name = "alarm_histories")
public class AlarmHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private MedicineSchedule schedule;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    private AlarmHistory(MedicineSchedule schedule, String channel, LocalDateTime sentAt, boolean success, String failureReason) {
        this.schedule = schedule;
        this.channel = channel;
        this.sentAt = sentAt;
        this.success = success;
        this.failureReason = failureReason;
    }

    public static AlarmHistory create(MedicineSchedule schedule, String channel, LocalDateTime sentAt,
                                      boolean success, String failureReason) {
        return new AlarmHistory(schedule, channel, sentAt, success, failureReason);
    }

    public static AlarmHistory testInstance(Long id, MedicineSchedule schedule) {
        AlarmHistory history = new AlarmHistory(schedule, "PUSH", LocalDateTime.now(), true, null);
        history.id = id;
        return history;
    }
}
