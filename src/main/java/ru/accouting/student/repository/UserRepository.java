package ru.accouting.student.repository;

import org.springframework.data.repository.CrudRepository;
import ru.accouting.student.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByLogin(String login);
    List<User> findAll();

    boolean existsByLogin(String login);
}
