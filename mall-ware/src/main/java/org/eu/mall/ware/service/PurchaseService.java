package org.eu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.ware.entity.PurchaseEntity;
import org.eu.mall.ware.vo.MergeVo;

import java.util.Map;

/**
 * 采购信息
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 19:04:03
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void purchaseMerge(MergeVo mergeVo);
}

