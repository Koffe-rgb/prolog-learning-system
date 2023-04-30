package ru.psu.pls.student.pack.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Test {
    private final String name;
    private final List<String> infoArguments;
    private final List<String> expectedResults;
    private final List<String> realResults;
    private boolean complete;

    public Test(String name) {
        this.name = name;
        infoArguments = new ArrayList<>();
        expectedResults = new ArrayList<>();
        realResults = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getInfoArguments() {
        return infoArguments;
    }

    public List<String> getExpectedResults() {
        return expectedResults;
    }

    public List<String> getRealResults() {
        return realResults;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Test test = (Test) o;
        return name.equals(test.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name.toUpperCase(Locale.ROOT) + " : " + (complete ? "успешно" : "ошибка");
    }
}
