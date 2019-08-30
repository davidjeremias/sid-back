package br.com.caixa.sidce.interfaces.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.*;

public class PageableUtil {
	
	private static final String PAGE_KEY = "page";
    private static final String PAGELIMIT_KEY = "limit";
    private static final String SORT_KEY = "sort";

    private static final int PAGELIMIT_DEFAULT = 10;
    private static final String DESC_KEY = "desc";

	public static Pageable getPageRequest(Map<String, String[]> params) {
        int page = 0;
        int limit = 10;

        String[] pageParam = params.get(PAGE_KEY);
        if(pageParam != null) {
            page = Integer.parseInt(pageParam[0]);
            page = page < 0 ? 0 : page;
        }

        String[] limitParam = params.get(PAGELIMIT_KEY);
        if(limitParam != null) {
            limit = Integer.parseInt(limitParam[0]);
            limit = limit < 1 ? PAGELIMIT_DEFAULT : limit;
        }
        return PageRequest.of(page, limit, getSort(params));
    }
	
	public static Sort getSort(Map<String, String[]> params) {
        String[] sortParams = params.get(SORT_KEY);
        if(sortParams != null && sortParams.length > 0 ) {

            List<Order> orders = new ArrayList<>();
            for(String sort : sortParams) {

                Sort.Direction direction = Sort.Direction.ASC;
                if(params.get(sort + ".dir").length > 0 && params.get(sort + ".dir")[0].equalsIgnoreCase(DESC_KEY)) {
                    direction = Sort.Direction.DESC;
                }
                orders.add(new Order(direction, sort));
            }
            return Sort.by(orders);
        }
        return Sort.by(new Order(Sort.Direction.ASC, "id"));
    }
}
