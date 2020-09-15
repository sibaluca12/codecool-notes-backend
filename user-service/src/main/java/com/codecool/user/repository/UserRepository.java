package com.codecool.user.repository;

import com.codecool.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsernameOrEmail(String username, String email);

    UserEntity findByUsername(String username);

    @Query("SELECT u.id from UserEntity u where u.username like :userName")
    int getUserId(@Param("userName") String userName);
}
