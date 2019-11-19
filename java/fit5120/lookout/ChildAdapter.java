package fit5120.lookout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

/**
 *
 */
public class ChildAdapter extends ExpandableRecyclerViewAdapter<ParentHolder,ChildHolder> {

    public ChildAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public ParentHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_layout,parent,false);
        return new ParentHolder(view);
    }

    @Override
    public ChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_layout,parent,false);
        return new ChildHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ChildHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final ChildText childText = (ChildText) group.getItems().get(childIndex);
        holder.bind(childText);
    }

    @Override
    public void onBindGroupViewHolder(ParentHolder holder, int flatPosition, ExpandableGroup group) {
        final ParentText parentText = (ParentText) group;
        holder.bind(parentText);
    }

    @Override
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        super.setOnGroupClickListener(listener);
    }
}
