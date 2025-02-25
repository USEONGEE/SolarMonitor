package com.example.web.repository;

import com.example.web.entity.NotificationPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationPostRepository extends JpaRepository<NotificationPost, Long> {
    Page<NotificationPost> findAll(Pageable pageable);
}
