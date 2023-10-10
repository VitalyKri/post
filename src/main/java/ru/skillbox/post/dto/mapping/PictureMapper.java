package ru.skillbox.post.dto.mapping;


import org.mapstruct.Mapper;
import ru.skillbox.post.dto.PictureDto;
import ru.skillbox.post.entity.Picture;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface PictureMapper {

    PictureDto toPictureDto(Picture picture);

    Picture toPicture(PictureDto pictureDto);
}
