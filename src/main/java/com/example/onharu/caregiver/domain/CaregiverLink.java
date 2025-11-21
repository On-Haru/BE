package com.example.onharu.caregiver.domain;

import com.example.onharu.user.domain.User;
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
@Table(name = "caregiver_links")
public class CaregiverLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private User caregiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;

    private CaregiverLink(User caregiver, User senior) {
        this.caregiver = caregiver;
        this.senior = senior;
    }

    public static CaregiverLink create(User caregiver, User senior) {
        return new CaregiverLink(caregiver, senior);
    }

    public static CaregiverLink testInstance(Long id, User caregiver, User senior) {
        CaregiverLink link = new CaregiverLink(caregiver, senior);
        link.id = id;
        return link;
    }
}
