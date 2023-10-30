package org.eu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 数据传输对象
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
