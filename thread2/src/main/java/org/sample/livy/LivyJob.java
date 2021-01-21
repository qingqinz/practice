package org.sample.livy;//package com.smarteast.review.server.controller.sparkCore.livy;
//
//
//import com.google.common.base.Joiner;
//import com.smarteast.review.server.controller.sparkCore.livy.entity.BatchBody;
//import com.smarteast.review.server.controller.sparkCore.livy.entity.StatementBody;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ExecutionException;
//
///**
// * Created by chenxh on 2018/7/24.
// */
//public class LivyJob {
//
//    public static void main(String[] args) throws URISyntaxException {
//        String livyUrl = "http://10.1.1.154:8998";
//        testSession(livyUrl);
////        testBatch(livyUrl);
//    }
//
//    private static void testBatch(String livyUrl) throws URISyntaxException, ExecutionException, InterruptedException {
//        BatchClient client = new BatchClient(new URI(livyUrl), new HttpConf(new Properties()));
//        Map<String, String> conf = new HashMap<>();
//        conf.put("spark.load_date", "20180430");
//        BatchBody body = new BatchBody();
//        body.setConf(conf);
//        body.setFile("/var/job-jars/default/spark-jobs-1.0-SNAPSHOT.jar");
//        body.setClassName("com.iwellmass.sparks.chains.FundChainDataApplication");
//        body.getJars().add("/var/job-jars/default/mysql-connector-java.jar");
//        System.out.println(client.startBatch(body).get());
//
//        System.out.println(Joiner.on("\n").join(client.fetchAllLog().getLog()));
//    }
//
//    private static void testSession(String livyUrl) throws URISyntaxException {
//        Properties prop = new Properties();
////        prop.setProperty("kind", "sql");
//        StatementClient client = new StatementClient(new URI(livyUrl), new HttpConf(prop));
//
//        boolean b = client.waitSessionOk();
//        System.out.println(b);
//        if (!b) {
//            return;
//        }
//        try {
//            StatementBody body = new StatementBody();
//            body.setKind("sql");
//            body.setCode("select * from studentno");
//            StatementHandleImpl handle = client.sendStatement(body);
//            String result = handle.get();
//
//            System.out.println("result1:" + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
////            client.stop(true);
//        }
//
//    }
//
//
//}
//
//
