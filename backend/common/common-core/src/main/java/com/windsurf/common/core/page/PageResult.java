package com.windsurf.common.core.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private int pageNum;

    /** 每页记录数 */
    private int pageSize;

    /** 总页数 */
    private int pages;

    /** 数据列表 */
    private List<T> list;

    /**
     * 构造方法
     */
    public PageResult() {
    }

    /**
     * 构造方法
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     */
    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 构造方法
     *
     * @param pageQuery 分页查询参数
     * @param list      数据列表
     * @param total     总记录数
     */
    public PageResult(PageQuery pageQuery, List<T> list, long total) {
        this(list, total, pageQuery.getPageNum(), pageQuery.getPageSize());
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return this.pageNum > 1;
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return this.pageNum < this.pages;
    }
}
