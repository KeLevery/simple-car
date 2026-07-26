package com.simplecar.result;

import java.util.List;

/**
 * Service 层分页结果载体，Controller 转换为 PageResponse 返回。
 */
public record PagedData<T>(List<T> rows, long total) {
}
