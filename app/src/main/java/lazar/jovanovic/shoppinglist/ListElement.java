package lazar.jovanovic.shoppinglist;

public class ListElement {
    private final String mUsername;
    private String mNaslov;
    private Boolean mShared;

    public ListElement(String mNaslov, Boolean mShared, String mUsername) {
        this.mNaslov = mNaslov;
        this.mShared = mShared;
        this.mUsername = mUsername;
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

    public String getmUsername() {
        return mUsername;
    }
}
