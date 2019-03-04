package com.app.l_pesa.profile.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ResUserInfo {

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

        @SerializedName("user_info")
        @Expose
        private UserInfo userInfo;
        @SerializedName("user_contact_info")
        @Expose
        private UserContactInfo userContactInfo;
        @SerializedName("user_business_info")
        @Expose
        private UserBusinessInfo userBusinessInfo;
        @SerializedName("user_employment_info")
        @Expose
        private UserEmploymentInfo userEmploymentInfo;
        @SerializedName("user_personal_info")
        @Expose
        private UserPersonalInfo userPersonalInfo;
        @SerializedName("user_ids_info")
        @Expose
        private ArrayList<UserIdsInfo> userIdsInfo = null;

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public UserContactInfo getUserContactInfo() {
            return userContactInfo;
        }

        public void setUserContactInfo(UserContactInfo userContactInfo) {
            this.userContactInfo = userContactInfo;
        }

        public UserBusinessInfo getUserBusinessInfo() {
            return userBusinessInfo;
        }

        public void setUserBusinessInfo(UserBusinessInfo userBusinessInfo) {
            this.userBusinessInfo = userBusinessInfo;
        }

        public UserEmploymentInfo getUserEmploymentInfo() {
            return userEmploymentInfo;
        }

        public void setUserEmploymentInfo(UserEmploymentInfo userEmploymentInfo) {
            this.userEmploymentInfo = userEmploymentInfo;
        }

        public UserPersonalInfo getUserPersonalInfo() {
            return userPersonalInfo;
        }

        public void setUserPersonalInfo(UserPersonalInfo userPersonalInfo) {
            this.userPersonalInfo = userPersonalInfo;
        }

        public ArrayList<UserIdsInfo> getUserIdsInfo() {
            return userIdsInfo;
        }

        public void setUserIdsInfo(ArrayList<UserIdsInfo> userIdsInfo) {
            this.userIdsInfo = userIdsInfo;
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

    public class UserBusinessInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("business_name")
        @Expose
        private String businessName;
        @SerializedName("tin_number")
        @Expose
        private String tinNumber;
        @SerializedName("id_type")
        @Expose
        private String idType;
        @SerializedName("id_number")
        @Expose
        private String idNumber;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getTinNumber() {
            return tinNumber;
        }

        public void setTinNumber(String tinNumber) {
            this.tinNumber = tinNumber;
        }

        public String getIdType() {
            return idType;
        }

        public void setIdType(String idType) {
            this.idType = idType;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

    }
    public class UserContactInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("street_address")
        @Expose
        private String streetAddress;
        @SerializedName("postal_address")
        @Expose
        private String postalAddress;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("phone_number")
        @Expose
        private String phoneNumber;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getStreetAddress() {
            return streetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
        }

        public String getPostalAddress() {
            return postalAddress;
        }

        public void setPostalAddress(String postalAddress) {
            this.postalAddress = postalAddress;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

    }

    public class UserEmploymentInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("employer_type")
        @Expose
        private String employerType;
        @SerializedName("employer_name")
        @Expose
        private String employerName;
        @SerializedName("department")
        @Expose
        private String department;
        @SerializedName("position")
        @Expose
        private String position;
        @SerializedName("employees_id_number")
        @Expose
        private String employeesIdNumber;
        @SerializedName("city")
        @Expose
        private String city;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmployerType() {
            return employerType;
        }

        public void setEmployerType(String employerType) {
            this.employerType = employerType;
        }

        public String getEmployerName() {
            return employerName;
        }

        public void setEmployerName(String employerName) {
            this.employerName = employerName;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getEmployeesIdNumber() {
            return employeesIdNumber;
        }

        public void setEmployeesIdNumber(String employeesIdNumber) {
            this.employeesIdNumber = employeesIdNumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }

    public class UserIdsInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("id_number")
        @Expose
        private String idNumber;
        @SerializedName("type_name")
        @Expose
        private String typeName;
        @SerializedName("id_type_unique")
        @Expose
        private String idTypeUnique;
        @SerializedName("id_type_name")
        @Expose
        private String idTypeName;
        @SerializedName("file_name")
        @Expose
        private String fileName;
        @SerializedName("verified")
        @Expose
        private String verified;
        @SerializedName("locked")
        @Expose
        private String locked;
        @SerializedName("created")
        @Expose
        private String created;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getIdTypeUnique() {
            return idTypeUnique;
        }

        public void setIdTypeUnique(String idTypeUnique) {
            this.idTypeUnique = idTypeUnique;
        }

        public String getIdTypeName() {
            return idTypeName;
        }

        public void setIdTypeName(String idTypeName) {
            this.idTypeName = idTypeName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

        public String getLocked() {
            return locked;
        }

        public void setLocked(String locked) {
            this.locked = locked;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

    }

    public class UserInfo {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("phone_number")
        @Expose
        private String phoneNumber;
        @SerializedName("credit_score")
        @Expose
        private Integer creditScore;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;
        @SerializedName("register_step")
        @Expose
        private String registerStep;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Integer getCreditScore() {
            return creditScore;
        }

        public void setCreditScore(Integer creditScore) {
            this.creditScore = creditScore;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getRegisterStep() {
            return registerStep;
        }

        public void setRegisterStep(String registerStep) {
            this.registerStep = registerStep;
        }

    }

    public class UserPersonalInfo {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("middle_name")
        @Expose
        private String middleName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("email_address")
        @Expose
        private String emailAddress;
        @SerializedName("mother_maiden_name")
        @Expose
        private String motherMaidenName;
        @SerializedName("dob")
        @Expose
        private String dob;
        @SerializedName("sex")
        @Expose
        private String sex;
        @SerializedName("merital_status")
        @Expose
        private String meritalStatus;
        @SerializedName("profile_image")
        @Expose
        private String profileImage;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getMotherMaidenName() {
            return motherMaidenName;
        }

        public void setMotherMaidenName(String motherMaidenName) {
            this.motherMaidenName = motherMaidenName;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getMeritalStatus() {
            return meritalStatus;
        }

        public void setMeritalStatus(String meritalStatus) {
            this.meritalStatus = meritalStatus;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

    }

}
