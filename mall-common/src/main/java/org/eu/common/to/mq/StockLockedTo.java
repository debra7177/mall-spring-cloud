package org.eu.common.to.mq;

import lombok.Data;

@Data
public class StockLockedTo {

    private Long id; //库存工作单的id
    private StockDetailTo detail;//工作单详情
}
