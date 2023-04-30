package ru.psu.pls.teacher.pack.controller;

import ru.psu.pls.general.model.Task;
import ru.psu.pls.general.model.TaskGroup;
import ru.psu.pls.teacher.pack.model.TypeConverter;

import java.io.File;
import java.io.IOException;

public class PackageManager {
    private final GroupManager groupManager;
    private final TaskManager taskManager;
    private final TypeConverter typeConverter;

    private final String workingDirectory = "./workspace"; // FIXME

    public PackageManager() {
        this.groupManager = new GroupManager();
        this.taskManager = new TaskManager();
        this.typeConverter = new TypeConverter();

        File workingDir = new File(workingDirectory);
        if (!workingDir.exists())
            workingDir.mkdirs();
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void saveAsType(TaskGroup taskGroup) throws IOException {
        String destinationPath = workingDirectory + File.separator + taskGroup.getDirectoryName();
        this.typeConverter.compress(taskGroup, destinationPath);
    }

    public TaskGroup loadFromType(String zipFilePath) throws IOException {
        return typeConverter.decompress(zipFilePath, workingDirectory);
    }

    public Task createTask(String taskName, TaskGroup taskGroup) throws IOException {
        Task task = this.taskManager.create(taskName, taskGroup, workingDirectory);
        this.saveTaskGroupChanges(taskGroup);
        return task;
    }

    public void deleteTask(Task task, TaskGroup taskGroup) throws IOException {
        this.taskManager.delete(task, taskGroup, workingDirectory);
        this.saveTaskGroupChanges(taskGroup);
    }

    public TaskGroup createTaskGroup(String name, String description, String author) throws IOException {
        String path = workingDirectory + File.separator + name;
        return this.groupManager.create(path, name, description, author);
    }

    public void saveTaskGroupChanges(TaskGroup taskGroup) throws IOException {
        String path = workingDirectory + File.separator + taskGroup.getDirectoryName();
        this.groupManager.saveChanges(path, taskGroup);
    }

    public TaskGroup openTaskGroup(String path) throws IOException {
        return this.groupManager.open(path);
    }
}
