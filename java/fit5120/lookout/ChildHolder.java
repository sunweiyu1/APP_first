package fit5120.lookout;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class ChildHolder extends ChildViewHolder {
    private TextView mTextView;
    private ImageView mImageView;

    public ChildHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.textData);
        mImageView = itemView.findViewById(R.id.imageData);
    }

    public void bind(ChildText childText){
        LinearLayout.LayoutParams lp;
        mTextView.setText(childText.name);
        if (childText.image != 0) {
            Log.d("Image Set","" +mImageView.getLayoutParams().height );
            mImageView.setImageResource(childText.image);
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,700);
            lp.setMargins(0,70,0,0);
        }
        else
            lp = new LinearLayout.LayoutParams(0,0);

        mImageView.setLayoutParams(lp);
    }
}
