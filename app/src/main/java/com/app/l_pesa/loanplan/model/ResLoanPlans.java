package com.app.l_pesa.loanplan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResLoanPlans {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private ArrayList<Datum> data = null;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    public void setData(ArrayList<Datum> data) {
        this.data = data;
    }

    public class Item {

        @SerializedName("details")
        @Expose
        private Details details;

        public Details getDetails() {
            return details;
        }

        public void setDetails(Details details) {
            this.details = details;
        }


    }


    public class Status {

        @SerializedName("statusCode")
        @Expose
        private Integer statusCode;
        @SerializedName("isSuccess")
        @Expose
        private Boolean isSuccess;
        @SerializedName("message")
        @Expose
        private String message;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public Boolean getIsSuccess() {
            return isSuccess;
        }

        public void setIsSuccess(Boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public class Details {

        @SerializedName("product_id")
        @Expose
        private Integer productId;
        @SerializedName("loan_amount")
        @Expose
        private Integer loanAmount;
        @SerializedName("required_credit_score")
        @Expose
        private Integer requiredCreditScore;
        @SerializedName("loan_period")
        @Expose
        private Integer loanPeriod;
        @SerializedName("loan_period_type")
        @Expose
        private String loanPeriodType;
        @SerializedName("loan_interest_rate")
        @Expose
        private Double loanInterestRate;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("btnStatus")
        @Expose
        private String btnStatus;
        @SerializedName("btnColor")
        @Expose
        private String btnColor;
        @SerializedName("btnHexColor")
        @Expose
        private String btnHexColor;
        @SerializedName("btnText")
        @Expose
        private String btnText;
        @SerializedName("alertMgs")
        @Expose
        private String alertMgs;
        @SerializedName("wizardStatus")
        @Expose
        private String wizardStatus;
        @SerializedName("wizardURL")
        @Expose
        private String wizardURL;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(Integer loanAmount) {
            this.loanAmount = loanAmount;
        }

        public Integer getRequiredCreditScore() {
            return requiredCreditScore;
        }

        public void setRequiredCreditScore(Integer requiredCreditScore) {
            this.requiredCreditScore = requiredCreditScore;
        }

        public Integer getLoanPeriod() {
            return loanPeriod;
        }

        public void setLoanPeriod(Integer loanPeriod) {
            this.loanPeriod = loanPeriod;
        }

        public String getLoanPeriodType() {
            return loanPeriodType;
        }

        public void setLoanPeriodType(String loanPeriodType) {
            this.loanPeriodType = loanPeriodType;
        }

        public Double getLoanInterestRate() {
            return loanInterestRate;
        }

        public void setLoanInterestRate(Double loanInterestRate) {
            this.loanInterestRate = loanInterestRate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getBtnStatus() {
            return btnStatus;
        }

        public void setBtnStatus(String btnStatus) {
            this.btnStatus = btnStatus;
        }

        public String getBtnColor() {
            return btnColor;
        }

        public void setBtnColor(String btnColor) {
            this.btnColor = btnColor;
        }

        public String getBtnHexColor() {
            return btnHexColor;
        }

        public void setBtnHexColor(String btnHexColor) {
            this.btnHexColor = btnHexColor;
        }

        public String getBtnText() {
            return btnText;
        }

        public void setBtnText(String btnText) {
            this.btnText = btnText;
        }

        public String getAlertMgs() {
            return alertMgs;
        }

        public void setAlertMgs(String alertMgs) {
            this.alertMgs = alertMgs;
        }

        public String getWizardStatus() {
            return wizardStatus;
        }

        public void setWizardStatus(String wizardStatus) {
            this.wizardStatus = wizardStatus;
        }

        public String getWizardURL() {
            return wizardURL;
        }

        public void setWizardURL(String wizardURL) {
            this.wizardURL = wizardURL;
        }

    }

    public class Datum {

        @SerializedName("appliedProduct")
        @Expose
        private AppliedProduct appliedProduct;
        @SerializedName("item")
        @Expose
        private ArrayList<Item> item = null;

        public AppliedProduct getAppliedProduct() {
            return appliedProduct;
        }

        public void setAppliedProduct(AppliedProduct appliedProduct) {
            this.appliedProduct = appliedProduct;
        }

        public ArrayList<Item> getItem() {
            return item;
        }

        public void setItem(ArrayList<Item> item) {
            this.item = item;
        }

    }

    public class AppliedProduct {

        @SerializedName("identity_number")
        @Expose
        private String identityNumber;
        @SerializedName("loan_purpose_message")
        @Expose
        private String loanPurposeMessage;
        @SerializedName("applied_date")
        @Expose
        private String appliedDate;
        @SerializedName("loan_status")
        @Expose
        private String loanStatus;
        @SerializedName("product_id")
        @Expose
        private Integer productId;

        public String getIdentityNumber() {
            return identityNumber;
        }

        public void setIdentityNumber(String identityNumber) {
            this.identityNumber = identityNumber;
        }

        public String getLoanPurposeMessage() {
            return loanPurposeMessage;
        }

        public void setLoanPurposeMessage(String loanPurposeMessage) {
            this.loanPurposeMessage = loanPurposeMessage;
        }

        public String getAppliedDate() {
            return appliedDate;
        }

        public void setAppliedDate(String appliedDate) {
            this.appliedDate = appliedDate;
        }

        public String getLoanStatus() {
            return loanStatus;
        }

        public void setLoanStatus(String loanStatus) {
            this.loanStatus = loanStatus;
        }

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

    }
}
