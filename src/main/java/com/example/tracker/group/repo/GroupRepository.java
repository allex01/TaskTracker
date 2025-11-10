package com.example.tracker.group.repo;

import com.example.tracker.group.entity.Group;
import com.example.tracker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.owner WHERE g.owner = :owner")
    List<Group> findByOwner(@Param("owner") User owner);

    @Query("SELECT g FROM Group g LEFT JOIN FETCH g.owner WHERE g.id = :id")
    Group findByIdWithOwner(@Param("id") UUID id);
}

