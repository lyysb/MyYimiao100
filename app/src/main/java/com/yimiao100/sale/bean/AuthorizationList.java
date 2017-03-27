package com.yimiao100.sale.bean;

import java.util.ArrayList;

/**
 * Created by Michel on 2017/3/23.
 */

public class AuthorizationList {


    private PagedResultBean pagedResult;

    private String status;

    public PagedResultBean getPagedResult() {
        return pagedResult;
    }

    public void setPagedResult(PagedResultBean pagedResult) {
        this.pagedResult = pagedResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class PagedResultBean {
        private int totalPage;
        private int pageSize;
        private int page;
        private ArrayList<Authorization> pagedList;

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

        public ArrayList<Authorization> getPagedList() {
            return pagedList;
        }

        public void setPagedList(ArrayList<Authorization> pagedList) {
            this.pagedList = pagedList;
        }

        public static class Authorization {
            private int id;
            private int userId;
            private int vendorId;
            private int categoryId;
            private int productId;
            private int specId;
            private int dosageFormId;
            private int provinceId;
            private String vendorName;
            private String provinceName;
            private String expressNo;
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

            public String getExpressNo() {
                return expressNo;
            }

            public void setExpressNo(String expressNo) {
                this.expressNo = expressNo;
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
