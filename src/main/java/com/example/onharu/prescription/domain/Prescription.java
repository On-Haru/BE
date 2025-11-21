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

    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @Column(name = "doctor_name", length = 60)
    private String doctorName;

    @Column(name = "note", length = 200)
    private String note;

    private Prescription(User senior, LocalDate issuedDate, LocalDate expiredDate, String doctorName, String note) {
        this.senior = senior;
        this.issuedDate = issuedDate;
        this.expiredDate = expiredDate;
        this.doctorName = doctorName;
        this.note = note;
    }

    public static Prescription create(User senior, LocalDate issuedDate, LocalDate expiredDate, String doctorName, String note) {
        return new Prescription(senior, issuedDate, expiredDate, doctorName, note);
    }

    public static Prescription testInstance(Long id, User senior) {
        Prescription prescription = new Prescription(senior, LocalDate.now(), LocalDate.now().plusMonths(1), "Dr. Test", "note");
        prescription.id = id;
        return prescription;
    }
}
