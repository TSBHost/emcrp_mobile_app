package app.ddc.lged.emcrp.reports;

public class MajortaskModel {

    // @JsonProperty("totalform")
    private Integer id;
    private Integer tid;
    private String name;
    private String details;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTid() {
        return tid;
    }
    public void setTid(Integer tid) {
        this.tid = tid;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getDetails() {
        return details;
    }
    public void setDetails(String alias) {
        this.details = details;
    }


}