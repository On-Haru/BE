package com.example.onharu.medicine.domain;

import com.example.onharu.prescription.domain.Prescription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "medicine_name", nullable = false, length = 80)
    private String name;

    @Column(name = "daily_dose_count", nullable = false)
    private int dailyDoseCount;

    @Column(name = "administration_method", length = 40)
    private String administrationMethod;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "ai_description", length = 200)
    private String aiDescription;

    private Medicine(Prescription prescription, String name, int dailyDoseCount,
            String administrationMethod, String memo,
            Integer totalCount, Integer durationDays, String aiDescription) {
        this.prescription = prescription;
        this.name = name;
        this.dailyDoseCount = dailyDoseCount;
        this.administrationMethod = administrationMethod;
        this.memo = memo;
        this.totalCount = totalCount;
        this.durationDays = durationDays;
        this.aiDescription = aiDescription;
    }

    public static Medicine create(Prescription prescription, String name, int dailyDoseCount,
            String administrationMethod, String memo) {
        return new Medicine(prescription, name, dailyDoseCount, administrationMethod, memo,
                null, null, null);
    }

    public static Medicine createWithDetails(Prescription prescription, String name, int dailyDoseCount,
            String administrationMethod, String memo, Integer totalCount,
            Integer durationDays, String aiDescription) {
        return new Medicine(prescription, name, dailyDoseCount, administrationMethod, memo,
                totalCount, durationDays, aiDescription);
    }
}
