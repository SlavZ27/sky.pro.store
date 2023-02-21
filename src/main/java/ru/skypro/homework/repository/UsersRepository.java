package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users where username=:login and  password=:pas limit 1"
            , nativeQuery = true)
    Optional<User> findByUsernameAndPas(String login, String pas);

    @Query(value = "select count(*) from users"
            , nativeQuery = true)
    int getCount();
}
