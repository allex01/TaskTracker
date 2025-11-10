package com.example.tracker.group.controller;

import com.example.tracker.group.entity.Group;
import com.example.tracker.group.repo.GroupRepository;
import com.example.tracker.group.service.GroupService;
import com.example.tracker.security.AppUserDetails;
import com.example.tracker.security.AuditService;
import com.example.tracker.task.service.TaskService;
import com.example.tracker.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class GroupController {

    private final GroupService groupService;
    private final GroupRepository groupRepository;
    private final AuditService auditService;
    private final TaskService taskService;

    public GroupController(GroupService groupService, GroupRepository groupRepository, AuditService auditService, TaskService taskService) {
        this.groupService = groupService;
        this.groupRepository = groupRepository;
        this.auditService = auditService;
        this.taskService = taskService;
    }

    @GetMapping("/groups")
    public String list(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails aud) {
            User user = aud.getUser();
            List<Group> groups = groupRepository.findByOwner(user);
            model.addAttribute("groups", groups);
        }
        return "groups/list";
    }

    @GetMapping("/groups/create")
    public String createPage() {
        return "groups/create";
    }

    @PostMapping("/groups/create")
    public String create(@RequestParam String name, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserDetails aud) {
            User user = aud.getUser();
            Group group = groupService.createGroup(name, user);
            auditService.log("GROUP_CREATED", user.getUsername(), request, Map.of("groupName", name, "groupId", group.getId().toString()));
        }
        return "redirect:/groups";
    }

    @GetMapping("/groups/{id}")
    public String view(@PathVariable UUID id, Model model) {
        Group group = groupService.findById(id);
        if (group == null) {
            return "redirect:/groups";
        }
        List<com.example.tracker.task.entity.Task> tasks = taskService.findByGroupId(id);
        model.addAttribute("group", group);
        model.addAttribute("tasks", tasks);
        return "groups/view";
    }
}

