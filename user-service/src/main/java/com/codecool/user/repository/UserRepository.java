package com.codecool.user.repository;

import com.codecool.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsernameOrEmail(String username, String email);
    

    @Query("SELECT u.id from UserEntity u where u.username like :userName")
    int getUserId(@Param("userName") String userName);

    @Query("update  UserEntity u set u.password = :password where u.username LIKE :userName")
    @Modifying(clearAutomatically = true)
    void updatePassword(@Param("password") String password, @Param("userName") String userName);

    @Query("update  UserEntity u set u.email = :email where u.username LIKE :userName")
    @Modifying(clearAutomatically = true)
    void updateEmail(@Param("email") String email, @Param("userName") String userName);

    UserEntity findByUsername(String username);
}
