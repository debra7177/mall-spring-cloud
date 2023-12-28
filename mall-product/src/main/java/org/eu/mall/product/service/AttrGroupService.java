package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.AttrGroupEntity;
import org.eu.mall.product.vo.AttrGroupWithAttrsVo;
import org.eu.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-27 00:34:58
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, long cateLogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsBycatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

