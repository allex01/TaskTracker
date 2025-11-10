package com.example.tracker.task.service;

import com.example.tracker.group.entity.Group;
import com.example.tracker.group.service.GroupService;
import com.example.tracker.task.entity.Task;
import com.example.tracker.task.entity.TaskPriority;
import com.example.tracker.task.entity.TaskStatus;
import com.example.tracker.task.repo.TaskRepository;
import com.example.tracker.user.entity.User;
import com.example.tracker.user.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final GroupService groupService;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, GroupService groupService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.groupService = groupService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Task createTask(UUID groupId, String title, String description, TaskPriority priority, UUID assigneeId, User createdBy) {
        Group group = groupService.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        Task task = new Task();
        task.setGroup(group);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority != null ? priority : TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.TODO);
        task.setCreatedBy(createdBy);

        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId).orElse(null);
            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public List<Task> findByGroupId(UUID groupId) {
        return taskRepository.findByGroupId(groupId);
    }

    public Task findById(UUID id) {
        return taskRepository.findByIdWithRelations(id);
    }

    @Transactional
    public Task updateTask(UUID id, String title, String description, TaskPriority priority, TaskStatus status, UUID assigneeId) {
        Task task = taskRepository.findByIdWithRelations(id);
        if (task == null) {
            return null;
        }

        if (title != null && !title.trim().isEmpty()) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        if (status != null) {
            task.setStatus(status);
        }
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId).orElse(null);
            task.setAssignee(assignee);
        } else {
            // Если передан null, убираем исполнителя
            task.setAssignee(null);
        }

        task.setUpdatedAt(java.time.OffsetDateTime.now());
        task = taskRepository.save(task);
        // Перезагружаем с отношениями для корректного отображения
        return taskRepository.findByIdWithRelations(id);
    }
}

