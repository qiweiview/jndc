package jndc.web_support.model.dto;

public class ClearRecordOptionDTO {

    private Integer recordType;//0 release 1 block

    private Integer clearType;//1 save top 10    / 2 clear by date limit

    private Long clearDateLimit;


    public boolean clearByDateLimit() {
        return clearType == 2;
    }

    @Override
    public String toString() {
        return "ClearRecordOptionDTO{" +
                "clearType=" + clearType +
                ", clearDateLimit='" + clearDateLimit + '\'' +
                '}';
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public Integer getClearType() {
        return clearType;
    }

    public void setClearType(Integer clearType) {
        this.clearType = clearType;
    }

    public Long getClearDateLimit() {
        return clearDateLimit;
    }

    public void setClearDateLimit(Long clearDateLimit) {
        this.clearDateLimit = clearDateLimit;
    }
}
