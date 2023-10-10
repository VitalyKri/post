package ru.skillbox.post.repository.jpa;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.post.entity.Post;

import java.util.Optional;
import java.util.UUID;

public interface PostDao extends JpaRepository<Post, UUID> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = "pictures")
    @NonNull
    Optional<Post> findById(@NonNull UUID id);

}
