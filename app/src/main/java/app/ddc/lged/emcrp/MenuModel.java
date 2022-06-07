package app.ddc.lged.emcrp;

public class MenuModel {

   // @JsonProperty("totalform")
    private Integer totalform;
    // @JsonProperty("id")
    private Integer id;
    //@JsonProperty("shelter")
    private String title;

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
    public String getTitle() {
        return title;
    }

    //@JsonProperty("shelter")
    public void setTitle(String title) {
        this.title = title;
    }


}