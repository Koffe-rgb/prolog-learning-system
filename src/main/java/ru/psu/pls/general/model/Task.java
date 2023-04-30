package ru.psu.pls.general.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;
    private String description;
    private String filename;
    private final List<String> testNames;

    public Task(String name) {
        this.name = name;
        this.testNames = new ArrayList<>();
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<String> getTestNames() {
        return testNames;
    }

    @Override
    public String toString() {
        return name;
    }
}
