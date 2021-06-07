package org.example.livy.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.livy.client.common.HttpMessages;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxh on 2018/7/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatementStatus implements HttpMessages.ClientMessage {

    private Integer id;
    private String code;
    private State state;
    private StatementOutput output;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public StatementOutput getOutput() {
        return output;
    }

    public void setOutput(StatementOutput output) {
        this.output = output;
    }

    public static enum State {
        waiting, running, available, error, cancelling, cancelled
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatementOutput {
        private String status;
        private Integer execution_count;
        private Map<String, Object> data;
        private String evalue;
        private List<String> traceback;

        public String getEvalue() {
            return evalue;
        }

        public void setEvalue(String evalue) {
            this.evalue = evalue;
        }

        public List<String> getTraceback() {
            return traceback;
        }

        public void setTraceback(List<String> traceback) {
            this.traceback = traceback;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getExecution_count() {
            return execution_count;
        }

        public void setExecution_count(Integer execution_count) {
            this.execution_count = execution_count;
        }

    }
}
