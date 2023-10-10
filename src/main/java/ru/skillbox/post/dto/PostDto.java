package ru.skillbox.post.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto implements Serializable {

    static final long serialVersionUID = 894090523080620426L;

    private UUID id;

    private String title;

    private String description;

    private String linkContent;

    @JsonManagedReference
    private List<PictureDto> pictures;

    private UUID userId;

}
