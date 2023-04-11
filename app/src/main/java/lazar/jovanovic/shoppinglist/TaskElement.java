package lazar.jovanovic.shoppinglist;

public class TaskElement {
    private String mNaslov;
    private Boolean mChecked;

    public TaskElement(String mNaslov, Boolean mChecked) {
        this.mNaslov = mNaslov;
        this.mChecked = mChecked;
    }

    public String getmNaslov() {
        return mNaslov;
    }

    public void setmNaslov(String mNaslov) {
        this.mNaslov = mNaslov;
    }

    public Boolean getmChecked() {
        return mChecked;
    }

    public void setmChecked(Boolean mChecked) {
        this.mChecked = mChecked;
    }
}
