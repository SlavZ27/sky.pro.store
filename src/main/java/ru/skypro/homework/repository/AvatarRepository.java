package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.Image;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

}
