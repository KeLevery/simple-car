package com.simplecar.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PageResponse<T> extends ApiResponse<T> {
    private List<T> rows;
    private Long total;

    public PageResponse() {
        super();
        this.setCode(200);
        this.setMsg("查询成功");
    }

    public static <T> PageResponse<T> success(List<T> rows, Long total) {
        PageResponse<T> response = new PageResponse<>();
        response.setRows(rows);
        response.setTotal(total);
        return response;
    }
}
