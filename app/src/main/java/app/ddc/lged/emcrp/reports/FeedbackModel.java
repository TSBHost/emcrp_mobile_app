package app.ddc.lged.emcrp.reports;

public class FeedbackModel {

    private Integer totalform;
    private Integer id;
    private Integer subid;
    private String shelter;
    private String mtask;
    private String sdate;

    private String hqfeed;
    private String hqfeeddate;
    private String status;
    private String userfeed;
    private String userimg;
    private String userfeeddate;



    //@JsonProperty("totalform")
    public Integer getTotalform() {
        return totalform;
    }
    //@JsonProperty("id")
    public void setTotalform(Integer totalform) {
        this.totalform = totalform;
    }


    //@JsonProperty("shelter")
    public String getShelter() {
        return shelter;
    }

   // @JsonProperty("shelter")
    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    //@JsonProperty("mtask")
    public String getMtask() {
        return mtask;
    }

    //@JsonProperty("mtask")
    public void setMtask(String mtask) {
        this.mtask = mtask;
    }

   // @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    //@JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    // @JsonProperty("subid")
    public Integer getSubid() {
        return subid;
    }

    //@JsonProperty("subid")
    public void setSubid(Integer subid) {
        this.subid = subid;
    }
    //@JsonProperty("sdate")
    public String getSdate() {
        return sdate;
    }
    // @JsonProperty("sdate")
    public void setSdate(String sdate) {
        this.sdate = sdate;
    }


    //@JsonProperty("hqf1")
    public String getHqFeed() {
        return hqfeed;
    }

   /// @JsonProperty("hqf1")
    public void setHqFeed(String hqfeed) {
        this.hqfeed = hqfeed;
    }

    //@JsonProperty("hqfd1")
    public String getHqFeedDate() {
        return hqfeeddate;
    }

    /// @JsonProperty("hqfd1")
    public void setHqFeedDate(String hqfeeddate) {
        this.hqfeeddate = hqfeeddate;
    }
    //@JsonProperty("hqf1")

    public String getStatus() {
        return status;
    }

    /// @JsonProperty("hqfs1")
    public void setStatus(String status) {
        this.status = status;
    }


    //@JsonProperty("ufeed1")
    public String getUserfeed() {
        return userfeed;
    }

    /// @JsonProperty("ufeed1")
    public void setUserfeed(String userfeed) {
        this.userfeed = userfeed;
    }

    //@JsonProperty("ufeeddate1")
    public String getUserFeedDate() {
        return userfeeddate;
    }

    /// @JsonProperty("ufeeddate1")
    public void setUserFeedDate(String userfeeddate) {
        this.userfeeddate = userfeeddate;
    }

    //@JsonProperty("ufimg1")
    public String getUserImg() {
        return userimg;
    }

    /// @JsonProperty("ufimg1")
    public void setUserImg(String userimg) {
        this.userimg = userimg;
    }




}