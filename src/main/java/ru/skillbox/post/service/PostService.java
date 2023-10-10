package ru.skillbox.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.post.entity.Post;
import ru.skillbox.post.repository.jpa.PostDao;
import ru.skillbox.post.repository.s3.ContentRepository;
import ru.skillbox.post.repository.s3.SourceRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDao;

    private final ProcessingS3Service processingService;
    private final StorageService contentStorage;
    private final StorageService sourceStorage;

    @Autowired
    public PostService(PostDao postDao, ProcessingS3Service processingService,
                       ContentRepository contentRepository, SourceRepository sourceRepository) {
        this.postDao = postDao;
        this.processingService = processingService;
        this.contentStorage = new StorageService(contentRepository);
        this.sourceStorage = new StorageService(sourceRepository);
    }

    public Post getPostByUuid(String uuid) {
        return postDao.findById(UUID.fromString(uuid)).orElseThrow();
    }

    public void savePost(Post post, MultipartFile source, List<MultipartFile> photos) throws IOException {
        Post postInBase = postDao.findById(post.getId()).orElse(post);
        if (postInBase.getId() != null) {
            removeMissingPicture(post);
        }
        postInBase = postDao.save(postInBase);
        createLink(postInBase);
        savePostContentAsHtml(postInBase, source);
        savePostSource(postInBase, source);
        if (photos.size() > 0) {
            savePhotoContent(postInBase, photos);
        }
    }

    private void removeMissingPicture(Post post) {
        var photosPrefix = linkWithPrefixPhoto(post.getId().toString());
        var allFilesByPrefix = contentStorage.getAllFilesByPrefix(photosPrefix);
        var deleteTasks = allFilesByPrefix.stream()
                .filter(s -> isContainedInPost(post, s))
                .map(this::deletePicture)
                .map((CompletableFuture::supplyAsync))
                .toList();
        CompletableFuture.allOf(deleteTasks.toArray(new CompletableFuture[0])).join();
    }

    private boolean isContainedInPost(Post post, String key) {
        return post.getPictures().stream()
                .anyMatch(picture -> key.contains(picture.getId().toString()));
    }

    private Supplier<Boolean> deletePicture(String key) {
        return () -> {
            try {
                contentStorage.deleteObject(key);
                return true;
            } catch (Exception e) {
                log.error("Can not delete photos  {}", key, e);
            }
            return false;
        };
    }

    private void createLink(Post post) {
        post.setLinkContent(contentStorage.getEndpoint()
                .concat("/")
                .concat(post.getId().toString()));
        post.setLinkSource(sourceStorage.getEndpoint()
                .concat("/")
                .concat(post.getId().toString()));
        post.getPictures()
                .forEach(picture -> picture.setLink(
                        linkWithPrefixPhoto(post.getLinkContent())
                                .concat(picture.getId().toString())
                ));
    }

    private void savePostContentAsHtml(Post post, MultipartFile source) throws IOException {
        sourceStorage.putObject(post.getId().toString(), source);
        processingService.savePostAsHtml(post, source);
    }

    private void savePostSource(Post post, MultipartFile source) throws IOException {
        sourceStorage.putObject(post.getId().toString(), source);
    }

    private void savePhotoContent(Post post, List<MultipartFile> photos) {
        var createPtohosTasks = photos.stream()
                .map(file -> savePhotos(post, file))
                .map(CompletableFuture::supplyAsync)
                .toList();
        CompletableFuture.allOf(createPtohosTasks.toArray(new CompletableFuture[0])).join();
    }

    private Supplier<Boolean> savePhotos(Post post, MultipartFile multipartFile) {
        return () -> {
            try {
                var first = post.getPictures()
                        .stream()
                        .filter(picture -> picture.getName().equals(multipartFile.getOriginalFilename()))
                        .findAny().orElseThrow();
                String linkPhoto = linkWithPrefixPhoto(post.getId().toString()) + (first.getId().toString());
                contentStorage.putObject(linkPhoto, multipartFile);
                return true;
            } catch (Exception e) {
                log.error("Can not save photos from post {} user {}", multipartFile.getOriginalFilename(), post.getUser().getId(), e);
            }
            return false;
        };
    }

    private String linkWithPrefixPhoto(String link) {
        return link.concat("/photos/");
    }

}
