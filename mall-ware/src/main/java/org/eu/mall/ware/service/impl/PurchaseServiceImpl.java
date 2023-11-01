package org.eu.mall.ware.service.impl;

import org.eu.common.constant.WareConstant;
import org.eu.mall.ware.entity.PurchaseDetailEntity;
import org.eu.mall.ware.service.PurchaseDetailService;
import org.eu.mall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.ware.dao.PurchaseDao;
import org.eu.mall.ware.entity.PurchaseEntity;
import org.eu.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void purchaseMerge(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId != null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream()
                .map(item -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(item);
                    detailEntity.setPurchaseId(finalPurchaseId);
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }
}
