package org.example.livy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxh on 2018/7/25.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionState implements Serializable {
    private long id;
    private String name;
    private State state;
    private String appId;
    private String owner;
    private String proxyUser;
    private String kind;
    private List<String> log;
    private Map<String, Object> appInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public enum State {
        not_started, starting, idle, busy, shutting_down, error, dead, success
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public Map<String, Object> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(Map<String, Object> appInfo) {
        this.appInfo = appInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
