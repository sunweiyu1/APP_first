package fit5120.lookout;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class ParentHolder extends GroupViewHolder {
    private RecyclerView expandRecView;
    private TextView mTextView;
    private ImageView arrow;
    private int lastExpandedPosition = -1;
    private OnGroupClickListener listener;
    private RecyclerView recyclerView;
    public ParentHolder(View itemView) {
        super(itemView);
        mTextView=itemView.findViewById(R.id.textData);
        arrow = itemView.findViewById(R.id.arrow);
    }
    public void bind(ParentText parentText){
        mTextView.setText(parentText.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 0, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}

