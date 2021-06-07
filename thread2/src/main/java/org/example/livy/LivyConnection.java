/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.livy;

import org.apache.livy.shaded.apache.http.HttpEntity;
import org.apache.livy.shaded.apache.http.HttpHeaders;
import org.apache.livy.shaded.apache.http.HttpStatus;
import org.apache.livy.shaded.apache.http.client.config.RequestConfig;
import org.apache.livy.shaded.apache.http.client.methods.*;
import org.apache.livy.shaded.apache.http.entity.ByteArrayEntity;
import org.apache.livy.shaded.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.livy.shaded.apache.http.entity.mime.content.FileBody;
import org.apache.livy.shaded.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.livy.shaded.apache.http.impl.client.CloseableHttpClient;
import org.apache.livy.shaded.apache.http.impl.client.HttpClientBuilder;
import org.apache.livy.shaded.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.livy.shaded.apache.http.util.EntityUtils;
import org.apache.livy.shaded.jackson.annotation.JsonInclude;
import org.apache.livy.shaded.jackson.databind.DeserializationFeature;
import org.apache.livy.shaded.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.example.livy.HttpConf.Entry.*;


/**
 * Abstracts a connection to the Livy server; serializes multiple requests so that we only need
 * one active HTTP connection (to keep resource usage down).
 */
public class LivyConnection {

    static final String SESSIONS_URI = "/";
    private static final String APPLICATION_JSON = "application/json";

    private final URI server;
    private final String uriRoot;
    private final CloseableHttpClient client;
    private final ObjectMapper mapper;

    public LivyConnection(URI uri, final HttpConf config) {
        this.uriRoot = "/";

        RequestConfig reqConfig = new RequestConfig() {
            @Override
            public int getConnectTimeout() {
                return (int) config.getTimeAsMs(CONNECTION_TIMEOUT);
            }

            @Override
            public int getSocketTimeout() {
                return (int) config.getTimeAsMs(SOCKET_TIMEOUT);
            }

            @Override
            public boolean isAuthenticationEnabled() {
                return true;
            }

            @Override
            public boolean isContentCompressionEnabled() {
                return config.getBoolean(CONTENT_COMPRESS_ENABLE);
            }
        };


        HttpClientBuilder builder = HttpClientBuilder.create()
                .disableAutomaticRetries()
                .evictExpiredConnections()
                .evictIdleConnections(config.getTimeAsMs(CONNECTION_IDLE_TIMEOUT), TimeUnit.MILLISECONDS)
                .setConnectionManager(new BasicHttpClientConnectionManager())
                .setConnectionReuseStrategy(new NoConnectionReuseStrategy())
                .setDefaultRequestConfig(reqConfig)
                .setMaxConnTotal(1)
                .setUserAgent("livy-client-http");

        this.server = uri;
        this.client = builder.build();
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    synchronized void close() throws IOException {
        client.close();
    }

    synchronized <V> V delete(Class<V> retType, String uri, Object... uriParams) throws Exception {
        return sendJSONRequest(new HttpDelete(), retType, uri, uriParams);
    }

    synchronized <V> V get(Class<V> retType, String uri, Object... uriParams) throws Exception {
        return sendJSONRequest(new HttpGet(), retType, uri, uriParams);
    }

    synchronized <V> V get(Class<V> retType, String query, String uri, Object... uriParams) throws Exception {
        return sendJSONRequest(new HttpGet(), retType, query, uri, uriParams);
    }

    synchronized <V> V post(
            Object body,
            Class<V> retType,
            String uri,
            Object... uriParams) throws Exception {
        HttpPost post = new HttpPost();
        if (body != null) {
            byte[] bodyBytes = mapper.writeValueAsBytes(body);
            post.setEntity(new ByteArrayEntity(bodyBytes));
        }
        return sendJSONRequest(post, retType, uri, uriParams);
    }

    synchronized <V> V post(
            File f,
            Class<V> retType,
            String paramName,
            String uri,
            Object... uriParams) throws Exception {
        HttpPost post = new HttpPost();
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(paramName, new FileBody(f));
        post.setEntity(builder.build());
        return sendRequest(post, retType, null, uri, uriParams);
    }

    private <V> V sendJSONRequest(
            HttpRequestBase req,
            Class<V> retType,
            String uri,
            Object... uriParams) throws Exception {
        req.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
        req.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        req.setHeader(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return sendRequest(req, retType, null, uri, uriParams);
    }

    private <V> V sendJSONRequest(
            HttpRequestBase req,
            Class<V> retType,
            String query,
            String uri,
            Object... uriParams) throws Exception {
        req.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);
        req.setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
        req.setHeader(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return sendRequest(req, retType, query, uri, uriParams);
    }

    private <V> V sendRequest(
            HttpRequestBase req,
            Class<V> retType,
            String query,
            String uri,
            Object... uriParams) throws Exception {
        req.setURI(new URI(server.getScheme(), null, server.getHost(), server.getPort(),
                uriRoot + String.format(uri, uriParams), query, null));
        // It is no harm to set X-Requested-By when csrf protection is disabled.
        if (req instanceof HttpPost || req instanceof HttpDelete || req instanceof HttpPut
                || req instanceof HttpPatch) {
            req.addHeader("X-Requested-By", "org/example/livy");
        }
        try (CloseableHttpResponse res = client.execute(req)) {
            int status = (res.getStatusLine().getStatusCode() / 100) * 100;
            HttpEntity entity = res.getEntity();
            if (status == HttpStatus.SC_OK) {
                if (!Void.class.equals(retType)) {
                    return mapper.readValue(entity.getContent(), retType);
                } else {
                    return null;
                }
            } else {
                String error = EntityUtils.toString(entity);
                throw new IOException(String.format("%s: %s", res.getStatusLine().getReasonPhrase(), error));
            }
        }
    }

}
