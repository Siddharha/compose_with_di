package com.app.l_pesa.loanHistory.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ResLoanHistory {

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

        @SerializedName("loan_history")
        @Expose
        private ArrayList<LoanHistory> loanHistory = null;
        @SerializedName("user_credit_score")
        @Expose
        private Integer userCreditScore;

        public ArrayList<LoanHistory> getLoanHistory() {
            return loanHistory;
        }

        public void setLoanHistory(ArrayList<LoanHistory> loanHistory) {
            this.loanHistory = loanHistory;
        }

        public Integer getUserCreditScore() {
            return userCreditScore;
        }

        public void setUserCreditScore(Integer userCreditScore) {
            this.userCreditScore = userCreditScore;
        }

    }

    public class LoanHistory {

        @SerializedName("loan_id")
        @Expose
        private Integer loanId;
        @SerializedName("identity_number")
        @Expose
        private String identityNumber;
        @SerializedName("loan_amount")
        @Expose
        private Integer loanAmount;
        @SerializedName("interest_rate")
        @Expose
        private String interestRate;
        @SerializedName("convertion_dollar_value")
        @Expose
        private String convertionDollarValue;
        @SerializedName("convertion_loan_amount")
        @Expose
        private String convertionLoanAmount;
        @SerializedName("actual_loan_amount")
        @Expose
        private String actualLoanAmount;
        @SerializedName("applied_date")
        @Expose
        private String appliedDate;
        @SerializedName("sanctioned_date")
        @Expose
        private String sanctionedDate;
        @SerializedName("finished_date")
        @Expose
        private String finishedDate;
        @SerializedName("disapprove_date")
        @Expose
        private String disapproveDate;
        @SerializedName("loan_status")
        @Expose
        private String loanStatus;
        @SerializedName("currency_code")
        @Expose
        private String currencyCode;
        @SerializedName("loan_status_txt")
        @Expose
        private ArrayList<String> loanStatusTxt = null;
        @SerializedName("due_date")
        @Expose
        private String dueDate;

        public Integer getLoanId() {
            return loanId;
        }

        public void setLoanId(Integer loanId) {
            this.loanId = loanId;
        }

        public String getIdentityNumber() {
            return identityNumber;
        }

        public void setIdentityNumber(String identityNumber) {
            this.identityNumber = identityNumber;
        }

        public Integer getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(Integer loanAmount) {
            this.loanAmount = loanAmount;
        }

        public String getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(String interestRate) {
            this.interestRate = interestRate;
        }

        public String getConvertionDollarValue() {
            return convertionDollarValue;
        }

        public void setConvertionDollarValue(String convertionDollarValue) {
            this.convertionDollarValue = convertionDollarValue;
        }

        public String getConvertionLoanAmount() {
            return convertionLoanAmount;
        }

        public void setConvertionLoanAmount(String convertionLoanAmount) {
            this.convertionLoanAmount = convertionLoanAmount;
        }

        public String getActualLoanAmount() {
            return actualLoanAmount;
        }

        public void setActualLoanAmount(String actualLoanAmount) {
            this.actualLoanAmount = actualLoanAmount;
        }

        public String getAppliedDate() {
            return appliedDate;
        }

        public void setAppliedDate(String appliedDate) {
            this.appliedDate = appliedDate;
        }

        public String getSanctionedDate() {
            return sanctionedDate;
        }

        public void setSanctionedDate(String sanctionedDate) {
            this.sanctionedDate = sanctionedDate;
        }

        public String getFinishedDate() {
            return finishedDate;
        }

        public void setFinishedDate(String finishedDate) {
            this.finishedDate = finishedDate;
        }

        public String getDisapproveDate() {
            return disapproveDate;
        }

        public void setDisapproveDate(String disapproveDate) {
            this.disapproveDate = disapproveDate;
        }

        public String getLoanStatus() {
            return loanStatus;
        }

        public void setLoanStatus(String loanStatus) {
            this.loanStatus = loanStatus;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public ArrayList<String> getLoanStatusTxt() {
            return loanStatusTxt;
        }

        public void setLoanStatusTxt(ArrayList<String> loanStatusTxt) {
            this.loanStatusTxt = loanStatusTxt;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
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
