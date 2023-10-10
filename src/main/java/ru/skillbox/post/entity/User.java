package ru.skillbox.post.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.skillbox.post.entity.common.InfoEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "users")
@SuperBuilder
public class User extends InfoEntity {

    @OneToMany(mappedBy = "user")
    private List<Post> products;
}
