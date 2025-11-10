package com.example.tracker.group.repo;

import com.example.tracker.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    @Query("SELECT gm FROM GroupMember gm LEFT JOIN FETCH gm.user WHERE gm.group.id = :groupId")
    List<GroupMember> findByGroupId(@Param("groupId") UUID groupId);
}

