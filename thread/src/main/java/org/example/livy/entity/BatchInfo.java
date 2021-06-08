package org.example.livy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxh on 2018/7/26.
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchInfo implements Serializable {
    private int id;
    private String appId;
    private Map<String, String> appInfo;
    private List<String> log;
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Map<String, String> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(Map<String, String> appInfo) {
        this.appInfo = appInfo;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
