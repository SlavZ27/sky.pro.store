package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Avatar;

/**
 * The interface Avatar repository.
 */
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

}
