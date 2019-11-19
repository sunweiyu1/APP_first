package fit5120.lookout;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.Vector;

public class VideoPage extends MainActivity {
    protected DrawerLayout myDrawer;
    RecyclerView recyclerView;
    Vector<YouTubeVid> youtubeVideos = new Vector<YouTubeVid>();
    private int[] vidTextArr = {
            R.string.vidtext1,R.string.vidtext2,R.string.vidtext3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_page);
        setNavigationViewListener();

        recyclerView = (RecyclerView) findViewById(R.id.videoRec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        youtubeVideos.add( new YouTubeVid("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/d2z7TuV0DzI?rel=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"));
        youtubeVideos.add( new YouTubeVid("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/2--6WAldqqg?rel=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>") );
        youtubeVideos.add( new YouTubeVid("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/r_l4kdbAGc4?rel=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>") );

        VideoAdapter videoAdapter = new VideoAdapter(youtubeVideos,vidTextArr);
        recyclerView.setAdapter(videoAdapter);
    }
}
