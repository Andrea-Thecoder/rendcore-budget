package it.thecoder.rendcore.budget.dto;

import io.ebean.PagedList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter

public class PagedResultDTO <T>{

    private List<T> list;
    private long totalRows;
    private int totalPages;
    private int pageSize = 100;
    private int page = 1;



    @SuppressWarnings("java:S6204")
    public static <T, R> PagedResultDTO<R> of(PagedList<T> list, Function<? super T, ? extends R> mapper) {
        PagedResultDTO<R> pr = new PagedResultDTO<>();
        pr.setList(list.getList().stream().map(mapper).collect(Collectors.toList()));
        pr.setPage(list.getPageIndex() + 1);
        pr.setTotalPages(list.getTotalPageCount());
        pr.setTotalRows(list.getTotalCount());
        pr.setPageSize(list.getPageSize());
        return pr;
    }
}
