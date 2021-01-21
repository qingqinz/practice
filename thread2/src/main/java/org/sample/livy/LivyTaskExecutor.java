package org.sample.livy;//package com.iwellmass.etl.base.util.livy;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.base.Joiner;
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Lists;
//import com.iwellmass.common.param.ExecParam;
//import com.iwellmass.common.util.Utils;
//import com.iwellmass.datafactory.common.Configs;
//import com.iwellmass.datafactory.entity.SysConfig;
//import com.iwellmass.datafactory.entity.TaskGroup;
//import com.iwellmass.datafactory.entity.TaskLog;
//import com.iwellmass.datafactory.job.JobUtil;
//import com.iwellmass.datafactory.job.ParamParser;
//import com.iwellmass.datafactory.job.TaskExecutor;
//import com.iwellmass.datafactory.job.livy.entity.BatchBody;
//import com.iwellmass.datafactory.job.livy.entity.StatementBody;
//import com.iwellmass.datafactory.repo.SysConfigDao;
//import com.iwellmass.datafactory.repo.TaskLogDao;
//import com.iwellmass.idc.executor.IDCJobContext;
//import com.iwellmass.idc.executor.IDCStatusService;
//import com.iwellmass.idc.model.JobInstanceStatus;
//import org.elasticsearch.common.Strings;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.inject.Inject;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by chenxh on 2018/7/25.
// */
//@Component
//public class LivyTaskExecutor implements TaskExecutor {
//
//    @Value("${livy.address}")
//    private String livyAddress;
//
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Inject
//    ParamParser paramParser;
//
//    @Inject
//    TaskLogDao taskLogDao;
//
//    @Inject
//    SysConfigDao sysConfigDao;
//
//    @Inject
//    private IDCStatusService idcStatusService;
//
//    @Value("${idc.logInfo}")
//    private String idcLogInfo;
//
//    private ExecutorService jobExecutorService = Executors.newFixedThreadPool(5);
//
//    /**
//     * 执行spark任务
//     *
//     * @param taskGroup
//     * @param execParams
//     */
//    public void executeTask(IDCJobContext context, TaskGroup taskGroup, List<Map<String, ExecParam>> execParams) {
//        // 发送log地址
//        if (context != null ) {
//            context.progress();
//        }
//        try {
//            jobExecutorService.submit(() ->
//                    {
//                        boolean res;
//                        switch (taskGroup.getContentType()) {
//                            case SCALA:
//                            case SPARK_SQL:
//                                res = executeStatement(context, taskGroup, execParams);
//                                break;
//                            case SPARK_APP:
//                                res = executeBatch(context, taskGroup, execParams);
//                                break;
//                            default:
//                                res = false;
//                        }
//                        if (context != null) {
//                            context.complete(context.newCompleteEvent(res ? JobInstanceStatus.FINISHED : JobInstanceStatus.FAILED));
//                        }
//                        return res;
//                    }
//
//            ).get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            if (context != null) {
//                context.fail(e);
//            }
//        }
//    }
//
//    private boolean executeBatch(IDCJobContext context, TaskGroup taskGroup, List<Map<String, ExecParam>> execParams) {
//        logger.info("execute task group [{}] , task count [{}]", taskGroup.getTaskName(), execParams.size());
//        BatchClient client = createBatchClient();
//        return executeBatchs(context, client, execParams, taskGroup);
//    }
//
//    private boolean executeBatchs(IDCJobContext context, BatchClient client, List<Map<String, ExecParam>> execParams, TaskGroup taskGroup) {
//        boolean res = true;
//        StringBuilder logs = new StringBuilder();
//        for (Map<String, ExecParam> params : execParams) {
//            BatchBody body = JSON.parseObject(taskGroup.getContent(), BatchBody.class);
//            try {
//                Map<String, String> newConf = calcBatchTaskParam(body, params);
//                logs.append("params:").append(JSON.toJSONString(newConf)).append("\n");
//                body.setConf(newConf);
//                BatchHandleImpl batchHandle = client.startBatch(body);
//                String s = batchHandle.get();
//                res = res && ("success".equals(s));
//                logs.append("result:").append(s).append("\n");
//                saveTaskLog(context, taskGroup, "SparkApp-" + taskGroup.getTaskName(), s, logs.toString());
//                if (!res && context != null) {
//                    context.fail("executeBatchs error:" + "\n" +"logs:" + logs.toString());
//                }
//            } catch (ExecutionException | InterruptedException e) {
//                logs.append("result:").append(e.getMessage()).append("\n");
//                saveTaskLog(context, taskGroup, "SparkApp-" + taskGroup.getTaskName(), e.getMessage(), logs.toString());
//                e.printStackTrace();
//                res = false;
//                if (context != null) {
//                    context.fail(e);
//                }
//            }
//        }
//        return res;
//    }
//
//    private Map<String, String> calcBatchTaskParam(BatchBody body,  Map<String, ExecParam> params) {
//        Map<String, String> conf = body.getConf();
//        Map<String, String> rewriteConf = new HashMap<>();
//        conf.entrySet().forEach(e -> rewriteConf.put(e.getKey(), paramParser.parseSql(e.getValue(), params)));
//        return rewriteConf;
//    }
//
//    private boolean executeStatement(IDCJobContext context, TaskGroup taskGroup, List<Map<String, ExecParam>> execParams) {
//        logger.info("execute task group [{}] , task count [{}]", taskGroup.getTaskName(), execParams.size());
//        StatementClient client = createStatementClient();
//        logger.info("starting session for task [{}]", taskGroup.getTaskName());
//        return executeStatements(context, client, taskGroup, execParams);
//    }
//
//    private boolean executeStatements(IDCJobContext context, StatementClient client, TaskGroup taskGroup, List<Map<String, ExecParam>> execParams) {
//        boolean res = true;
//        StringBuilder logs = new StringBuilder();
//        try {
//            Preconditions.checkState(client.waitSessionOk());
//        } catch (Exception e) {
//            logger.error("start session error!", e);
//            saveTaskLog(context, taskGroup, taskGroup.getContent(), "error", logs.toString());
//            if (context != null) {
//                context.fail(e);
//            }
//            return false;
//        }
//
//        for (Map<String, ExecParam> params : execParams) {
//            String[] sqlContents = taskGroup.getContent().split(";");
//            for (String content : sqlContents) {
//                if (Strings.isNullOrEmpty(Utils.trimBlank(content))) {
//                    continue;
//                }
//                String statement = "";
//                try {
//                    statement = paramParser.parseSql(content, params);
//                } catch (Exception e) {
//                    // notify idc nodeJob fail
//                    if (context != null) {
//                        context.fail(e);
//                    }
//
//                    logger.error("parse statement[{},{}] error!", content, params, e);
//                    logs.append(String.format("parse statement[%s,%s] error : %s", content, JSON.toJSONString(params), e.getMessage()));
//                    saveTaskLog(context, taskGroup, "parse statement exception", "error", logs.toString());
//                    throw new RuntimeException("parse statement exception:" + e.getMessage());
//                }
//                logger.info("execute task group [{}] , actual statement:{}", taskGroup.getTaskName(), statement);
//                logs.append(String.format("execute task group [%s] , actual statement:%s \n ", taskGroup.getTaskName(), statement));
//                StatementBody body = new StatementBody(JobUtil.getStatementKind(taskGroup.getContentType()).name(), statement);
//                String result = null;
//                try {
//                    StatementHandleImpl handle = client.sendStatement(body);
//                    result = handle.get();
//                    res = res && ("ok".equals(result));
//                    logs.append(String.format("result:%s \n", result));
//                    logs.append(handle.getEvalue() + " \n");
//                    Joiner.on("\n").appendTo(logs, handle.getTraceback() == null ? Lists.newArrayList() : handle.getTraceback());
//                    saveTaskLog(context, taskGroup, statement, result, logs.toString());
//                    if (!res && context != null) {
//                        context.fail(handle.getEvalue() + "\n");
//                    }
//                } catch (Exception e) {
//                    result = e.getMessage();
//                    logs.append(String.format("error:%s \n", result));
//                    saveTaskLog(context, taskGroup, statement, result, logs.toString());
//                    e.printStackTrace();
//                    // notify idc nodeJob fail
//                    if (context != null) {
//                        context.fail(e);
//                    }
//                }
//            }
//        }
////        Map<String, Object> sessionAppInfo = client.getSessionAppInfo();
////        if (!res) {
////            logger.error(String.format("execute task group %s , actual sql:%s", taskGroup.getTaskName(), client));
////            logger.error("spark ui : " + sessionAppInfo.get(Constants.LIVY_KEY_SESSION_SPARK_UI));
////            logger.error("driver log : " + sessionAppInfo.get(Constants.LIVY_KEY_SESSION_DRIVER_URL));
////            logger.error("log url:" + String.format("%s/ui/session/%d", livyAddress, client.getSessionId()));
////        }
//        client.stop(true);
//        return res;
//    }
//
//    private void saveTaskLog(IDCJobContext context, TaskGroup taskGroup, String sql, String result, String log) {
//        TaskLog taskLog = new TaskLog();
//        taskLog.setTaskId(taskGroup.getId());
//        taskLog.setExecParams(null);
//        taskLog.setExecContent(sql);
//        taskLog.setCreateTime(new Date());
//        if (context != null && context.getExecuteRequest() != null && context.getExecuteRequest() != null) {
//            taskLog.setInstanceId(context.getExecuteRequest().getNodeJobId());
//        }
//        taskLog.setLogs(log);
//        taskLog.setResult(result);
//        taskLogDao.save(taskLog);
//    }
//
//    private StatementClient createStatementClient() {
//
//        SysConfig config = sysConfigDao.findByKey(Configs.JOB_SPARK_TASK_CONFIG);
//        Properties prop = getProp(config);
//        try {
//            return new StatementClient(new URI(livyAddress), new HttpConf(prop));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static Properties getProp(SysConfig config) {
//        Properties prop = new Properties();
//        if (config == null) {
//            prop.setProperty("spark.executor.memory", "16G");
//            prop.setProperty("spark.executor.cores", "4");
//            prop.setProperty("spark.yarn.num-executors", "20");
//            prop.setProperty("spark.sql.shuffle.partitions", "10");
//            prop.setProperty("spark.yarn.queue", "root.users.model");
//        } else {
//            JSONObject conf = JSON.parseObject(config.getValue());
//            conf.keySet().forEach(key -> prop.setProperty(key, conf.getString(key)));
//        }
//
//        return prop;
//    }
//
//    private BatchClient createBatchClient() {
//        Properties prop = new Properties();
//        try {
//            return new BatchClient(new URI(livyAddress), new HttpConf(prop));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Map<String, String> createDefaultConf() {
//        return new HashMap<>();
//    }
//
//}
