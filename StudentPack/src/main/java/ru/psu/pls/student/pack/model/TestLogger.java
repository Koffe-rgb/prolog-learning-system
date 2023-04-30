package ru.psu.pls.student.pack.model;

import java.util.*;

public class TestLogger {
    private final Map<String, Test> tests;
    private String description;

    public TestLogger() {
        tests = new LinkedHashMap<>();
    }

    public List<Test> results() {
        return new ArrayList<>(tests.values());
    }

    public void clear() {
        tests.clear();
    }

    public void print()  {
        System.out.println(description);
        tests.forEach((key, test) -> System.out.println(test));
    }

    // Методы ниже вызываются SWI-Prolog'ом при выполнении тестов

    public void addNewTest(String testName) {
        tests.put(testName, new Test(testName));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addInfoArgument(Object argument, String testName) {
        tests.get(testName)
                .getInfoArguments()
                .add(String.valueOf(argument));
    }

    public void addInfoResult(Object expectedResult, String testName) {
        tests.get(testName)
                .getExpectedResults()
                .add(String.valueOf(expectedResult));
    }

    public void addRealResult(Object realResult, String testName) {
        tests.get(testName)
                .getRealResults()
                .add(String.valueOf(realResult));
    }

    public void setTestComplete(boolean completeStatus, String testName) {
        tests.get(testName).setComplete(completeStatus);
    }
}
