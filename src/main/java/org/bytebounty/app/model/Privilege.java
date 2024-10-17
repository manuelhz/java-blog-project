package org.bytebounty.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long privilegeId;

    private String name;
        
    public Privilege(String name) {
        this.name = name;
    }

}
