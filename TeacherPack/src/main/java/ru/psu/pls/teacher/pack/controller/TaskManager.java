package ru.psu.pls.teacher.pack.controller;

import ru.psu.pls.general.model.Task;
import ru.psu.pls.general.model.TaskGroup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TaskManager {
    private static final String template =
            "% Используйте функцию генерации начального кода Обозревателя группы заданий\n" +
            "% для избегания ошибок. Для доступа к функции необходимо: создать / открыть \n" +
            "% группу заданий в Обозревателе групп заданий, после чего в списке слева \n" +
            "% выбрать задание и нажать кнопку \"Открыть на редактирование\" под списком.\n" +
            "% В открывшемся окне: добавить названия тестов, написать формулировку \n" +
            "% задания, после чего нажать на кнопку \"Сгенерировать начальный код\".\n" +
            "% Сгенерированный код будет скопирован в буфер обмена, после чего его можно\n" +
            "% вставить в этот файл.";

    private static final String extension = "pl";

    public TaskManager() {
    }

    public Task create(String name, TaskGroup taskGroup, String workingDirectory) throws IOException {
        Task task = new Task(name);
        String pathToGroup = workingDirectory + File.separator +
                taskGroup.getDirectoryName() + File.separator;
        String taskFileName = task.getName() + "." + extension;

        task.setFilename(taskFileName);
        task.setDescription("");

        File taskFile = new File(pathToGroup, taskFileName);
        try (FileWriter fw = new FileWriter(taskFile)) {
            fw.write(template);
        }

        taskGroup.getTasks().add(task);
        return task;
    }

    public void delete(Task task, TaskGroup taskGroup, String workingDirectory) {
        String pathToGroup = workingDirectory + File.separator +
                taskGroup.getDirectoryName() + File.separator;
        String taskFileName = task.getName() + "." + extension;

        taskGroup.getTasks().remove(task);

        File taskFile = new File(pathToGroup, taskFileName);
        taskFile.delete();
    }
}
