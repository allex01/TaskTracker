package com.example.tracker.task.controller;

import com.example.tracker.group.entity.Group;
import com.example.tracker.group.service.GroupService;
import com.example.tracker.group.entity.GroupMember;
import com.example.tracker.group.repo.GroupMemberRepository;
import com.example.tracker.security.AppUserDetails;
import com.example.tracker.security.AuditService;
import com.example.tracker.task.entity.Task;
import com.example.tracker.task.entity.TaskPriority;
import com.example.tracker.task.entity.TaskStatus;
import com.example.tracker.task.service.TaskService;
import com.example.tracker.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final GroupService groupService;
    private final GroupMemberRepository groupMemberRepository;
    private final AuditService auditService;

    public TaskController(TaskService taskService, GroupService groupService, GroupMemberRepository groupMemberRepository, AuditService auditService) {
        this.taskService = taskService;
        this.groupService = groupService;
        this.groupMemberRepository = groupMemberRepository;
        this.auditService = auditService;
    }

    @GetMapping("/groups/{groupId}/tasks/create")
    public String createPage(@PathVariable UUID groupId, Model model) {
        Group group = groupService.findById(groupId);
        if (group == null) {
            return "redirect:/groups";
        }

        // Получаем список участников группы для выбора исполнителя
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        List<User> groupUsers = members.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toList());

        model.addAttribute("group", group);
        model.addAttribute("groupUsers", groupUsers);
        model.addAttribute("priorities", TaskPriority.values());

        return "tasks/create";
    }

    @PostMapping("/groups/{groupId}/tasks/create")
    public String create(
            @PathVariable UUID groupId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID assigneeId,
            HttpServletRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails aud) {
            User user = aud.getUser();
            TaskPriority taskPriority = priority != null && !priority.isEmpty() 
                    ? TaskPriority.valueOf(priority) 
                    : TaskPriority.MEDIUM;
            
            Task task = taskService.createTask(groupId, title, description, taskPriority, assigneeId, user);
            auditService.log("TASK_CREATED", user.getUsername(), request, 
                    Map.of("taskId", task.getId().toString(), "groupId", groupId.toString(), "title", title));
        }
        return "redirect:/groups/" + groupId;
    }

    @GetMapping("/tasks/{id}")
    public String view(@PathVariable UUID id, Model model) {
        Task task = taskService.findById(id);
        if (task == null) {
            return "redirect:/groups";
        }
        model.addAttribute("task", task);
        return "tasks/view";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editPage(@PathVariable UUID id, Model model) {
        Task task = taskService.findById(id);
        if (task == null) {
            return "redirect:/groups";
        }
        List<GroupMember> members = groupMemberRepository.findByGroupId(task.getGroup().getId());
        List<User> groupUsers = members.stream()
                .map(GroupMember::getUser)
                .collect(Collectors.toList());

        model.addAttribute("task", task);
        model.addAttribute("groupUsers", groupUsers);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/edit";
    }

    @PostMapping("/tasks/{id}/update")
    public String update(
            @PathVariable UUID id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assigneeIdStr,
            HttpServletRequest request
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails aud) {
            User user = aud.getUser();
            TaskPriority taskPriority = priority != null && !priority.isEmpty() 
                    ? TaskPriority.valueOf(priority) 
                    : null;
            TaskStatus taskStatus = status != null && !status.isEmpty() 
                    ? TaskStatus.valueOf(status) 
                    : null;
            UUID assigneeId = assigneeIdStr != null && !assigneeIdStr.isEmpty() 
                    ? UUID.fromString(assigneeIdStr) 
                    : null;
            
            Task task = taskService.updateTask(id, title, description, taskPriority, taskStatus, assigneeId);
            if (task != null) {
                auditService.log("TASK_UPDATED", user.getUsername(), request, 
                        Map.of("taskId", id.toString()));
                return "redirect:/tasks/" + id;
            }
        }
        return "redirect:/groups";
    }
}

