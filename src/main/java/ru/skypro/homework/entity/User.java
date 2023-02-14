package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    private String phone;
    @Column(name = "reg_date")
    private LocalDate regDate;
    @OneToOne
    @JoinColumn(name = "id_avatar")
    private Avatar avatar;
    @Column(name = "password")
    private String password;
    @Column(name = "username")
    private String username;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String email,
                String firstName,
                String lastName,
                String phone,
                LocalDate regDate,
                Avatar avatar,
                String password,
                String username,
                Role role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.regDate = regDate;
        this.avatar = avatar;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
