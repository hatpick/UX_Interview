package demo.datapp.photogallery.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hat on 12/18/15.
 */
public class PhotoModel implements Parcelable {
    private String name;
    private String url;
    private final String ASSETS_FOLDER = "file:///android_asset/photos/";

    public PhotoModel(String name) {
        this.name = name;
        this.url = ASSETS_FOLDER + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrettyName() {
        String sName = name.toLowerCase().substring(0, name.lastIndexOf('.'));
        return Character.toString(sName.charAt(0)).toUpperCase() + sName.substring(1);
    }

    public String getUrl() {
        return url;
    }

    protected PhotoModel(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }

    public static final Creator<PhotoModel> CREATOR = new Creator<PhotoModel>() {
        @Override
        public PhotoModel createFromParcel(Parcel in) {
            return new PhotoModel(in);
        }

        @Override
        public PhotoModel[] newArray(int size) {
            return new PhotoModel[size];
        }
    };
}
