package fit5120.lookout;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import static fit5120.lookout.R.id.hr_weather;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private int[] imageArr;
    private String[] textArr;
    private double[] heatIndex;
    private String[] rainper;
    private double[] wcFactor;
    private char recType;
    private static ClickListener clickListener;
    private  int[] backArr;

    public RecyclerViewAdapter(int [] imageArr,String [] textArr,char recType,double[] wcFactor, double[] heatIndex, String[] rainPer, int[] backArr){

        this.imageArr = imageArr;
        this.textArr = textArr;
        this.recType = recType;
        this.wcFactor = wcFactor;
        this.heatIndex = heatIndex;
        this.rainper = rainPer;
        this.backArr = backArr;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (recType == 'H')
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,parent,false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_weather,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewId = imageArr[position];
        holder.recItem.setImageResource(viewId);
        holder.txtItem.setText(textArr[position]);

        if (recType == 'W') {
            holder.hrWeather.setImageResource(backArr[position]);
            holder.hrtext.setText(getTitleText(backArr[position]));
            holder.hrtext.setShadowLayer(50f,holder.hrtext.getShadowDx(),holder.hrtext.getShadowDy(),getShadowColor(backArr[position]));
            holder.hiwc.setText(heatIndex[position] + "°\t|\t " + wcFactor[position] + "°");
            holder.rainper.setText(rainper[position] +"%");
        }
    }

    @Override
    public int getItemCount() {
        return imageArr.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView recItem;
        TextView txtItem;
        TextView hiwc;
        TextView rainper;
        TextView hrtext;
        ImageView hrWeather;
        public ViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                itemView.setOnClickListener(this);
                recItem = (ImageView) itemView.findViewById(R.id.rec_item);
                txtItem = (TextView) itemView.findViewById(R.id.text_item);
                if (recType == 'W') {
                    hiwc = (TextView) itemView.findViewById(R.id.hiwc);
                    rainper = (TextView) itemView.findViewById(R.id.rainpr);
                    hrWeather = (ImageView) itemView.findViewById(hr_weather);
                    hrtext = (TextView) itemView.findViewById(R.id.hrtext);
                }
            }
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

    }
    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public String getTitleText(int backid){

        if (backid == R.drawable.weather_back_1)
            return ("Go For It !");
        else
        if (backid == R.drawable.weather_back_2)
            return ("Take It Easy !");
        else
        if (backid == R.drawable.weather_back_3)
            return ("Take Extreme Care !");
        else
            return ("Stay Inside !!");
    }

    public int getShadowColor(int backid){

        if (backid == R.drawable.weather_back_1)
            return Color.GREEN;
        else
        if (backid == R.drawable.weather_back_2)
            return Color.YELLOW;
        else
        if (backid == R.drawable.weather_back_3)
            return Color.rgb(255, 150, 0);
        else
            return Color.RED;
    }
}