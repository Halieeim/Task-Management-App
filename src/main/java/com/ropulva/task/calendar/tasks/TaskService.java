package com.ropulva.task.calendar.tasks;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.ropulva.task.calendar.configurations.GoogleTasksConfig;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
	@Autowired
    private TaskRepository taskRepository;
	
	@CacheEvict(value = "tasks", allEntries = true)
    public String addTask(String title, String notes, String due) throws Exception {
        Tasks service = GoogleTasksConfig.getTasksService();
        Task task = new Task();
        task.setTitle(title);
        task.setNotes(notes);
        DateTime dueDate = new DateTime(due);
        task.setDue(dueDate);

        Task result = service.tasks().insert("@default", task).execute();
        
        // add task in database
        LocalDateTime dueDateTime = LocalDateTime.parse(due);
        com.ropulva.task.calendar.tasks.Tasks taskDB = new com.ropulva.task.calendar.tasks.Tasks(result.getId(), title, notes, "@default", dueDateTime);
        taskRepository.save(taskDB);
        
        return "Task created: " + result.getTitle();
    }

	@CacheEvict(value = "tasks", allEntries = true)
    public String deleteTask(String taskId) throws Exception {
        Tasks service = GoogleTasksConfig.getTasksService();
        service.tasks().delete("@default", taskId).execute();
        
        //delete from database
        taskRepository.deleteById(taskId);
        
        return "Task deleted";
    }
    
	@CacheEvict(value = "tasks", allEntries = true)
    @Transactional
    public String editTask(String taskId, String title, String notes, String due) throws Exception {
        Tasks service = GoogleTasksConfig.getTasksService();
        Task task = service.tasks().get("@default", taskId).execute();
        
        com.ropulva.task.calendar.tasks.Tasks taskDB = taskRepository.findById(taskId).orElseThrow(() -> new IllegalStateException("Task with id " + taskId + " does not exist."));
        
        if(title != null && title.length() > 0) {
        	task.setTitle(title);
        	taskDB.setTitle(title);
        }
        if(notes != null && notes.length() > 0) {
        	task.setNotes(notes);
        	taskDB.setNotes(notes);
        }
        if(due != null && due.length() > 0) {
        	DateTime dueDate = new DateTime(due);
        	task.setDue(dueDate);
        	LocalDateTime dueDateTime = LocalDateTime.parse(due);
        	taskDB.setDue(dueDateTime);
        }

        Task updatedTask = service.tasks().update("@default", taskId, task).execute();
        
        
        return "Task updated: " + updatedTask.getTitle();
    }

	@Cacheable("tasks")
    public List<Task> getTasks() throws Exception {
        Tasks service = GoogleTasksConfig.getTasksService();
        com.google.api.services.tasks.model.Tasks tasks = service.tasks().list("@default").execute();
        return tasks.getItems();
    }
}
