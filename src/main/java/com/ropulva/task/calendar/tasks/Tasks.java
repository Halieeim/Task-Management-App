package com.ropulva.task.calendar.tasks;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Tasks {
	@Id
	private String id;
	private String title;
	private String notes;
	private String taskList;
	private LocalDateTime due;
	
	public Tasks() {
		
	}
	
	public Tasks(String id, String title, String notes, String taskList, LocalDateTime due) {
		super();
		this.id = id;
		this.title = title;
		this.notes = notes;
		this.taskList = taskList;
		this.due = due;
	}
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNotes() {
		return this.notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getTaskList() {
		return this.taskList;
	}
	public void setTaskList(String taskList) {
		this.taskList = taskList;
	}

	public LocalDateTime getDue() {
		return due;
	}

	public void setDue(LocalDateTime due) {
		this.due = due;
	}
	
	
}
