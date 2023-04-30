package ru.psu.pls.student.pack.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lingala.zip4j.ZipFile;
import ru.psu.pls.general.model.TaskGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class TypeConverter {
    private final static char[] password = "A0B72228EE874188A36A3CE7CB06AC75".toCharArray();
    private final static String packageInfoFileName = "package-info.json";

    public TaskGroup decompress(String zipFilePath, String destinationPath) throws IOException {
        // разархивировать файлы группы
        try (ZipFile zipFile = new ZipFile(zipFilePath, password)) {
            zipFile.extractAll(destinationPath);
        }

        // получить json описание группы заданий
        String json = extractMetadata(destinationPath);

        // создать объект группы по json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, TaskGroup.class);
    }

    private String extractMetadata(String groupPath) throws IOException {
        String json;
        String extractFromPath = groupPath + File.separator + packageInfoFileName;

        try (FileReader fr = new FileReader(extractFromPath);
             BufferedReader br = new BufferedReader(fr)) {
            json = br.lines().collect(Collectors.joining());
        }
        return json;
    }
}
