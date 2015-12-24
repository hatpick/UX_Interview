package demo.datapp.messageapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageModel implements Parcelable {
    private int id;
    private String content;
    private String updatedDate;
    private String authorName;
    private String authorAvatar;

    public MessageModel(int id, String content, String updatedDate, String authorName, String authorAvatar) {
        this.id = id;
        this.content = content;
        this.updatedDate = updatedDate;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(content);
        dest.writeString(updatedDate.toString());
        dest.writeString(authorName);
        dest.writeString(authorAvatar);
    }

    protected MessageModel(Parcel in) {
        this.id = in.readInt();
        this.content = in.readString();
        this.updatedDate = in.readString();
        this.authorName = in.readString();
        this.authorAvatar = in.readString();
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };
}
