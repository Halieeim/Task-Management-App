package com.ropulva.task.calendar.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.google.api.services.tasks.model.Task;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/createTask")
    public String createTask(
    		@RequestParam String title,
    		@RequestParam String notes,
    		@RequestParam String due) {
        try {
            return taskService.addTask(title, notes, due);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating task: " + e.getMessage();
        }
    }

    @GetMapping("/deleteTask/{taskId}")
    public String deleteTask(@PathVariable String taskId) {
        try {
            return taskService.deleteTask(taskId);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting task: " + e.getMessage();
        }
    }

    @GetMapping("/editTask/{taskId}")
    public String editTask(@PathVariable String taskId,
                           @RequestParam(required = false) String title,
                           @RequestParam(required = false) String notes,
                           @RequestParam(required = false) String due) {
        try {
            return taskService.editTask(taskId, title, notes, due);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error editing task: " + e.getMessage();
        }
    }

    @GetMapping("/getTasks")
    public List<Task> getTasks() {
        try {
            return taskService.getTasks();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
