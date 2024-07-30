package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTitleContaining(String title);
    List<Task> findByCategory(String category);
    List<Task> findByPriority(String priority);
    List<Task> findByTitleContainingIgnoreCase(String title);
    List<Task> findByTitleContainingAndCategory(String title, String category);
    List<Task> findByTitleContainingAndPriority(String title, String priority);
    List<Task> findByCategoryAndPriority(String category, String priority);
    List<Task> findByTitleContainingAndCategoryAndPriority(String title, String category, String priority);
    }
