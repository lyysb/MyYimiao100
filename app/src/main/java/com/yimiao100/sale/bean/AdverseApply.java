package com.yimiao100.sale.bean;

import java.util.List;

/**
 * 不良反应申报
 * Created by Michel on 2017/3/22.
 */

public class AdverseApply {

    private String status;
    private List<BizListBean> bizList;
    private List<RegionListBean> regionList;

    private AdverseApplyBean adverseApply;

    public AdverseApplyBean getAdverseApply() {
        return adverseApply;
    }

    public void setAdverseApply(AdverseApplyBean adverseApply) {
        this.adverseApply = adverseApply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BizListBean> getBizList() {
        return bizList;
    }

    public void setBizList(List<BizListBean> bizList) {
        this.bizList = bizList;
    }

    public List<RegionListBean> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<RegionListBean> regionList) {
        this.regionList = regionList;
    }

    public static class AdverseApplyBean {
        private String accountNumber;
        private String adverseDesc;
        private String applyStatus;
        private String applyStatusName;
        private int areaId;
        private String areaName;
        private int categoryId;
        private String categoryName;
        private int cityId;
        private String cityName;
        private String contactEmail;
        private String contactName;
        private String contactPhoneNumber;
        private long createdAt;
        private String diagnosticFilePath;
        private String diagnosticFileUrl;
        private String diagnosticHospital;
        private String diagnosticResult;
        private String dosageForm;
        private int dosageFormId;
        private int id;
        private long injectAt;
        private String patientAge;
        private String patientName;
        private String patientSex;
        private String patientSexName;
        private int productId;
        private String productName;
        private int provinceId;
        private String provinceName;
        private String spec;
        private int specId;
        private long updatedAt;
        private int userId;
        private int vendorId;
        private String vendorName;

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getAdverseDesc() {
            return adverseDesc;
        }

        public void setAdverseDesc(String adverseDesc) {
            this.adverseDesc = adverseDesc;
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

        public int getAreaId() {
            return areaId;
        }

        public void setAreaId(int areaId) {
            this.areaId = areaId;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactPhoneNumber() {
            return contactPhoneNumber;
        }

        public void setContactPhoneNumber(String contactPhoneNumber) {
            this.contactPhoneNumber = contactPhoneNumber;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public String getDiagnosticFilePath() {
            return diagnosticFilePath;
        }

        public void setDiagnosticFilePath(String diagnosticFilePath) {
            this.diagnosticFilePath = diagnosticFilePath;
        }

        public String getDiagnosticFileUrl() {
            return diagnosticFileUrl;
        }

        public void setDiagnosticFileUrl(String diagnosticFileUrl) {
            this.diagnosticFileUrl = diagnosticFileUrl;
        }

        public String getDiagnosticHospital() {
            return diagnosticHospital;
        }

        public void setDiagnosticHospital(String diagnosticHospital) {
            this.diagnosticHospital = diagnosticHospital;
        }

        public String getDiagnosticResult() {
            return diagnosticResult;
        }

        public void setDiagnosticResult(String diagnosticResult) {
            this.diagnosticResult = diagnosticResult;
        }

        public String getDosageForm() {
            return dosageForm;
        }

        public void setDosageForm(String dosageForm) {
            this.dosageForm = dosageForm;
        }

        public int getDosageFormId() {
            return dosageFormId;
        }

        public void setDosageFormId(int dosageFormId) {
            this.dosageFormId = dosageFormId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getInjectAt() {
            return injectAt;
        }

        public void setInjectAt(long injectAt) {
            this.injectAt = injectAt;
        }

        public String getPatientAge() {
            return patientAge;
        }

        public void setPatientAge(String patientAge) {
            this.patientAge = patientAge;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getPatientSex() {
            return patientSex;
        }

        public void setPatientSex(String patientSex) {
            this.patientSex = patientSex;
        }

        public String getPatientSexName() {
            return patientSexName;
        }

        public void setPatientSexName(String patientSexName) {
            this.patientSexName = patientSexName;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public int getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(int provinceId) {
            this.provinceId = provinceId;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public int getSpecId() {
            return specId;
        }

        public void setSpecId(int specId) {
            this.specId = specId;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
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

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
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
            private String categoryName;
            private int id;
            private List<ProductListBean> productList;

            @Override
            public String toString() {
                return categoryName;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public List<ProductListBean> getProductList() {
                return productList;
            }

            public void setProductList(List<ProductListBean> productList) {
                this.productList = productList;
            }

            public static class ProductListBean {
                private String dosageForm;
                private int dosageFormId;
                private int id;
                private String productName;
                private String spec;
                private int specId;

                @Override
                public String toString() {
                    return productName;
                }

                public String getDosageForm() {
                    return dosageForm;
                }

                public void setDosageForm(String dosageForm) {
                    this.dosageForm = dosageForm;
                }

                public int getDosageFormId() {
                    return dosageFormId;
                }

                public void setDosageFormId(int dosageFormId) {
                    this.dosageFormId = dosageFormId;
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

                public String getSpec() {
                    return spec;
                }

                public void setSpec(String spec) {
                    this.spec = spec;
                }

                public int getSpecId() {
                    return specId;
                }

                public void setSpecId(int specId) {
                    this.specId = specId;
                }
            }
        }
    }

    public static class RegionListBean {
        private String code;
        private int id;
        private String name;
        private List<CityListBean> cityList;

        @Override
        public String toString() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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
            private String code;
            private int id;
            private String name;
            private String provinceCode;

            @Override
            public String toString() {
                return name;
            }

            private List<AreaListBean> areaList;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
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

            public String getProvinceCode() {
                return provinceCode;
            }

            public void setProvinceCode(String provinceCode) {
                this.provinceCode = provinceCode;
            }

            public List<AreaListBean> getAreaList() {
                return areaList;
            }

            public void setAreaList(List<AreaListBean> areaList) {
                this.areaList = areaList;
            }

            public static class AreaListBean {
                private String cityCode;
                private String code;
                private int id;
                private String name;

                @Override
                public String toString() {
                    return name;
                }

                public String getCityCode() {
                    return cityCode;
                }

                public void setCityCode(String cityCode) {
                    this.cityCode = cityCode;
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
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
            }
        }
    }
}
