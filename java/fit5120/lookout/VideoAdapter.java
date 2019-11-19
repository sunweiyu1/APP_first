package fit5120.lookout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<YouTubeVid> videoList;
    private int[] vidTextArr;
    public VideoAdapter(){}

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_layout,parent,false);
        return new VideoAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.VideoViewHolder holder, int position) {
        holder.videoWeb.loadData(videoList.get(position).getUrl(),"text/html","utf-8");
        int textID = vidTextArr[position];
        holder.vidText.setText(textID);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public VideoAdapter(List<YouTubeVid> videoList, int[] vidTextArr){

        this.videoList=videoList;
        this.vidTextArr=vidTextArr;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        WebView videoWeb;
        TextView vidText;
        public VideoViewHolder(View itemView) {
            super(itemView);

            videoWeb = (WebView) itemView.findViewById(R.id.vidWebView);
            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient() {});
            vidText = (TextView)itemView.findViewById(R.id.vid_text);
        }
    }

}
