package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * 不良反应申报列表
 * Created by Michel on 2017/3/22.
 */

public class AdverseList {
    private String status;
    private int reason;
    private PagedResult pagedResult;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public PagedResult getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(PagedResult pagedResult) {
        this.pagedResult = pagedResult;
    }

    public static class PagedResult {
        private int totalPage;
        private int pageSize;
        private int page;
        private ArrayList<Adverse> pagedList;

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public ArrayList<Adverse> getPagedList() {
            return pagedList;
        }

        public void setPagedList(ArrayList<Adverse> pagedList) {
            this.pagedList = pagedList;
        }

        public static class Adverse {
            private int id;
            private int userId;
            private int vendorId;
            private int categoryId;
            private int productId;
            private int specId;
            private int dosageFormId;
            private int provinceId;
            private int cityId;
            private int areaId;
            private String vendorName;
            private String provinceName;
            private String cityName;
            private String areaName;
            private long createdAt;
            private long updatedAt;
            private String applyStatus;
            private String applyStatusName;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getVendorId() {
                return vendorId;
            }

            public void setVendorId(int vendorId) {
                this.vendorId = vendorId;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public int getSpecId() {
                return specId;
            }

            public void setSpecId(int specId) {
                this.specId = specId;
            }

            public int getDosageFormId() {
                return dosageFormId;
            }

            public void setDosageFormId(int dosageFormId) {
                this.dosageFormId = dosageFormId;
            }

            public int getProvinceId() {
                return provinceId;
            }

            public void setProvinceId(int provinceId) {
                this.provinceId = provinceId;
            }

            public int getCityId() {
                return cityId;
            }

            public void setCityId(int cityId) {
                this.cityId = cityId;
            }

            public int getAreaId() {
                return areaId;
            }

            public void setAreaId(int areaId) {
                this.areaId = areaId;
            }

            public String getVendorName() {
                return vendorName;
            }

            public void setVendorName(String vendorName) {
                this.vendorName = vendorName;
            }

            public String getProvinceName() {
                return provinceName;
            }

            public void setProvinceName(String provinceName) {
                this.provinceName = provinceName;
            }

            public String getCityName() {
                return cityName;
            }

            public void setCityName(String cityName) {
                this.cityName = cityName;
            }

            public String getAreaName() {
                return areaName;
            }

            public void setAreaName(String areaName) {
                this.areaName = areaName;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public long getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
            }

            public String getApplyStatus() {
                return applyStatus;
            }

            public void setApplyStatus(String applyStatus) {
                this.applyStatus = applyStatus;
            }

            public String getApplyStatusName() {
                return applyStatusName;
            }

            public void setApplyStatusName(String applyStatusName) {
                this.applyStatusName = applyStatusName;
            }
        }
    }

}
