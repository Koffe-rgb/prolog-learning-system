package ru.psu.pls.general.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskGroup {
    private String directoryName;
    private String name;
    private String description;
    private String author;
    private final LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;
    private final List<Task> tasks;

    public TaskGroup(String name, String description, String author) {
        this.directoryName = name;
        this.name = name;
        this.description = description;
        this.author = author;
        this.tasks = new ArrayList<>();
        this.creationDateTime = LocalDateTime.now();
        this.updateDateTime = LocalDateTime.now();
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
