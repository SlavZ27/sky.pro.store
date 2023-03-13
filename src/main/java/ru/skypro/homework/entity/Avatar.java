package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Validated
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "path")
    @NotNull
    private String path;

    public Avatar(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Avatar avatar = (Avatar) o;
        return id != null && Objects.equals(id, avatar.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
