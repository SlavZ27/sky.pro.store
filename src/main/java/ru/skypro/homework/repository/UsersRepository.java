package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users where first_name=:firstName and  last_name=:lastName limit 1"
            , nativeQuery = true)
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    @Query(value = "select count(*) from users"
            , nativeQuery = true)
    int getCount();
}
