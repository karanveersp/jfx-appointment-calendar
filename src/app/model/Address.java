package app.model;

import java.time.LocalDateTime;

public class Address {
    private Long addressId;
    private String address;
    private String address2;
    private Long cityId;
    private String postalCode;
    private String phone;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public Address() { }

    public Address(String address, String address2, long cityId, String postalCode, String phone, String createdBy) {
        this.address = address;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.cityId = cityId;
        this.phone = phone;
        this.createdBy = createdBy;
        this.createDate = LocalDateTime.now();
        this.lastUpdate = this.createDate;
        this.lastUpdateBy = this.createdBy;
    }

    public Address(String address, String address2, Long cityId, String postalCode, String phone, String createdBy) {
        this.address = address;
        this.address2 = address2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = LocalDateTime.now();
        this.createdBy = createdBy;
        this.lastUpdate = LocalDateTime.now();
        this.lastUpdateBy = createdBy;
    }

    /**
     * Constructor for creating an Address instance
     * when all fields are known
     * @param addressId
     * @param address
     * @param address2
     * @param cityId
     * @param postalCode
     * @param phone
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdateBy
     */
    public Address(Long addressId, String address, String address2, Long cityId, String postalCode, String phone, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.addressId = addressId;
        this.address = address;
        this.address2 = address2;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    @Override
    public String toString() {
        return "Address{" +
            "addressId=" + addressId +
            ", address='" + address + '\'' +
            ", address2='" + address2 + '\'' +
            ", cityId=" + cityId +
            ", postalCode='" + postalCode + '\'' +
            ", phone='" + phone + '\'' +
            ", createDate=" + createDate +
            ", createdBy='" + createdBy + '\'' +
            ", lastUpdate=" + lastUpdate +
            ", lastUpdateBy='" + lastUpdateBy + '\'' +
            '}';
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

}
