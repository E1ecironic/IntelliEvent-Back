package com.kevin.basecore.common.domin;

import java.util.List;

public class PageResult {

    private Long total;

    private List<?> list;

    private Integer totalPage;

    public PageResult(Long total, List<?> data) {
        this.total = total;
        this.list = data;
    }

    public PageResult(Long total, List<?> list, Integer totalPage) {
        this.total = total;
        this.list = list;
        this.totalPage = totalPage;
    }

    public static PageResult returnResult(Long total, List<?> list, Integer totalPage) {
        return new PageResult(total, list, totalPage);
    }

    public static PageResult returnResult(Long total, List<?> list, Long totalPage) {
        return new PageResult(total, list, totalPage.intValue());
    }

    public static PageResult returnResult(Long total, List<?> list) {
        return new PageResult(total, list);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getlist() {
        return list;
    }

    public void setlist(List<?> list) {
        this.list = list;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
