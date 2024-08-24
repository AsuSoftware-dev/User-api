package com.asusoftware.user_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactPerson {

    @Column(name = "contact_first_name")
    private String firstName;

    @Column(name = "contact_last_name")
    private String lastName;

    @Column(name = "contact_email", nullable = false)
    private String email;

    @Column(name = "contact_phone_number")
    private String phoneNumber;
}