package ${packageName};

import java.io.Serializable;
import java.util.Date;
import java.util.List;
public class ${shortBeanName}ParamVo{

    private String siteId;

    private Integer pageNo;

    private Integer pageSize;

    private Date startDate;

    private Date endDate;

    public ${shortBeanName}ParamVo(String siteId, Integer pageNo, Integer pageSize, Date startDate, Date endDate) {
        this.siteId = siteId;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}