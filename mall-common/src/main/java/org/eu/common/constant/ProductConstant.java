package org.eu.common.constant;

public class ProductConstant {
    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        private int code;

        public int getCode() {
            return code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        private String msg;

    }

    public enum StatusEnum {
        NEW_SPU(0, "新建"),
        SPU_UP(1, "商品上架"),
        SPU_DOWN(2, "商品下架");

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        private int code;

        public int getCode() {
            return code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        private String msg;
    }
}
