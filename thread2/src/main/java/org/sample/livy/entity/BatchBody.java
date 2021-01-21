package org.sample.livy.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxh on 2018/7/26.
 */
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchBody extends SparkApplicationBody {
    private String file;
    private String className;
    private List<String> args = new ArrayList<>();

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
