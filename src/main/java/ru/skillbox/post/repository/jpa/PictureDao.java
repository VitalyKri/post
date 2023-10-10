package ru.skillbox.post.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.post.entity.Picture;

import java.util.UUID;

public interface PictureDao extends JpaRepository<Picture, UUID> {
}
