package org.eu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.to.mq.OrderTo;
import org.eu.common.to.mq.StockLockedTo;
import org.eu.common.utils.PageUtils;
import org.eu.mall.ware.entity.WareSkuEntity;
import org.eu.common.to.SkuHasStockVo;
import org.eu.mall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 19:04:03
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    /**
     * 锁定库存
     *
     * @param vo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo vo);

    /**
     * 查询某个sku在哪个仓库有库存
     *
     * @param skuId
     * @return
     */
    List<Long> listWareIdHasSkuStock(Long skuId);

    /**
     * 解锁库存
     *
     * @param to
     */
    void unlockStock(StockLockedTo to);


    void unlockStock(OrderTo orderTo);
}

