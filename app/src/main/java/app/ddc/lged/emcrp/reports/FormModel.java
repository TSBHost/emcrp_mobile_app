package app.ddc.lged.emcrp.reports;

public class FormModel {

   // @JsonProperty("totalform")
    private Integer totalform;
    // @JsonProperty("id")
    private Integer id;
    //@JsonProperty("shelter")
    private String shelter;
    //@JsonProperty("mtask")
    private String mtask;
    //@JsonProperty("sdate")
    private String sdate;
    private String org;

    //@JsonProperty("totalform")
    public Integer getTotalform() {
        return totalform;
    }
    //@JsonProperty("id")
    public void setTotalform(Integer totalform) {
        this.totalform = totalform;
    }

    //@JsonProperty("id")
    public Integer getId() {
        return id;
    }

    //@JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    //@JsonProperty("shelter")
    public String getOrg() {
        return org;
    }

    //@JsonProperty("shelter")
    public void setOrg(String org) {
        this.org = org;
    }

    //@JsonProperty("shelter")
    public String getShelter() {
        return shelter;
    }

    //@JsonProperty("shelter")
    public void setShelter(String shelter) {
        this.shelter = shelter;
    }

    //@JsonProperty("mtask")
    public String getMtask() {
        return mtask;
    }
   // @JsonProperty("mtask")
    public void setMtask(String mtask) {
        this.mtask = mtask;
    }

   // @JsonProperty("sdate")
    public String getSdate() {
        return sdate;
    }

    //@JsonProperty("sdate")
    public void setSdate(String sdate) {
        this.sdate = sdate;
    }


}