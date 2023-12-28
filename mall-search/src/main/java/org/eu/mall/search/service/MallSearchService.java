package org.eu.mall.search.service;

import org.eu.mall.search.vo.SearchParam;
import org.eu.mall.search.vo.SearchResult;

public interface MallSearchService {

    SearchResult search(SearchParam param);
}
