package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.validation.annotation.Validated;
import ru.skypro.homework.validate.ValidPhone;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users")
@Validated
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "email")
    @Email(message = "Username has invalid format: ${validatedValue}")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    @ValidPhone
    private String phone;
    @Column(name = "reg_date")
    @PastOrPresent
    @NotNull
    private LocalDate regDate;
    @OneToOne
    @JoinColumn(name = "id_avatar")
    private Avatar avatar;
    @Column(name = "password")
    @NotNull
    private String password;
    @Email(message = "Username has invalid format: ${validatedValue}")
    @Column(name = "username")
    @NotNull
    private String username;
    @Column(name = "enabled")
    @NotNull
    private Boolean enabled;

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
