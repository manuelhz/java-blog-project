package org.bytebounty.app.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long roleId;

    private String name;

    @ManyToMany
    @JoinTable(
        name = "role_privileges",
        joinColumns = @JoinColumn(
            name = "role_id", referencedColumnName = "roleId"
        ),
        inverseJoinColumns = @JoinColumn(
            name = "privilege_id", referencedColumnName = "privilegeId"
        )
    )
    private Set<Privilege> privileges = new HashSet<>();

    public Role(String name, Set<Privilege> privileges) {
        this.name = name;
        this.privileges = privileges;
    }


    
}
