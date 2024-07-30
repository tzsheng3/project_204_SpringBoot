package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public Task createTask(Task task) {
        if (task.getUser() == null || task.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID must be provided");
        }
        User user = userRepository.findById(task.getUser().getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }
    

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Task getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setUser(userRepository.findById(task.getUser().getId()).orElse(null));
        }
        return task;
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task != null) {
            task.setTitle(taskDetails.getTitle());
            task.setCategory(taskDetails.getCategory());
            task.setDescription(taskDetails.getDescription());
            task.setPriority(taskDetails.getPriority());
            task.setDueDate(taskDetails.getDueDate());
            return taskRepository.save(task);
        }
        return null;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> searchTasks(String title, String category, String priority) {
        if (title != null && !title.isEmpty()) {
            if (category != null && !category.isEmpty()) {
                if (priority != null && !priority.isEmpty()) {
                    return taskRepository.findByTitleContainingAndCategoryAndPriority(title, category, priority);
                } else {
                    return taskRepository.findByTitleContainingAndCategory(title, category);
                }
            } else if (priority != null && !priority.isEmpty()) {
                return taskRepository.findByTitleContainingAndPriority(title, priority);
            } else {
                return taskRepository.findByTitleContaining(title);
            }
        } else if (category != null && !category.isEmpty()) {
            if (priority != null && !priority.isEmpty()) {
                return taskRepository.findByCategoryAndPriority(category, priority);
            } else {
                return taskRepository.findByCategory(category);
            }
        } else if (priority != null && !priority.isEmpty()) {
            return taskRepository.findByPriority(priority);
        } else {
            return taskRepository.findAll();
        }
    }
}
