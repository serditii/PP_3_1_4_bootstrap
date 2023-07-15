package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    @Query(value = "from User d left join fetch d.roles where d.username=:username ")
    User findByUsername(String username);
}
