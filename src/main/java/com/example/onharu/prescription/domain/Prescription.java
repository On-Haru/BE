package com.example.onharu.prescription.domain;

import com.example.onharu.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "hospital_name", length = 100)
    private String hospitalName;

    @Column(name = "doctor_name", length = 60)
    private String doctorName;

    @Column(name = "note", length = 200)
    private String note;

    private Prescription(User senior, LocalDate issuedDate, String hospitalName,
            String doctorName, String note) {
        this.senior = senior;
        this.issuedDate = issuedDate;
        this.hospitalName = hospitalName;
        this.doctorName = doctorName;
        this.note = note;
    }

    public static Prescription create(User senior, LocalDate issuedDate, String hospitalName,
            String doctorName, String note) {
        return new Prescription(senior, issuedDate, hospitalName, doctorName, note);
    }

}
