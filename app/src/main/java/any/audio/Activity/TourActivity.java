package any.audio.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import any.audio.Adapters.TourPagerAdapter;
import any.audio.R;


public class TourActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ProgressBar tourProgress;
    private Button btnBack, btnNext;
    private TourPagerAdapter tourPagerAdapter;
    private int viewPagerPosition = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_tour);

        viewPager = (ViewPager) findViewById(R.id.tourPager);
        tourProgress = (ProgressBar) findViewById(R.id.progressBar);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnBack = (Button) findViewById(R.id.back);
        btnBack.setVisibility(View.GONE);
        tourPagerAdapter = new TourPagerAdapter(this);
        viewPager.setAdapter(tourPagerAdapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(0);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                if(viewPager.getCurrentItem()==3){
                    startActivity(new Intent(TourActivity.this,Home.class));
                }else {
                    btnBack.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                if(viewPager.getCurrentItem()==0){
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                    btnBack.setVisibility(View.GONE);
                }else {
                    btnBack.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                }
            }
        });


    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // changing the next button text 'NEXT' / 'GOT IT'

            ProgressBarAnimation anim = new ProgressBarAnimation(tourProgress, viewPagerPosition *100 , 100*(position+1));
            anim.setDuration(500);
            tourProgress.startAnimation(anim);

            if (position == 3) {
                // last page. make button text to GOT IT
                btnNext.setText("Accept Terms Condition");

            }
            if(position>0 && position<3){
                // still pages are left
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText("NEXT");
            }

            if(position==0){
                btnBack.setVisibility(View.GONE);
            }

            viewPagerPosition = position + 1;

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    public class ProgressBarAnimation extends Animation {

        /*
        * Use
        * ProgressBarAnimation anim = new ProgressBarAnimation(progress, from, to);
anim.setDuration(1000);
progress.startAnimation(anim);
        *
        * */

        private ProgressBar progressBar;
        private float from;
        private float to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            progressBar.setProgress((int) value);
        }

    }

}
