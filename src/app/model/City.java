package app.model;

import java.time.LocalDateTime;

public class City {
    private Long cityId;
    private String city;
    private Long countryId;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdateBy;

    public City(Long cityId, String city, Long countryId, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdateBy) {
        this.cityId = cityId;
        this.city = city;
        this.countryId = countryId;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }

    /**
     * Minimal object with required values
     * @param city
     * @param countryId
     * @param createdBy
     */
    public City(String city, long countryId, String createdBy) {
        this.city = city;
        this.countryId = countryId;
        this.createdBy = createdBy;
        this.createDate = LocalDateTime.now();
        this.lastUpdate = this.createDate;
        this.lastUpdateBy = this.createdBy;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCountryId() {
        return countryId;
    }

    public void setCountryId(long countryId) {
        this.countryId = countryId;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "City{" +
            "cityId=" + cityId +
            ", city='" + city + '\'' +
            ", countryId=" + countryId +
            ", createDate=" + createDate +
            ", createdBy='" + createdBy + '\'' +
            ", lastUpdate=" + lastUpdate +
            ", lastUpdateBy='" + lastUpdateBy + '\'' +
            '}';
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
