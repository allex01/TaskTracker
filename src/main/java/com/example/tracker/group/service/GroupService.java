package com.example.tracker.group.service;

import com.example.tracker.group.entity.Group;
import com.example.tracker.group.entity.GroupMember;
import com.example.tracker.group.repo.GroupMemberRepository;
import com.example.tracker.group.repo.GroupRepository;
import com.example.tracker.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional
    public Group createGroup(String name, User owner) {
        Group group = new Group();
        group.setName(name);
        group.setOwner(owner);
        group = groupRepository.save(group);

        // Автоматически добавляем владельца как участника с ролью OWNER
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(owner);
        member.setRole("OWNER");
        groupMemberRepository.save(member);

        // Перезагружаем группу с owner для корректного отображения
        return groupRepository.findByIdWithOwner(group.getId());
    }

    public Group findById(UUID id) {
        return groupRepository.findByIdWithOwner(id);
    }
}

