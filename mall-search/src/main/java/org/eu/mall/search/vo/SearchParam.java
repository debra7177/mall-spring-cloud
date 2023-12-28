package org.eu.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 *
 *  catalog3Id=225&keyword=小米&sort=saleCount_asc&hasStock=0/1&brandId=1&brandId=2
 *  &attrs=1_5寸:8寸&attrs=2_16G:8G
 */
@Data
public class SearchParam {
    // 全文匹配关键字
    private String keyword;
    // 三级分类id
    private Long catalog3Id;
    // 排序字段
    private String sort;
    // 是否有货
    private Integer hasStock;
    // 品牌id
    private List<Long> brandId;
    // 价格区间查询
    private String skuPrice;
    // 按照属性
    private List<String> attrs;
    // 页码
    private Integer pageNum = 1;
    // 原生的所有查询条件
    private String _queryString;
}
