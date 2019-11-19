package fit5120.lookout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;

public class sliderAdapter extends PagerAdapter {
    private int[] imageResources = {
            R.drawable.slider2,
            R.drawable.slider5,
            R.drawable.slider3,
            R.drawable.slider4
    };

    private Context sliderContext;
    private LayoutInflater layoutInflater;
    private int custPos;
    public sliderAdapter(Context sliderContext){
        this.sliderContext = sliderContext;
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int actCount(){
        return imageResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == (LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (custPos > (imageResources.length - 1)){
            custPos = 0;
        }
        layoutInflater = (LayoutInflater)sliderContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideView = layoutInflater.inflate(R.layout.slider_layout,container,false);
        ImageView imageView =  (ImageView)slideView.findViewById(R.id.slider_image);
        imageView.setImageResource(imageResources[custPos]);
        container.addView(slideView);
        custPos++;
        return slideView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
