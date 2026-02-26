package com.example.bankcards.entity;

import com.example.bankcards.enums.UserStatus;
import com.example.bankcards.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.id.factory.spi.GenerationTypeStrategy;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Entity
@Table(name="users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @Column(name="id")
    private long id;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String passwordHash;
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "roles", columnDefinition = "jsonb", nullable = false)
    private List<Role> roles;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Card> cards;
    @Version
    private int version;
}
