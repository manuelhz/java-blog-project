package org.bytebounty.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long passwordId;

    @NotEmpty(message = "Password missing")
    private char[] word;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    public Password(char[] word) {
        this.word = word;
    }
    
}
