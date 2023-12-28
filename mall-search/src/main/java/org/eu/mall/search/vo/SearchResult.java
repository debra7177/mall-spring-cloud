package org.eu.mall.search.vo;

import lombok.Data;
import org.eu.common.to.es.SkuEsModel;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    // 查询所有商品信息
    private List<SkuEsModel> products;

    // 分页信息
    //当前页码
    private Integer pageNum;
    // 总记录数
    private Long total;
    // 总页数
    private Integer totalPages;
    private List<Integer> pageNavs;
    // 当前查询到的结果，所有涉及到的品牌
    private List<BrandVo> brands;
    // 当前查询到的结果，所有涉及到的分类
    private List<CatalogVo> catalogs;
    // 当前查询到的结果，所有涉及到的属性
    private List<AttrVo> attrs;

    // 面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
}
