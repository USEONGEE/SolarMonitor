package com.energy.outsourcing.controller;

import com.energy.outsourcing.dto.NotificationResponseDto;
import com.energy.outsourcing.entity.NotificationPost;
import com.energy.outsourcing.service.NotificationPostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notification-posts")
public class NotificationPostController {

    private final NotificationPostService notificationPostService;

    @GetMapping
    public Page<NotificationResponseDto> getAllPosts(
            @PageableDefault(page = 0, size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return notificationPostService.getPosts(pageable)
                .map(post -> new NotificationResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getMember().getName(),
                        post.getCreatedDate()
                ));
    }
}
