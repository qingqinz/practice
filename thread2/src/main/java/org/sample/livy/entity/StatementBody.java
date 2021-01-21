package org.sample.livy.entity;

import org.apache.livy.client.common.HttpMessages;

/**
 * Created by chenxh on 2018/7/24.
 */
public class StatementBody implements HttpMessages.ClientMessage {
    private String kind;
    private String code;

    public StatementBody() {
    }

    public StatementBody(String kind, String code) {
        this.kind = kind;
        this.code = code;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "StatementBody{" +
                "kind='" + kind + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
