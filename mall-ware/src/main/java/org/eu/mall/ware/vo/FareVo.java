package org.eu.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 运费vo
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
