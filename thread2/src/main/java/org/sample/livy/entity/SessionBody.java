package org.sample.livy.entity;

/**
 * Created by chenxh on 2018/7/26.
 */
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SessionBody extends SparkApplicationBody {

    //    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String kind;
    private Integer heartbeatTimeoutInSecond;

    public Integer getHeartbeatTimeoutInSecond() {
        return heartbeatTimeoutInSecond;
    }

    public void setHeartbeatTimeoutInSecond(Integer heartbeatTimeoutInSecond) {
        this.heartbeatTimeoutInSecond = heartbeatTimeoutInSecond;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
