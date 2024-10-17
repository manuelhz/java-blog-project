package org.bytebounty.app.model;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {       

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long accountId;
    
    @NotEmpty(message = "The field name is required")
    private String firstName;

    private String lastName;

    private String mobile;

    @Email(message = "Invalid email")
    @NotEmpty(message = "Email is required")
    @Column(unique=true)
    private String email;

    private String website;

    @OneToOne
    @JoinColumn(name = "password_id", referencedColumnName = "passwordId", nullable = true)
    private Password password;

    private LocalDateTime registeredAt;

    private LocalDateTime lastLogin;

    private String profilePicture;

    @NotEmpty(message = "User must have roles")
    @ManyToMany
    @JoinTable(
        name = "accounts_roles",
        joinColumns = @JoinColumn(
            name = "account_id", referencedColumnName = "accountId"            
        ),
        inverseJoinColumns = @JoinColumn(
            name = "role_id", referencedColumnName = "roleId")
    )
    private Set<Role> roles = new HashSet<>();

    private boolean disabled;

    @Column(length = 5000)
    private String about;

    public String fullName() {
        return this.firstName + " " + this.lastName;
    }

    public String roles() {
        String roles = "";
        int count = 0;
        int size = this.roles.size();
        for (Role role: this.roles) {
            if(++count==size) {
                roles = roles + role.getName();
            } else {
                roles = roles + role.getName() + " ";
            }            
        }
        return roles;
    }

    public Account() {

        this.profilePicture = "/img/userImg/default.svg";    

    }
    
    
    public boolean hasPrivilege(Privilege privilege) {

        for (Role role: this.roles) {
            if(role.getPrivileges().contains(privilege))
                return true;
        }
        return false;
    }    
}
