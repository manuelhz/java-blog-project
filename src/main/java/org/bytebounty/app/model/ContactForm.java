package org.bytebounty.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContactForm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long contactId;

    @NotEmpty(message = "The field name is required")
    private String name;


    @Email(message = "Invalid email")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Please enter a subject")
    private String subject;

    @NotEmpty(message = "Please enter a message")
    @Column(length = 3000)
    private String message;    
}
