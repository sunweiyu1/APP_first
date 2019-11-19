package fit5120.lookout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;

public class GuideMain extends MainActivity{

    private RecyclerView expandRecView;
    ArrayList<ParentText> topics = new ArrayList<>();

    ArrayList<ChildText> helmetDesc = new ArrayList<>();
    ArrayList<ChildText> riderSafety = new ArrayList<>();
    ArrayList<ChildText> beVisible = new ArrayList<>();
    ArrayList<ChildText> carDoor = new ArrayList<>();

    private static int[] recTextSources = new int[]{
            R.string.guide1,
            R.string.guide2,
            R.string.guide3,
            R.string.guide4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        setNavigationViewListener();

        expandRecView = (RecyclerView) findViewById(R.id.guideRec);

        expandRecView.setLayoutManager(new LinearLayoutManager(this));
        helmetDesc.add(new ChildText("All bike riders are required to wear a bike helmet in Victoria.",0));
        helmetDesc.add(new ChildText("Make sure it fits firmly and comfortably on your head and cannot be tilted in any direction and the straps can be adjusted so there is no slack when fastened.",R.drawable.helmet_fit));
        helmetDesc.add(new ChildText("Place your hands on top of the helmet and try to move it. It should not be possible to tilt the helmet:" +
                "\n\u2022 forwards to cover the eyes"+
                "\n\u2022 backwards to uncover the forehead" +
                "\n\u2022 sideways to uncover the side of the head.",0));
        helmetDesc.add(new ChildText("Your helmet must be safety approved and meets the Australian/New Zealand Standard AS/NZS 2063.",R.drawable.standard_code));
        ParentText topic1 = new ParentText("Wearing Bicycle Helmet",helmetDesc);
        topics.add(topic1);

        riderSafety.add(new ChildText("Before changing lanes and turning, always scan behind and signal your intentions to other road users.",R.drawable.signals));
        riderSafety.add(new ChildText("Try to make eye contact with other road users to help them know that you are there.",0));
        riderSafety.add(new ChildText("Look out for other road users particularly when they are approaching you from behind or pulling out in front of you.",0));
        riderSafety.add(new ChildText("Look out for drivers and passengers getting in and out of parked cars and be aware of the risk of car doors opening.",R.drawable.parked_car_safety));
        riderSafety.add(new ChildText("Be careful riding over tram tracks, especially in wet weather.",0));
        ParentText topic2 = new ParentText("Rider Safety",riderSafety);
        topics.add(topic2);

        beVisible.add(new ChildText("When riding at night or in conditions of low light, your bike must have a white front light, a rear red light, both visible from at least 200 metres, and a red rear reflector visible from at least 50 metres.",R.drawable.light));
        beVisible.add(new ChildText("When using a single lane roundabout, ride in the middle of the lane. This is so you are more visible to other road users and you are less likely to be cut off when other road users are exiting the roundabout.",R.drawable.single_lane_roundabout));
        beVisible.add(new ChildText("Wear bright top day and night.",0));

        ParentText topic3 = new ParentText("Be Visible",beVisible);
        topics.add(topic3);

        carDoor.add(new ChildText("Look out for drivers and passengers getting in and out of parked cars.",0));
        carDoor.add(new ChildText("Take care when riding beside parked cars.  Position yourself so that there is sufficient space between you and the parked cars.",0));
        carDoor.add(new ChildText("In places where there are a lot of parked cars, slow down.",0));

        ParentText topic4 = new ParentText("Stay Alert",carDoor);
        topics.add(topic4);

        ChildAdapter childAdapter = new ChildAdapter(topics);
        expandRecView.setAdapter(childAdapter);
    }
}

