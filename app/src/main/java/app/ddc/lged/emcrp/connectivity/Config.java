package app.ddc.lged.emcrp.connectivity;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by User on 5/15/2017.
 */

public class Config extends AppCompatActivity {
    public String URL,PageUrl;

    //////// MDSP Database : ref: mdspbd.com///////////
    public Config() {
        this.URL="https://emcrpbd.org/android/";
    }

    public String getURL() {
        return URL;
    }


    ////// Shelter Data Sync ///////////
    public String getPackage() {
        PageUrl = URL+"get_package.php";
        return PageUrl;
    }


    ////// Shelter Data Sync ///////////
    public String getSubPackage() {
        PageUrl = URL+"get_sub_package.php";
        return PageUrl;
    }

    ////// Shelter Data Sync ///////////
    public String getTaskUrl() {
        PageUrl = URL+"get_majortasks.php";
        //PageUrl = URL+"get_tasks_test.php";
        return PageUrl;
    }
    ////// Shelter Data Sync ///////////
    public String getSubTask() {
            PageUrl = URL+"get_subtasks.php";
            //PageUrl = URL+"get_tasks_test.php";
        return PageUrl;
    }

    ////// Submission Data Sync ///////////
    public String getSubmissionSyncUrl() {
        PageUrl = URL+"get_submitted_form_list_sync.php";
        return PageUrl;
    }

    ////// Feedback Data Sync ///////////
    public String getFeedbackSyncUrl() {
        //PageUrl = URL+"get_feedback.php";
        PageUrl = "https://emcrpbd.org/storage/app/json/";
        return PageUrl;
    }

    ////// Feedback Data Sync ///////////
    public String getAllSync() {
        //PageUrl = URL+"get_feedback.php";
        PageUrl = URL+"get_sync_datea.php";
        return PageUrl;
    }
}
