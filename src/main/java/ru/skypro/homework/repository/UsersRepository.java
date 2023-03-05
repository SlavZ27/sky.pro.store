package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.User;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from users where username=:login"
            , nativeQuery = true)
    Optional<User> findByUsername(String login);

    @Query(value = "select count(*) from users"
            , nativeQuery = true)
    int getCount();

    @Query(value = "SELECT CASE WHEN EXISTS(SELECT a.* FROM ads as a, users as u WHERE a.id=:idAds and a.id_author=u.id  and u.username=:username) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END"
            , nativeQuery = true)
    boolean isAdsAuthor(Integer idAds, String username);

    @Query(value = "SELECT CASE WHEN EXISTS(SELECT c.* FROM comment as c , users as u WHERE c.id=:idComment and c.id_author=u.id  and u.username=:username) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END"
            , nativeQuery = true)
    boolean isCommentAuthor(Integer idComment, String username);
}
