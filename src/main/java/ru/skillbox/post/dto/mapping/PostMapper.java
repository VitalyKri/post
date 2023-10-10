package ru.skillbox.post.dto.mapping;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.post.dto.PostDto;
import ru.skillbox.post.entity.Post;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface PostMapper {

    @Mapping(source = "user.id", target = "userId")
    PostDto toPostDto(Post post);

    @Mapping(source = "userId", target = "user.id")
    Post toPost(PostDto postDto);


}
