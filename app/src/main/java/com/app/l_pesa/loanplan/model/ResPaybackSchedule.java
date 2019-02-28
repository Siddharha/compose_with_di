package com.app.l_pesa.loanplan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ResPaybackSchedule {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private Data data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("loan_info")
        @Expose
        private LoanInfo loanInfo;
        @SerializedName("payback_schedule")
        @Expose
        private ArrayList<PaybackSchedule> paybackSchedule = null;

        public LoanInfo getLoanInfo() {
            return loanInfo;
        }

        public void setLoanInfo(LoanInfo loanInfo) {
            this.loanInfo = loanInfo;
        }

        public ArrayList<PaybackSchedule> getPaybackSchedule() {
            return paybackSchedule;
        }

        public void setPaybackSchedule(ArrayList<PaybackSchedule> paybackSchedule) {
            this.paybackSchedule = paybackSchedule;
        }

    }

    public class LoanInfo {

        @SerializedName("loan_id")
        @Expose
        private Integer loanId;
        @SerializedName("ref_no")
        @Expose
        private String refNo;
        @SerializedName("merchant_code")
        @Expose
        private Integer merchantCode;
        @SerializedName("total_payback")
        @Expose
        private String totalPayback;
        @SerializedName("payfullamount_button_status")
        @Expose
        private Boolean payfullamountButtonStatus;
        @SerializedName("payfullamount_button_text")
        @Expose
        private String payfullamountButtonText;
        @SerializedName("payfullamount_message")
        @Expose
        private String payfullamountMessage;

        public Integer getLoanId() {
            return loanId;
        }

        public void setLoanId(Integer loanId) {
            this.loanId = loanId;
        }

        public String getRefNo() {
            return refNo;
        }

        public void setRefNo(String refNo) {
            this.refNo = refNo;
        }

        public Integer getMerchantCode() {
            return merchantCode;
        }

        public void setMerchantCode(Integer merchantCode) {
            this.merchantCode = merchantCode;
        }

        public String getTotalPayback() {
            return totalPayback;
        }

        public void setTotalPayback(String totalPayback) {
            this.totalPayback = totalPayback;
        }

        public Boolean getPayfullamountButtonStatus() {
            return payfullamountButtonStatus;
        }

        public void setPayfullamountButtonStatus(Boolean payfullamountButtonStatus) {
            this.payfullamountButtonStatus = payfullamountButtonStatus;
        }

        public String getPayfullamountButtonText() {
            return payfullamountButtonText;
        }

        public void setPayfullamountButtonText(String payfullamountButtonText) {
            this.payfullamountButtonText = payfullamountButtonText;
        }

        public String getPayfullamountMessage() {
            return payfullamountMessage;
        }

        public void setPayfullamountMessage(String payfullamountMessage) {
            this.payfullamountMessage = payfullamountMessage;
        }

    }
    public class PaybackSchedule {

        @SerializedName("paid_date")
        @Expose
        private String paidDate;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("paid_amount")
        @Expose
        private String paidAmount;
        @SerializedName("can_pay")
        @Expose
        private Boolean canPay;
        @SerializedName("paid_status")
        @Expose
        private String paidStatus;
        @SerializedName("s_date")
        @Expose
        private String sDate;
        @SerializedName("e_date")
        @Expose
        private String eDate;
        @SerializedName("current_balance")
        @Expose
        private Integer currentBalance;
        @SerializedName("payanytime_button_status")
        @Expose
        private Boolean payanytimeButtonStatus;

        public String getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(String paidDate) {
            this.paidDate = paidDate;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(String paidAmount) {
            this.paidAmount = paidAmount;
        }

        public Boolean getCanPay() {
            return canPay;
        }

        public void setCanPay(Boolean canPay) {
            this.canPay = canPay;
        }

        public String getPaidStatus() {
            return paidStatus;
        }

        public void setPaidStatus(String paidStatus) {
            this.paidStatus = paidStatus;
        }

        public String getSDate() {
            return sDate;
        }

        public void setSDate(String sDate) {
            this.sDate = sDate;
        }

        public String getEDate() {
            return eDate;
        }

        public void setEDate(String eDate) {
            this.eDate = eDate;
        }

        public Integer getCurrentBalance() {
            return currentBalance;
        }

        public void setCurrentBalance(Integer currentBalance) {
            this.currentBalance = currentBalance;
        }

        public Boolean getPayanytimeButtonStatus() {
            return payanytimeButtonStatus;
        }

        public void setPayanytimeButtonStatus(Boolean payanytimeButtonStatus) {
            this.payanytimeButtonStatus = payanytimeButtonStatus;
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
}
