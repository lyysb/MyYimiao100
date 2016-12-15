package com.yimiao100.sale.bean;

/**
 * 资源列表
 * Created by 亿苗通 on 2016/8/19.
 */
public class ResourceBean {

    /**
     * pagedList : [{"id":3,"vendorId":3,"productId":4,"provinceId":2,"cityId":3,"areaId":20,"startTime":1470758400000,"endTime":1471276799000,"bidDeposit":1000,"totalDeposit":5000,"quota":3000,"unitPrice":120,"deliveryPrice":100,"paymentPrice":110,"incompletePercent":10,"reducePercent":20,"policyContent":"11111111111","protocolFilePath":"2016/08/18/7b0e7a296f75418c8dd9cebb2b619ea1.dot","protocolFileUrl":"http://obkybrxiz.bkt.clouddn.com/2016/08/18/7b0e7a296f75418c8dd9cebb2b619ea1.dot","expiredAt":1472054399000,"createdAt":1471499959000,"updatedAt":1471499959000,"vendorName":"测试厂家-3","vendorLogoImageUrl":"http://obklzicwf.bkt.clouddn.com/2016/08/08/c625042daaf4478d9b43b9db4b82d2ee.jpg","productFormalName":"厂家3-产品1","productCommonName":"厂家3-产品1","spec":"规格-1","dosageForm":"规格-1","provinceName":"天津市","cityName":"市辖区","areaName":"河东区"},{"id":1,"vendorId":3,"productId":4,"provinceId":1,"cityId":1,"areaId":1,"startTime":1470758400000,"endTime":1471449599000,"bidDeposit":1000,"totalDeposit":7000,"quota":8000,"unitPrice":300,"deliveryPrice":200,"paymentPrice":230,"incompletePercent":80,"reducePercent":10,"policyContent":"推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策","protocolFilePath":"2016/08/18/aaf9647483d14ab49616d85b42cb0364.dot","protocolFileUrl":"http://obkybrxiz.bkt.clouddn.com/2016/08/18/aaf9647483d14ab49616d85b42cb0364.dot","expiredAt":1472227199000,"createdAt":1471499844000,"updatedAt":1471499844000,"vendorName":"测试厂家-3","vendorLogoImageUrl":"http://obklzicwf.bkt.clouddn.com/2016/08/08/c625042daaf4478d9b43b9db4b82d2ee.jpg","productFormalName":"厂家3-产品1","productCommonName":"厂家3-产品1","spec":"规格-1","dosageForm":"规格-1","provinceName":"北京市","cityName":"市辖区","areaName":"东城区"}]
     * totalPage : 1
     * pageSize : 10
     * page : 0
     */

    private ResourceResultBean pagedResult;
    /**
     * pagedResult : {"pagedList":[{"id":3,"vendorId":3,"productId":4,"provinceId":2,"cityId":3,"areaId":20,"startTime":1470758400000,"endTime":1471276799000,"bidDeposit":1000,"totalDeposit":5000,"quota":3000,"unitPrice":120,"deliveryPrice":100,"paymentPrice":110,"incompletePercent":10,"reducePercent":20,"policyContent":"11111111111","protocolFilePath":"2016/08/18/7b0e7a296f75418c8dd9cebb2b619ea1.dot","protocolFileUrl":"http://obkybrxiz.bkt.clouddn.com/2016/08/18/7b0e7a296f75418c8dd9cebb2b619ea1.dot","expiredAt":1472054399000,"createdAt":1471499959000,"updatedAt":1471499959000,"vendorName":"测试厂家-3","vendorLogoImageUrl":"http://obklzicwf.bkt.clouddn.com/2016/08/08/c625042daaf4478d9b43b9db4b82d2ee.jpg","productFormalName":"厂家3-产品1","productCommonName":"厂家3-产品1","spec":"规格-1","dosageForm":"规格-1","provinceName":"天津市","cityName":"市辖区","areaName":"河东区"},{"id":1,"vendorId":3,"productId":4,"provinceId":1,"cityId":1,"areaId":1,"startTime":1470758400000,"endTime":1471449599000,"bidDeposit":1000,"totalDeposit":7000,"quota":8000,"unitPrice":300,"deliveryPrice":200,"paymentPrice":230,"incompletePercent":80,"reducePercent":10,"policyContent":"推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策推广政策","protocolFilePath":"2016/08/18/aaf9647483d14ab49616d85b42cb0364.dot","protocolFileUrl":"http://obkybrxiz.bkt.clouddn.com/2016/08/18/aaf9647483d14ab49616d85b42cb0364.dot","expiredAt":1472227199000,"createdAt":1471499844000,"updatedAt":1471499844000,"vendorName":"测试厂家-3","vendorLogoImageUrl":"http://obklzicwf.bkt.clouddn.com/2016/08/08/c625042daaf4478d9b43b9db4b82d2ee.jpg","productFormalName":"厂家3-产品1","productCommonName":"厂家3-产品1","spec":"规格-1","dosageForm":"规格-1","provinceName":"北京市","cityName":"市辖区","areaName":"东城区"}],"totalPage":1,"pageSize":10,"page":0}
     * status : success
     */

    private String status;

    public ResourceResultBean getResourceResult() {
        return pagedResult;
    }

    public void setPagedResult(ResourceResultBean pagedResult) {
        this.pagedResult = pagedResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
