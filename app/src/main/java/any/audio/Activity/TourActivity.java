package any.audio.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import any.audio.Adapters.TourPagerAdapter;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.Views.ScrollViewExt;


public class TourActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ProgressBar tourProgress;
    private Button btnBack, btnNext;
    private TourPagerAdapter tourPagerAdapter;
    private int viewPagerPosition = 1 ;

    private ScrollViewExt scrollView;
    private TextView acceptBtn;
    private TextView termsHeader;
    private TextView termsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // try navigating to next
        if(SharedPrefrenceUtils.getInstance(this).getTermsAccepted()) {
            navigateToNext();
        }
        fullScreencall();
        setContentView(R.layout.activity_tour);
        initView();
        attachListeners();

    }

    private void navigateToNext(){


            if(ConnectivityUtils.getInstance(this).isConnectedToNet()){
                startActivity(new Intent(this,AnyAudioActivity.class));
            }else{
                startActivity(new Intent(this,ErrorSplash.class));
            }
            finish();

    }

    public void fullScreencall() {
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initView(){

        View view = LayoutInflater.from(this).inflate(R.layout.tour_fourth,null,false);

        scrollView = (ScrollViewExt) view.findViewById(R.id.termsScrollView);
        termsHeader = (TextView) view.findViewById(R.id.termsHeader);
        termsText = (TextView) view.findViewById(R.id.termsContent);

      //  termsText.setText(Html.fromHtml(getResources().getString(R.string.terms)));
        acceptBtn = (TextView) findViewById(R.id.acceptTermsConditionBtn);

        viewPager = (ViewPager) findViewById(R.id.tourPager);
        tourProgress = (ProgressBar) findViewById(R.id.progressBarStreamProgress);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnBack = (Button) findViewById(R.id.back);
        btnBack.setVisibility(View.GONE);
        tourPagerAdapter = new TourPagerAdapter(this);
        viewPager.setAdapter(tourPagerAdapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(0);






    }

    private void attachListeners(){

        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                return false;
            }
        });

        btnNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                return false;
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TourPage","clicked Next currentNo -"+viewPager.getCurrentItem());
                if(viewPager.getCurrentItem()==3){
                    SharedPrefrenceUtils.getInstance(TourActivity.this).setTermsAccepted(true);
                    navigateToNext();
                }else{
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }

                if(viewPager.getCurrentItem()>1)
                    btnBack.setVisibility(View.VISIBLE);



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
                    btnNext.setEnabled(true);
                    btnNext.setVisibility(View.VISIBLE);
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
                btnNext.setVisibility(View.VISIBLE);

                btnNext.setText("Accept & Proceed");
            }
            if(position>0 && position<3){
                // still pages are left

                btnBack.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
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
