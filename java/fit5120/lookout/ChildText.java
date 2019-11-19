package fit5120.lookout;

import android.os.Parcel;
import android.os.Parcelable;

public class ChildText implements Parcelable {
    public final String name;
    public final int image;

    public ChildText(String name,int image) {
        this.name = name;
        this.image = image;
    }

    protected ChildText(Parcel in) {
        name = in.readString();
        image = in.readInt();
    }

    public static final Creator<ChildText> CREATOR = new Creator<ChildText>() {
        @Override
        public ChildText createFromParcel(Parcel in) {
            return new ChildText(in);
        }

        @Override
        public ChildText[] newArray(int size) {
            return new ChildText[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(image);
    }
}
