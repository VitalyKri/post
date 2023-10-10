package ru.skillbox.post.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.post.dto.PostDto;
import ru.skillbox.post.dto.mapping.PostMapper;
import ru.skillbox.post.service.PostService;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/post")
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @Operation(summary = "Получить пост по ID")
    @GetMapping(path = "/{postUuid}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String postUuid) {
        PostDto postDto = postMapper.toPostDto(postService.getPostByUuid(postUuid));
        return ResponseEntity.ok(postDto);
    }

    @Operation(summary = "Добавление поста")
    @PostMapping(path = "")
    public ResponseEntity<String> savePost(@RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                                   @RequestPart(value = "sourceContent", required = false) MultipartFile source,
                                   @RequestPart(value = "info", required = false) PostDto postDto) throws IOException {
        postService.savePost(postMapper.toPost(postDto), source, photos);
        return ResponseEntity.ok("Пост сохранен");
    }

    @Operation(summary = "Добавление поста")
    @PostMapping(path = "/{postUuid}", consumes = "multipart/form-data")
    public ResponseEntity<String> updatePost(@RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                                     @RequestPart(value = "sourceContent") MultipartFile source,
                                     @RequestPart("info") PostDto postDto) throws IOException {
        postService.savePost(postMapper.toPost(postDto), source, photos);
        return ResponseEntity.ok("Пост обновлен");
    }

}
