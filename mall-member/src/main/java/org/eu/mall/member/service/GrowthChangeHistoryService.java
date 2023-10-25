package org.eu.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:48:06
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

