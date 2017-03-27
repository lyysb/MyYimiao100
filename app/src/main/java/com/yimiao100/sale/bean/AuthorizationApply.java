package com.yimiao100.sale.bean;

import java.util.List;

/**
 * Created by Michel on 2017/3/23.
 */

public class AuthorizationApply {

    private String status;
    private List<RegionListBean> regionList;
    private List<BizListBean> bizList;
    private AuthzApplyBean authzApply;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RegionListBean> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<RegionListBean> regionList) {
        this.regionList = regionList;
    }

    public List<BizListBean> getBizList() {
        return bizList;
    }

    public void setBizList(List<BizListBean> bizList) {
        this.bizList = bizList;
    }

    public AuthzApplyBean getAuthzApply() {
        return authzApply;
    }

    public void setAuthzApply(AuthzApplyBean authzApply) {
        this.authzApply = authzApply;
    }

    public static class RegionListBean {
        private int id;
        private String name;
        private List<CityListBean> cityList;

        @Override
        public String toString() {
            return name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CityListBean> getCityList() {
            return cityList;
        }

        public void setCityList(List<CityListBean> cityList) {
            this.cityList = cityList;
        }

        public static class CityListBean {
            private int id;
            private String name;
            private List<AreaListBean> areaList;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<AreaListBean> getAreaList() {
                return areaList;
            }

            public void setAreaList(List<AreaListBean> areaList) {
                this.areaList = areaList;
            }

            public static class AreaListBean {
                private int id;
                private String name;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }
        }
    }

    public static class BizListBean {
        private int id;
        private String vendorName;

        private List<CategoryListBean> categoryList;

        @Override
        public String toString() {
            return vendorName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public List<CategoryListBean> getCategoryList() {
            return categoryList;
        }

        public void setCategoryList(List<CategoryListBean> categoryList) {
            this.categoryList = categoryList;
        }

        public static class CategoryListBean {
            private int id;
            private String categoryName;
            private List<ProductListBean> productList;

            @Override
            public String toString() {
                return categoryName;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public List<ProductListBean> getProductList() {
                return productList;
            }

            public void setProductList(List<ProductListBean> productList) {
                this.productList = productList;
            }

            public static class ProductListBean {
                private int id;
                private String productName;
                private int specId;
                private int dosageFormId;
                private String spec;
                private String dosageForm;

                @Override
                public String toString() {
                    return productName;
                }

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getProductName() {
                    return productName;
                }

                public void setProductName(String productName) {
                    this.productName = productName;
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

                public String getSpec() {
                    return spec;
                }

                public void setSpec(String spec) {
                    this.spec = spec;
                }

                public String getDosageForm() {
                    return dosageForm;
                }

                public void setDosageForm(String dosageForm) {
                    this.dosageForm = dosageForm;
                }
            }
        }
    }

    public static class AuthzApplyBean {
        private int id;
        private int userId;
        private int vendorId;
        private int categoryId;
        private int productId;
        private int specId;
        private int dosageFormId;
        private int provinceId;
        private String regionRemark;
        private String qualificationNum;
        private String contractNum;
        private String agreementNum;
        private String authzNum;
        private String authzCopyNum;
        private String productDescNum;
        private String bizLicenseCopyNum;
        private String remark;
        private String cnName;
        private String phoneNumber;
        private String fullAddress;
        private String expressNo;
        private String applyStatus;
        private String applyStatusName;
        private long createdAt;
        private long updatedAt;
        private String accountNumber;
        private String vendorName;
        private String categoryName;
        private String productName;
        private String spec;
        private String dosageForm;
        private String provinceName;

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

        public String getRegionRemark() {
            return regionRemark;
        }

        public void setRegionRemark(String regionRemark) {
            this.regionRemark = regionRemark;
        }

        public String getQualificationNum() {
            return qualificationNum;
        }

        public void setQualificationNum(String qualificationNum) {
            this.qualificationNum = qualificationNum;
        }

        public String getContractNum() {
            return contractNum;
        }

        public void setContractNum(String contractNum) {
            this.contractNum = contractNum;
        }

        public String getAgreementNum() {
            return agreementNum;
        }

        public void setAgreementNum(String agreementNum) {
            this.agreementNum = agreementNum;
        }

        public String getAuthzNum() {
            return authzNum;
        }

        public void setAuthzNum(String authzNum) {
            this.authzNum = authzNum;
        }

        public String getAuthzCopyNum() {
            return authzCopyNum;
        }

        public void setAuthzCopyNum(String authzCopyNum) {
            this.authzCopyNum = authzCopyNum;
        }

        public String getProductDescNum() {
            return productDescNum;
        }

        public void setProductDescNum(String productDescNum) {
            this.productDescNum = productDescNum;
        }

        public String getBizLicenseCopyNum() {
            return bizLicenseCopyNum;
        }

        public void setBizLicenseCopyNum(String bizLicenseCopyNum) {
            this.bizLicenseCopyNum = bizLicenseCopyNum;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCnName() {
            return cnName;
        }

        public void setCnName(String cnName) {
            this.cnName = cnName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getFullAddress() {
            return fullAddress;
        }

        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }

        public Object getExpressNo() {
            return expressNo;
        }

        public void setExpressNo(String expressNo) {
            this.expressNo = expressNo;
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

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getDosageForm() {
            return dosageForm;
        }

        public void setDosageForm(String dosageForm) {
            this.dosageForm = dosageForm;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }
    }
}
