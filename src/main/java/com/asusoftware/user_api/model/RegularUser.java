package com.asusoftware.user_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "regular_users")
@EqualsAndHashCode(callSuper = true)
public class RegularUser extends User {

    @Column(name = "subscription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    // Alte câmpuri specifice pentru utilizatorii obișnuiți, dacă este cazul
}
