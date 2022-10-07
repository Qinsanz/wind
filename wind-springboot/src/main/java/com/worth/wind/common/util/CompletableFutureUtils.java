package com.worth.wind.common.util;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture的一些包装
 */
@Log4j2
public class CompletableFutureUtils {
    /**
     * 运行异步
     *
     * @param r r
     * @return
     */
    public static CompletableFuture<Void> runAsync(Runnable r, String message) {
        return CompletableFuture.runAsync(r).exceptionally(e -> {
            log.error(message, e);
            return null;
        });
    }
}
