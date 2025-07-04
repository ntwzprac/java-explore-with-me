package ru.practicum.mainservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.event.id = :eventId ORDER BY " +
            "CASE WHEN c.updatedOn IS NOT NULL THEN c.updatedOn ELSE c.createdOn END")
    Page<Comment> findByEventIdOrderByUpdateTimeOrCreateTime(Long eventId, Pageable pageable);
}
