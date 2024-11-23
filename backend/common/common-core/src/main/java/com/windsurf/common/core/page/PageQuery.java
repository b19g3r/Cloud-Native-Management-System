package com.windsurf.common.core.page;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUM = 1;
    /** 默认每页记录数 */
    public static final int DEFAULT_PAGE_SIZE = 10;
    /** 最大每页记录数 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 页码 */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = DEFAULT_PAGE_NUM;

    /** 每页记录数 */
    @Min(value = 1, message = "每页记录数不能小于1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /** 排序字段 */
    private String orderBy;

    /** 排序方式：ascending/descending */
    private String orderDirection;

    /**
     * 获取当前页的偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 校验分页参数，并返回修正后的分页参数
     */
    public PageQuery validate() {
        // 校验pageNum
        if (this.pageNum == null || this.pageNum < 1) {
            this.pageNum = DEFAULT_PAGE_NUM;
        }

        // 校验pageSize
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
        if (this.pageSize > MAX_PAGE_SIZE) {
            this.pageSize = MAX_PAGE_SIZE;
        }

        return this;
    }
}
