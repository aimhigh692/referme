package com.referme.android.referme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends Activity {
    private CallbackManager callbackManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AccessToken accessToken = null;

    private class ImagePagerAdapter extends PagerAdapter {
        private final int[] mImages = new int[] {
                R.drawable.t1,
                R.drawable.t2,
                R.drawable.t3,
        };

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }

        @Override
        public int getCount() {
            return this.mImages.length;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final Context context = LoginActivity.this;
            final ImageView imageView = new ImageView(context);
            // Currently set no padding
            //final int padding = context.getResources().getDimensionPixelSize(
            //        R.dimen.padding_small);
            //imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageResource(this.mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == ((ImageView) object);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setStatusBarTranslucent(true);
        setContentView(R.layout.activity_main_2);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        final ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);

        final CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        circleIndicator.setViewPager(viewPager);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "read_custom_friendlists"));


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.d(TAG, "User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Insert your code here
                                Log.d("response1 = ", response.toString());
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,friends{first_name,last_name,picture,birthday,email}");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "Login attempt failed.");
            }
        });
        Log.d(TAG, "In test2");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "In test5");
        Log.d(TAG, String.format("requestcode %d, resultCode %d, data %s", requestCode, resultCode, data));
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}