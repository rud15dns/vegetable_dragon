package com.example.vegetabledragon.controller;

import com.example.vegetabledragon.apiPayload.code.status.ErrorStatus;
import com.example.vegetabledragon.apiPayload.exception.GeneralException;
import com.example.vegetabledragon.domain.Category;
import com.example.vegetabledragon.domain.Post;
import com.example.vegetabledragon.dto.PostRequest;
import com.example.vegetabledragon.exception.*;
import com.example.vegetabledragon.repository.CategoryRepository;
import com.example.vegetabledragon.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@Slf4j
@RequiredArgsConstructor
public class PostController {
    private final PostService postService; // PostImpl 에서 PostService -> 상위 인터페이스를 참조하도록 변경.
    private final CategoryRepository categoryRepository;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest request, HttpSession session) {
        // 카테고리 값 확인

        String loggedInUser = (String) session.getAttribute("loggedInUser");

        Category category = categoryRepository.findByName(request.getCategory());

        if (loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 반환
        }
        Post savedPost = postService.createPost(loggedInUser, request, category);

        return ResponseEntity.ok(savedPost);
    }

    // 게시글 목록 조회(페이지네이션)
    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
//        log.info("[PostController] 게시글 목록 조회 - 페이지 : {}, 사이즈 : {}", page, size);
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
//        log.info("[PostController] 특정 게시글 조회 - 게시글 ID: {}", postId);
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND));
    }

    // 카테고리별 게시글 조회
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Post>> getPostsByCategory(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "5") int limit) {  // 기본값을 5로 설정
        Category category = categoryRepository.findByName(categoryName);

        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 카테고리가 없으면 404 반환
        }

        List<Post> posts = postService.getPostsByCategory(category, limit);  // 서비스에서 카테고리별 게시글 조회
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String title) {
        List<Post> results = postService.searchPostsByTitle(title);
        return ResponseEntity.ok(results);
    }


    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long postId, HttpSession session){
        postService.deletePostById(postId, session);
        return ResponseEntity.noContent().build();
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePostById(@PathVariable Long postId, @RequestBody PostRequest request, HttpSession session) {
        Post updatedPost = postService.updatePost(postId, request, session);
        return ResponseEntity.ok(updatedPost);
    }



}