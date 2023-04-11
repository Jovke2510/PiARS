package lazar.jovanovic.shoppinglist;

public class ListElement {
    private String mNaslov;
    private Boolean mShared;

    public ListElement(String mNaslov, Boolean mShared) {
        this.mNaslov = mNaslov;
        this.mShared = mShared;
    }

    public String getmNaslov() {
        return mNaslov;
    }

    public void setmNaslov(String mNaslov) {
        this.mNaslov = mNaslov;
    }

    public Boolean getmShared() {
        return mShared;
    }

    public void setmShared(Boolean mShared) {
        this.mShared = mShared;
    }
}
