package ru.skillbox.post.config;


import ru.skillbox.post.dto.PictureDto;
import ru.skillbox.post.dto.PostDto;

import java.util.List;
import java.util.UUID;

public class TestConstants {

    public static final PostDto POSTDTO;
    static {
        POSTDTO = PostDto.builder()
                .id(UUID.randomUUID())
                .linkContent("")
                .description("описание")
                .title("Заголовок")
                .userId(UUID.randomUUID())
                .build();
        PictureDto photo1 = PictureDto.builder()
                .id(UUID.randomUUID())
                .name("photos1.md")
                .link("")
                .userId(POSTDTO.getUserId())
                .build();
        PictureDto photo2 = PictureDto.builder()
                .id(UUID.randomUUID())
                .name("photos2.md")
                .link("")
                .userId(POSTDTO.getUserId())
                .build();
        POSTDTO.setPictures(List.of(photo1,photo2));
    }


}
