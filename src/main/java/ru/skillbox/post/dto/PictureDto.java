package ru.skillbox.post.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.UUID;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PictureDto implements Serializable {

    static final long serialVersionUID = -8853804012729036215L;

    private UUID id;

    private String link;

    private String name;

    @JsonBackReference
    private PostDto postDto;

    @NotBlank
    private UUID userId;

}
