package ru.psu.pls.teacher.pack.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import ru.psu.pls.general.model.TaskGroup;
import ru.psu.pls.teacher.pack.controller.MetadataManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TypeConverter {
    private final static char[] password = "A0B72228EE874188A36A3CE7CB06AC75".toCharArray();
    private final static String dateTimeFormat = "dd-MM-yyyy-[HH]-[mm]";
    private final static String extension = "tg";

    private final MetadataManager metadataManager;

    public TypeConverter() {
        metadataManager = new MetadataManager();
    }

    public void compress(TaskGroup taskGroup, String destinationPath) throws IOException {
        // сохранить метаданные
        metadataManager.saveMetadata(taskGroup, destinationPath);
        File packageInfo = new File(destinationPath + File.separator + MetadataManager.packageInfoFileName);

        // сформировать набор файлов для архива
        List<File> filesToZip = taskGroup.getTasks().stream()
                .map(task -> new File(destinationPath, task.getFilename()))
                .collect(Collectors.toList());
        filesToZip.add(packageInfo);

        // параметры архива
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        String zipFileName = generateZipFileName(taskGroup);

        // создание архива
        try (ZipFile zipFile = new ZipFile(destinationPath + File.separator + zipFileName, TypeConverter.password)) {
            zipFile.addFiles(filesToZip, zipParameters);
        }
    }

    private static String generateZipFileName(TaskGroup taskGroup) {
        return taskGroup.getName() +
                "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)) +
                "." +
                extension;
    }

    public TaskGroup decompress(String zipFilePath, String destinationPath) throws IOException {
        // разархивировать файлы группы
        try (ZipFile zipFile = new ZipFile(zipFilePath, password)) {
            zipFile.extractAll(destinationPath);
        }

        // получить json описание группы заданий
        String json = metadataManager.extractMetadata(destinationPath);

        // создать объект группы по json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, TaskGroup.class);
    }
}
