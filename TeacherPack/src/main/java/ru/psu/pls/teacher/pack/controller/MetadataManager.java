package ru.psu.pls.teacher.pack.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.psu.pls.general.model.TaskGroup;

import java.io.*;
import java.util.stream.Collectors;

public class MetadataManager {
    public final static String packageInfoFileName = "package-info.json";

    private final Gson gson;

    public MetadataManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveMetadata(TaskGroup taskGroup, String groupPath) throws IOException {
        String packageInfoJsonString = this.gson.toJson(taskGroup);
        String saveToPath = groupPath + File.separator + MetadataManager.packageInfoFileName;

        try (FileWriter fw = new FileWriter(saveToPath)) {
            fw.write(packageInfoJsonString);
        }
    }

    public String extractMetadata(String groupPath) throws IOException {
        String json;
        String extractFromPath = groupPath + File.separator + MetadataManager.packageInfoFileName;

        try (FileReader fr = new FileReader(extractFromPath);
             BufferedReader br = new BufferedReader(fr)) {
            json = br.lines().collect(Collectors.joining());
        }
        return json;
    }

    public TaskGroup extractTaskGroup(String groupPath) throws IOException {
        String json = this.extractMetadata(groupPath);
        return gson.fromJson(json, TaskGroup.class);
    }
}
