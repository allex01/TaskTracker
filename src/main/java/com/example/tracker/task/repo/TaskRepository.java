package com.example.tracker.task.repo;

import com.example.tracker.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.group LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignee WHERE t.group.id = :groupId ORDER BY t.createdAt DESC")
    List<Task> findByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.group LEFT JOIN FETCH t.createdBy LEFT JOIN FETCH t.assignee WHERE t.id = :id")
    Task findByIdWithRelations(@Param("id") UUID id);
}

