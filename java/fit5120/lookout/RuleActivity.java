package fit5120.lookout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;

public class RuleActivity extends MainActivity {

    private RecyclerView expandRecView;
    ArrayList<ParentText> topics = new ArrayList<>();

    ArrayList<ChildText> infraRule = new ArrayList<>();
    ArrayList<ChildText> busLane = new ArrayList<>();
    ArrayList<ChildText> bbox = new ArrayList<>();
    ArrayList<ChildText> carDoor = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        setNavigationViewListener();

        expandRecView = (RecyclerView) findViewById(R.id.ruleRec);

        expandRecView.setLayoutManager(new LinearLayoutManager(this));
        infraRule.add(new ChildText("You can ride on Footpath if you have been given and are following the conditions on a medical certificate that says you have a disability that makes it difficult for you to ride on the road.",0));
        infraRule.add(new ChildText("When riding on footpaths and shared paths, cyclists need to:" +
                "\n\u2022 Keep to the left on footpaths and shared paths (unless impractical to do so)." +
                "\n\u2022 give way to pedestrians.",R.drawable.ped ));

        infraRule.add(new ChildText("You don't have to use an off-road bicycle path, separated footpaths or shared paths (if there is one) when riding a bike. You can choose to ride on the road instead if you wish.",R.drawable.shared_road));
        infraRule.add(new ChildText("If there’s a bicycle lane on the road heading in the same direction as you, you must use this when riding a bike (unless it’s not practical to do so).",R.drawable.blane));
        ParentText topic1 = new ParentText("Infrastructure Related Rules",infraRule);
        topics.add(topic1);

        busLane.add(new ChildText("Keep to the left of the bus lane.",0));
        busLane.add(new ChildText("Give way to buses at all times.",0));
        busLane.add(new ChildText("Wait behind the bus if it is coming to a stop and do not overtake or undertake it.",R.drawable.stayback));
        busLane.add(new ChildText("Be alert at bus stops and watch out for passengers getting on and off buses, stop behind the bus until it has moved off.",0));
        busLane.add(new ChildText("Before changing lanes and turning, always scan behind and signal your intentions to other road users.",0));
        busLane.add(new ChildText("Take extra care when cycling at night. Wear bright or light coloured clothing and reflective strips, use front and rear bike lights.",0));
        ParentText topic2 = new ParentText("Riders in Bus Lane",busLane);
        topics.add(topic2);

        bbox.add(new ChildText("When facing a red light, drivers must stop before the first stop line and not move into the bicycle box until the lights turn green.",0));
        bbox.add(new ChildText("Bike riders can make a hook turn to turn right at any intersection (unless there are signs restricting this).",R.drawable.hook_turn));
        bbox.add(new ChildText("In some locations, particularly in the Melbourne CBD, hook turns are obligatory for all vehicles, including bike riders.  These intersections carry special 'Right turn from left only' signs.",R.drawable.rightfromleft));
        ParentText topic3 = new ParentText("Bicycle box rules for drivers",bbox);
        topics.add(topic3);

        ChildAdapter childAdapter = new ChildAdapter(topics);
        expandRecView.setAdapter(childAdapter);
    }
}
