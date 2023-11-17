package org.eu.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.eu.common.to.es.SkuEsModel;
import org.eu.mall.search.config.MallElasticSearchConfig;
import org.eu.mall.search.constant.EsConstant;
import org.eu.mall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 保存到es中
        //1、给es中建立索引。product，建立好映射关系。

        //2、给es中保存这些数据
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            // 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        // 如果批量有错误
        boolean hasFailures = bulk.hasFailures();
        List<Object> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        log.info("商品上架完成:{},返回数据:{}", collect, bulk.toString());
        return hasFailures;
    }
}
