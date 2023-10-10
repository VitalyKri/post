package ru.skillbox.post.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.post.entity.User;

import java.util.UUID;

public interface UserDao extends JpaRepository<User, UUID> {
}
