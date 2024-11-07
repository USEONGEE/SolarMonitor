package com.energy.outsourcing.service;

import com.energy.outsourcing.entity.NotificationPost;
import com.energy.outsourcing.repository.NotificationPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationPostService {
    private final NotificationPostRepository notificationPostRepository;

    public Page<NotificationPost> getPosts(Pageable pageable) {
        return notificationPostRepository.findAll(pageable);
    }
}
