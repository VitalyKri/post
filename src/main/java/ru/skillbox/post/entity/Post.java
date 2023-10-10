package ru.skillbox.post.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.skillbox.post.entity.common.InfoEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "posts")
@SuperBuilder
public class Post extends InfoEntity {

    @Column
    private String title;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String linkSource;

    @Column
    private String linkContent;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL})
    private List<Picture> pictures;
}
