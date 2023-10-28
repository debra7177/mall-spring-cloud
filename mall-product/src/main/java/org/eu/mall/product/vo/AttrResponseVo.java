package org.eu.mall.product.vo;

import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo {
    /**
     * 所属三级分类名称
     */
    private String catelogName;

    /**
     * 所属属性分组名称
     */
    private String groupName;

    /**
     * 分类路径 [2, 34, 225]
     */
    private Long[] catelogPath;

}
