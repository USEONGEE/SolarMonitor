package com.energy.outsourcing.repository;

import com.energy.outsourcing.entity.NotificationPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationPostRepository extends JpaRepository<NotificationPost, Long> {
    Page<NotificationPost> findAll(Pageable pageable);
}
