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

    @Query(value = "SELECT CASE WHEN EXISTS(select * from ads as a inner join users as u on a.id = :idAds and a.id_author = u.id and u.username = :username)THEN TRUE ELSE FALSE END"
            , nativeQuery = true)
    boolean isAdsAuthor(Integer idAds, String username);

    @Query(value = "SELECT CASE WHEN EXISTS(select * from comment as c inner join users as u on c.id = :idComment and c.id_author = u.id and u.username = :username)THEN TRUE ELSE FALSE END"
            , nativeQuery = true)
    boolean isCommentAuthor(Integer idComment, String username);
}
