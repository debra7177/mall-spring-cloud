/**
  * Copyright 2023 bejson.com
  */
package org.eu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Auto-generated: 2023-10-30 4:27:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MemberPrice {

    /**
     * 会员等级 level
     */
    private Long id;
    /**
     * 会员等级名称
     */
    private String name;
    /**
     * 会员等级价格
     */
    private BigDecimal price;
}
