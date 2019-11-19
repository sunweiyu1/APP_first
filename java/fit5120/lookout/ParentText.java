package fit5120.lookout;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class ParentText extends ExpandableGroup<ChildText> {

    public ParentText(String title, List<ChildText> items) {
        super(title + "\n", items);
    }
}
