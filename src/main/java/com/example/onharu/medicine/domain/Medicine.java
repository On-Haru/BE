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

    private Medicine(Prescription prescription, String name, int dailyDoseCount, String administrationMethod, String memo) {
        this.prescription = prescription;
        this.name = name;
        this.dailyDoseCount = dailyDoseCount;
        this.administrationMethod = administrationMethod;
        this.memo = memo;
    }

    public static Medicine create(Prescription prescription, String name, int dailyDoseCount, String administrationMethod, String memo) {
        return new Medicine(prescription, name, dailyDoseCount, administrationMethod, memo);
    }

    public static Medicine testInstance(Long id, Prescription prescription) {
        Medicine medicine = new Medicine(prescription, "TestMed", 2, "ORAL", "none");
        medicine.id = id;
        return medicine;
    }
}
