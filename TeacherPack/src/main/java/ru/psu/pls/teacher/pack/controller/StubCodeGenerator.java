package ru.psu.pls.teacher.pack.controller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import ru.psu.pls.general.model.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StubCodeGenerator {

    private final Task task;
    private final Handlebars hbs;

    public StubCodeGenerator(Task task) {
        this.task = task;
        TemplateLoader loader = new FileTemplateLoader("./templates", ".tpl");
        this.hbs = new Handlebars(loader);
    }

    public String generateStubCode() {
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("taskName", task.getName());
            put("description", task.getDescription());
            put("testNames", task.getTestNames());
        }};

        return createStubCodeString("std_template", data);
    }

    private String createStubCodeString(String templateName, Map<String, Object> data) {
        Template template;
        String stubCode;
        try {
            template = this.hbs.compile(templateName);
            stubCode = template.apply(data);
        } catch (IOException e) {
            // FIXME
            throw new RuntimeException(e);
        }
        return stubCode;
    }
}
