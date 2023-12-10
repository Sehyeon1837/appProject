package com.example.appproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private int[] layoutResources = {R.layout.guide_page1, R.layout.guide_page2, R.layout.guide_page3};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View page = inflater.inflate(layoutResources[position], container, false);

        // 나가기 버튼 활성화
        ImageButton exitbutton = page.findViewById(R.id.ExitButton);

        exitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof GuideButton) {
                    ((GuideButton) context).finish();
                }
            }
        });

        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return layoutResources.length; // 페이지 수
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
