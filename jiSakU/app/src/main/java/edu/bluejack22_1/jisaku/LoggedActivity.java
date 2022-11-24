package edu.bluejack22_1.jisaku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import edu.bluejack22_1.jisaku.adapters.ViewPagerAdapter;
import edu.bluejack22_1.jisaku.fragments.ActivityFragment;
import edu.bluejack22_1.jisaku.fragments.ExploreFragment;
import edu.bluejack22_1.jisaku.fragments.HomeFragment;
import edu.bluejack22_1.jisaku.fragments.PostFragment;
import edu.bluejack22_1.jisaku.fragments.ProfileFragment;

public class LoggedActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;

    private ViewPager viewPagerNav;

    private MenuItem itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);

        bottomNav = findViewById(R.id.bottom_navigation);
        viewPagerNav = findViewById(R.id.viewPager_nav);

        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.home_menu:
                        viewPagerNav.setCurrentItem(0);
                        break;
                    case R.id.explore_menu:
                        viewPagerNav.setCurrentItem(1);
                        break;
                    case R.id.post_menu:
                        viewPagerNav.setCurrentItem(2);
                        break;
                    case R.id.activity_menu:
                        viewPagerNav.setCurrentItem(3);
                        break;
                    case R.id.profile_menu:
                        viewPagerNav.setCurrentItem(4);
                        break;
                }
            }
        });

        setViewPagerNav(viewPagerNav);

        viewPagerNav.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(itemPosition != null) itemPosition.setChecked(false);
                else bottomNav.getMenu().getItem(0).setChecked(false);

                Log.d("page", "onPageSelected: "+position);

                bottomNav.getMenu().getItem(position).setChecked(true);
                itemPosition = bottomNav.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setViewPagerNav(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addNavFragment(new HomeFragment());
        adapter.addNavFragment(new ExploreFragment());
        adapter.addNavFragment(new PostFragment());
        adapter.addNavFragment(new ActivityFragment());
        adapter.addNavFragment(new ProfileFragment());
        viewPager.setAdapter(adapter);
    }
}