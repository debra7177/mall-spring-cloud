package org.eu.mall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.eu.mall.product.entity.AttrEntity;

import java.util.List;

/**
 * 属性分组 - 属性值对象
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 属性列表
     */
    private List<AttrEntity> attrs;
}
