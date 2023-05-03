package lazar.jovanovic.shoppinglist;

public class TaskElement {
    private String mNaslov;
    private Boolean mChecked;
    private String mItemListName;

    public TaskElement(String mNaslov, Boolean mChecked, String mItemListName) {
        this.mNaslov = mNaslov;
        this.mChecked = mChecked;
        this.mItemListName = mItemListName;
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

    public String getmItemListName() {
        return mItemListName;
    }

    public void setmItemListName(String mItemListName) {
        this.mItemListName = mItemListName;
    }
}
