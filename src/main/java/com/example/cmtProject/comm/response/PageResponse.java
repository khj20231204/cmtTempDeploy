package com.example.cmtProject.comm.response;

import java.util.List;
import org.springframework.data.domain.Page;

import lombok.Getter;

// 페이징 처리된 응답 클래스
@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int pageNo;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNo = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}

