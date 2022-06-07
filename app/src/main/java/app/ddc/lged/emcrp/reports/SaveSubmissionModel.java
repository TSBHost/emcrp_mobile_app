package app.ddc.lged.emcrp.reports;

public class SaveSubmissionModel {

    // @JsonProperty("totalform")
    private Integer id;
    private String ShelterCode;
    private String UserName;
    private String MajorTasks;
    private String Tasks;
    private String sdate;
    private boolean isSelected;

    //@JsonProperty("id")
    public Integer getId() {
        return id;
    }
    //@JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    //@JsonProperty("shelter")
    public String getShelter() {
        return ShelterCode;
    }
   // @JsonProperty("shelter")
    public void setShelter(String ShelterCode) {
        this.ShelterCode = ShelterCode;
    }

    //@JsonProperty("uname")
    public String getUname() {
        return UserName;
    }

    //@JsonProperty("uname")
    public void setUname(String UserName) {
        this.UserName = UserName;
    }

    //@JsonProperty("mtask")
    public String getMtask() {
        return MajorTasks;
    }

    //@JsonProperty("mtask")
    public void setMtask(String MajorTasks) {
        this.MajorTasks = MajorTasks;
    }

    //@JsonProperty("sdate")
    public String getTasks() {
        return Tasks;
    }

    // @JsonProperty("ufeed1")
    public void setTasks(String Tasks) {
        this.Tasks = Tasks;
    }

    //@JsonProperty("sdate")
    public String getDate() {
        return sdate;
    }

    // @JsonProperty("ufeed1")
    public void setDate(String sdate) {
        this.sdate = sdate;
    }


    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}