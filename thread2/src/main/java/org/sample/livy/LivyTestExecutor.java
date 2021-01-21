package org.sample.livy;//package com.iwellmass.etl.base.util.livy;
//
//import com.alibaba.fastjson.JSON;
//import com.google.common.base.Preconditions;
//import com.google.common.base.Throwables;
//import com.iwellmass.common.param.ExecParam;
//import com.iwellmass.common.util.Utils;
//import com.iwellmass.datafactory.common.Configs;
//import com.iwellmass.datafactory.common.Constants;
//import com.iwellmass.datafactory.entity.SysConfig;
//import com.iwellmass.datafactory.entity.TaskGroup;
//import com.iwellmass.datafactory.job.JobUtil;
//import com.iwellmass.datafactory.job.LogProcessor;
//import com.iwellmass.datafactory.job.ParamParser;
//import com.iwellmass.datafactory.job.TestExecutor;
//import com.iwellmass.datafactory.job.livy.entity.BatchBody;
//import com.iwellmass.datafactory.job.livy.entity.LogInfo;
//import com.iwellmass.datafactory.job.livy.entity.StatementBody;
//import com.iwellmass.datafactory.repo.SysConfigDao;
//import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.elasticsearch.common.Strings;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.inject.Inject;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by chenxh on 2018/8/6.
// */
//@Component
//public class LivyTestExecutor implements TestExecutor, InitializingBean, DisposableBean {
//
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Value("${livy.address}")
//    private String livyAddress;
//
//    @Inject
//    ParamParser paramParser;
//
//    GenericObjectPool<StatementClient> statementClientPool;
//
//    @Inject
//    SysConfigDao sysConfigDao;
//
//    private ExecutorService jobExecutorService = Executors.newFixedThreadPool(5);
//
//
//    @Override
//    public void executeTaskTest(TaskGroup taskGroup, Map<String, ExecParam> execParamMap, LogProcessor logProccesser) {
//        switch (taskGroup.getContentType()) {
//            case SCALA:
//            case DATA_CLEAN:
//            case SPARK_SQL:
//                executeStatement(taskGroup, execParamMap, logProccesser);
//                break;
//            case SPARK_APP:
//                executeBatch(taskGroup, execParamMap, logProccesser);
//                break;
//            case SELF_ANALYSIS:
//                executeAnalysis(taskGroup, execParamMap, logProccesser);
//                break;
//            default:
//        }
//    }
//
//    private void executeAnalysis(TaskGroup taskGroup, Map<String, ExecParam> execParams, LogProcessor logProcesser) {
//        logProcesser.proccessSingleLog("waiting......");
//
//        jobExecutorService.submit(() -> {
//                // 执行jar和类写死
//                BatchBody body = new BatchBody();
//                body.setFile("/var/job-jars/default/spark-jobs-1.0-SNAPSHOT.jar");
//                body.setClassName("com.iwellmass.sparks.models.bi.BIJobExecutor");
//                logProcesser.proccessSingleLog(String.format("start execute batch task[%s]", taskGroup.getTaskName()));
//
//                Map<String, String> conf = new HashMap<>();
//
//                // 添加业务参数
//                Map<String, String> valueMap = paramParser.calcExprs(execParams.keySet(), execParams);
//                execParams.forEach((key, value1) -> {
//                    String value = valueMap.get(key);
//                    conf.put("spark." + key, value);
//                    logProcesser.proccessSingleLog(String.format("params %s -> %s", key, value));
//                });
//
//                // 添加执行json
//                conf.put("spark.script", taskGroup.getContent());
//
//                body.setConf(conf);
//
//                doExecuteBatch(body, logProcesser);
//
//        });
//    }
//
//
//    private void executeBatch(TaskGroup taskGroup, Map<String, ExecParam> execParams, LogProcessor logProcesser) {
//
//        BatchBody body = JSON.parseObject(taskGroup.getContent(), BatchBody.class);
//        Map<String, String> conf = body.getConf();
//        logProcesser.proccessSingleLog(String.format("start execute batch task[%s]", taskGroup.getTaskName()));
//
//        Map<String, String> valueMap = paramParser.calcExprs(execParams.keySet(), execParams);
//        execParams.forEach((key, value1) -> {
//            String value = valueMap.get(key);
//            conf.put("spark." + key, value);
//            logProcesser.proccessSingleLog(String.format("params %s -> %s", key, value));
//        });
//        body.setConf(conf);
//
//        doExecuteBatch(body, logProcesser);
//    }
//
//    private void doExecuteBatch(BatchBody body, LogProcessor logProcesser) {
//        logProcesser.proccessSingleLog("waiting 。。。。。。。");
//        jobExecutorService.submit(() -> {
//            try {
//                BatchClient client = createBatchClient();
//                logProcesser.proccessSingleLog("executing");
//                BatchHandleImpl batchHandle = client.startBatch(body);
//                logProcesser.proccessSingleLog("log url:" + String.format("%s/ui/batch/%d/log", livyAddress, batchHandle.getBatchId()));
//                String s = batchHandle.get();
//                logProcesser.proccessSingleLog(String.format("execute end, result: %s ", s));
//                LogInfo logInfo = client.fetchAllLog();
//                logProcesser.proccessLog(logInfo.getLog());
//                logProcesser.proccessData(null, true);
//            } catch (Exception e) {
//                logger.error("执行异常:", e);
//                logProcesser.proccessSingleLog(Throwables.getStackTraceAsString(e));
//                logProcesser.proccessData(null, false);
//            }
//        });
//    }
//
//    private void executeStatement(TaskGroup taskGroup, Map<String, ExecParam> execParams, LogProcessor logProcesser) {
//        logProcesser.proccessSingleLog("waiting 。。。。。。。");
//        jobExecutorService.submit(() -> {
//            String[] sqlContents = taskGroup.getContent().split(";");
//            for (String content : sqlContents) {
//                if(Strings.isNullOrEmpty(Utils.trimBlank(content))) {
//                    continue;
//                }
//                StatementClient statementClient = null;
//                StatementBody body = null;
//                try {
//                    String statement = paramParser.parseSql(content, execParams);
//                    body = new StatementBody(JobUtil.getStatementKind(taskGroup.getContentType()).name(), statement);
//                    logProcesser.proccessSingleLog("start get session from pool");
//                    statementClient = statementClientPool.borrowObject();
//                    logProcesser.proccessSingleLog(String.format("geted session %s from pool", statementClient.toString()));
//                    logProcesser.proccessSingleLog(String.format("waiting session %s ready", statementClient.toString()));
//                    Preconditions.checkState(statementClient.waitSessionOk(),"获取session失败！");
//
//                    Map<String, Object> sessionAppInfo = statementClient.getSessionAppInfo();
//                    logProcesser.proccessSingleLog(String.format("execute task group %s , actual sql:%s", taskGroup.getTaskName(), statement));
//                    logProcesser.proccessSingleLog("spark ui : " + sessionAppInfo.get(Constants.LIVY_KEY_SESSION_SPARK_UI));
//                    logProcesser.proccessSingleLog("driver log : " + sessionAppInfo.get(Constants.LIVY_KEY_SESSION_DRIVER_URL));
//                    logProcesser.proccessSingleLog("log url:" + String.format("%s/ui/session/%d", livyAddress, statementClient.getSessionId()));
//                    StatementHandleImpl handle = statementClient.sendStatement(body);
//                    String result = handle.get();
//                    logProcesser.proccessSingleLog("execute result:" + result);
//                    if (result.equals("ok")) {
//                        Map<String, Object> data = handle.getData();
//                        logProcesser.proccessData(data,true);
//                    } else{
//                        logProcesser.proccessData(null,false);
//                    }
//                } catch (Exception e) {
//                    logger.error("执行SQL：{} 失败！",body,e);
//                    String trace = Throwables.getStackTraceAsString(e);
//                    logProcesser.proccessSingleLog(trace);
//                    logProcesser.proccessData("",false);
//                } finally {
//                    if (statementClient != null) {
//                        statementClientPool.returnObject(statementClient);
//                    }
////                logProcesser.closeConnection();
//                }
//            }
//        });
//
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
//    @Override
//    public void afterPropertiesSet() {
//
//        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//        config.setMaxTotal(4);
//        config.setMinIdle(1);
//        config.setTestOnBorrow(true);
//
//        SysConfig sparkConfig = sysConfigDao.findByKey(Configs.JOB_SPARK_TEST_CONFIG);
//        Properties sessionConf = LivyTaskExecutor.getProp(sparkConfig);
//
//        statementClientPool
//                = new GenericObjectPool<StatementClient>(new StatementClientFactory(livyAddress, sessionConf), config);
//    }
//
//    /**
//     * Invoked by a BeanFactory on destruction of a singleton.
//     *
//     */
//    @Override
//    public void destroy() {
//        if (statementClientPool != null) {
//            statementClientPool.clear();
//        }
//    }
//
//    private Map<String, String> createDefaultConf() {
//        return new HashMap<>();
//    }
//}
