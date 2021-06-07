package org.example.livy.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.ToString;
import org.apache.livy.client.common.HttpMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxh on 2018/7/26.
 */
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SparkApplicationBody implements HttpMessages.ClientMessage {

    private String proxyUser;
    private List<String> jars;
    private List<String> pyFiles;
    private List<String> files;
    private String driverMemory;
    private Integer driverCores;
    private String executorMemory;
    private Integer executorCores;
    private Integer numExecutors;
    private List<String> archives;
    private String queue;
    private String name;
    private Map<String, String> conf;

    public SparkApplicationBody() {
        jars = new ArrayList<>();
        pyFiles = new ArrayList<>();
        files = new ArrayList<>();
        archives = new ArrayList<>();
        conf = new HashMap<>();
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public List<String> getJars() {
        return jars;
    }

    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public List<String> getPyFiles() {
        return pyFiles;
    }

    public void setPyFiles(List<String> pyFiles) {
        this.pyFiles = pyFiles;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public Integer getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(Integer driverCores) {
        this.driverCores = driverCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public Integer getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(Integer executorCores) {
        this.executorCores = executorCores;
    }

    public Integer getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(Integer numExecutors) {
        this.numExecutors = numExecutors;
    }

    public List<String> getArchives() {
        return archives;
    }

    public void setArchives(List<String> archives) {
        this.archives = archives;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConf() {
        return conf;
    }

    public void setConf(Map<String, String> conf) {
        this.conf = conf;
    }
}
