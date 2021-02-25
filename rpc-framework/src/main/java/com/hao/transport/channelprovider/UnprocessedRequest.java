package com.hao.transport.channelprovider;

import com.hao.transport.dto.RPCResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理未完成请求，有关CompletableFuture的详细使用可见 https://javadoop.com/post/completable-future
 */
public class UnprocessedRequest {
    private static final Map<String, CompletableFuture<RPCResponse<Object>>> UNPROCESSED_RESPONSE_FUTURE = new ConcurrentHashMap<>();

    public void put(String requestID, CompletableFuture<RPCResponse<Object>> future) {

        UNPROCESSED_RESPONSE_FUTURE.put(requestID, future);

    }

    public void complete(RPCResponse<Object> response) {

        CompletableFuture<RPCResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURE.remove(response);
        if (future != null) {
            future.complete(response);
        }else {
            throw new IllegalStateException();
        }
    }



}
