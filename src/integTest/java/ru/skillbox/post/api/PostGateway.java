package ru.skillbox.post.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.post.dto.PostDto;

import java.util.List;

@Component
@FeignClient(name = "${Mytest.blog-feignClient.user.name}",
        url = "${Mytest.blog-feignClient.url}" + "${Mytest.blog-feignClient.user.url}")
public interface PostGateway {

    @GetMapping(path = "/{postUuid}")
    ResponseEntity<PostDto> getPostById(@PathVariable String postUuid);

    @PostMapping(path = "")
    ResponseEntity<String> savePost(@RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                            @RequestPart(value = "sourceContent", required = false) MultipartFile source,
                            @RequestPart(value = "info", required = false) PostDto postDto);


    @PostMapping(path = "/{postUuid}", consumes = "multipart/form-data")
    ResponseEntity<String> updatePost(@RequestPart(value = "photos", required = false) List<MultipartFile> photos,
                              @RequestPart(value = "sourceContent") MultipartFile source,
                              @RequestPart("info") PostDto postDto);

}
