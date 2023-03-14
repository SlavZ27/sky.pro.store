package ru.skypro.homework.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "Authorities")
@Validated
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "username", nullable = false)
    @NotNull
    private String username;
    @Column(name = "authority", nullable = false)
    @NotNull
    private String authority;

}
