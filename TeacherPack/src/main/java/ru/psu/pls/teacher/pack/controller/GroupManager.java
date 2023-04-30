package ru.psu.pls.teacher.pack.controller;

import ru.psu.pls.general.model.TaskGroup;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class GroupManager {
    private final MetadataManager metadataManager;

    public GroupManager() {
        metadataManager = new MetadataManager();
    }

    // создает папку для группы с именем группы и сохраняет в ней файл метаданных
    public TaskGroup create(String path, String name, String description, String author) throws IOException {
        new File(path).mkdirs();
        TaskGroup taskGroup = new TaskGroup(name, description, author);
        this.saveChanges(path, taskGroup);
        return taskGroup;
    }

    // возвращает объект группы из файла метаданных
    public TaskGroup open(String path) throws IOException {
        return metadataManager.extractTaskGroup(path);
    }

    // сохраняет метаданные группы
    public void saveChanges(String path, TaskGroup taskGroup) throws IOException {
        taskGroup.setUpdateDateTime(LocalDateTime.now());
        metadataManager.saveMetadata(taskGroup, path);
    }
}
