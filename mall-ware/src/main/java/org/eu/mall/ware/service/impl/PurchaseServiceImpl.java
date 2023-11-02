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
        // 确认采购单状态是0或者1才可以合并
        PurchaseEntity getById = getById(purchaseId);
        if (!getById.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode())
                && !getById.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())) {
            return;
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

    @Override
    public void received(List<Long> ids) {
        // 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream()
                .map(this::getById).filter(item -> {
                    return item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode();
                }).map(item -> {
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                }).collect(Collectors.toList());
        // 改变采购单的状态
        if (collect.size() > 0) {
            updateBatchById(collect);
        }
        // 改变采购项的状态
        collect.forEach(item ->{
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream()
                    .map(entity -> {
                        PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                        purchaseDetailEntity.setId(entity.getId());
                        purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                        return purchaseDetailEntity;
                    }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }
}
