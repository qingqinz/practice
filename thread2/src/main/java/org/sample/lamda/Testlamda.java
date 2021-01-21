package org.sample.lamda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Testlamda {
    static Logger logger = LoggerFactory.getLogger(Testlamda.class);

    public static void main(String[] args) {
        List<TSysBbgz> tSysBbgzs = new ArrayList<>();
        TSysBbgz tSysBbgz1 = new TSysBbgz("1", "1", "1");
        //测试去掉空元素
        TSysBbgz tSysBbgz2 = new TSysBbgz("", "2", "2");
        TSysBbgz tSysBbgz3 = new TSysBbgz("3", "3", "3");
        tSysBbgzs.add(tSysBbgz1);
        tSysBbgzs.add(tSysBbgz2);
        tSysBbgzs.add(tSysBbgz3);

        List<TSysExceptionRule> tSysExceptionRules = new ArrayList<>();
        TSysExceptionRule tSysExceptionRule1 = new TSysExceptionRule("1", "1", "1");
        //测试去掉重复元素
        TSysExceptionRule tSysExceptionRule2 = new TSysExceptionRule("1", "2", "1");
        TSysExceptionRule tSysExceptionRule3 = new TSysExceptionRule("3", "3", "3");
        tSysExceptionRules.add(tSysExceptionRule1);
        tSysExceptionRules.add(tSysExceptionRule2);
        tSysExceptionRules.add(tSysExceptionRule3);

        //必须赋给新对象
        List<TSysBbgz> newTsysbbgz =tSysBbgzs.stream().filter(p -> p.getXtbm() != null && !"".equals(p.getXtbm())).collect(Collectors.toList());

        logger.info(tSysBbgzs.toString());
        logger.info(newTsysbbgz.toString());

        try {
            Map<String, TSysExceptionRule> exceptionRuleMap =
                    tSysExceptionRules.stream().collect(Collectors.toMap(TSysExceptionRule::getRuleSeq, Function.identity(), (v1, v2) -> v1));
            Iterator<TSysBbgz> iterator = tSysBbgzs.iterator();
            while (iterator.hasNext()) {
                TSysBbgz tmpRule = iterator.next();
                if (exceptionRuleMap.containsKey(tmpRule.getRuleSeq())) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
        }

        tSysBbgzs.stream().map(detailVo -> {
            detailVo.setDiySql("aaaa");
            return detailVo;
        }).collect(Collectors.toList());

        logger.info(tSysBbgzs.toString());
    }
}

class TSysBbgz {
    public String getXtbm() {
        return xtbm;
    }

    public void setXtbm(String xtbm) {
        this.xtbm = xtbm;
    }

    public String getDiySql() {
        return diySql;
    }

    public void setDiySql(String diySql) {
        this.diySql = diySql;
    }

    public String getRuleSeq() {
        return ruleSeq;
    }

    public void setRuleSeq(String ruleSeq) {
        this.ruleSeq = ruleSeq;
    }

    String xtbm;
    String diySql;
    String ruleSeq;

    public TSysBbgz(String xtbm, String diySql, String ruleSeq) {
        this.xtbm = xtbm;
        this.diySql = diySql;
        this.ruleSeq = ruleSeq;
    }
}

class TSysExceptionRule {
    public String getXtbm() {
        return xtbm;
    }

    public void setXtbm(String xtbm) {
        this.xtbm = xtbm;
    }

    public String getDiySql() {
        return diySql;
    }

    public void setDiySql(String diySql) {
        this.diySql = diySql;
    }

    public String getRuleSeq() {
        return ruleSeq;
    }

    public void setRuleSeq(String ruleSeq) {
        this.ruleSeq = ruleSeq;
    }

    String xtbm;
    String diySql;
    String ruleSeq;

    public TSysExceptionRule(String xtbm, String diySql, String ruleSeq) {
        this.xtbm = xtbm;
        this.diySql = diySql;
        this.ruleSeq = ruleSeq;
    }
}
