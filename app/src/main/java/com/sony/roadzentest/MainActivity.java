package com.sony.roadzentest;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        LinearLayout linearLayout=findViewById(R.id.ll);
        setSupportActionBar(toolbar);
        linearLayout.setFocusable(true);
        linearLayout.setFocusableInTouchMode(true);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {

           JSONArray jsonArray=new JSONArray(loadDataFromAssets());
           JSONObject jsonObject=jsonArray.getJSONObject(0);
            JSONArray array=jsonObject.getJSONArray("questions");
            for(int i=0;i<array.length();i++)
            {
                JSONObject views=array.getJSONObject(i);
                // populate the layout with the views
                switch (views.get("type").toString())
                {
                    case "image":
                        ImageView imageView=new ImageView(this);
                        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(400,400);
                        lp.gravity= Gravity.CENTER;
                        imageView.setLayoutParams(lp);
                        imageView.setId(i);
                        imageView.setPadding(10,10,10,10);
                        Picasso.get().load(views.get("url").toString()).resize(400,400)
                                .centerCrop().into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                System.out.println(e);
                            }
                        });
                        System.out.println(views.get("url").toString());
                        linearLayout.addView(imageView);
                        break;
                    case "text":
                        EditText et = new EditText(this);
                        LinearLayout.LayoutParams p = new
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        et.setLayoutParams(p);
                        et.setId(i);
                        et.setHint(views.get("hint").toString());
                        linearLayout.addView(et);

                        break;
                    case "textNumeric":
                        EditText numericET = new EditText(this);
                        LinearLayout.LayoutParams layoutParams = new
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        numericET.setLayoutParams(layoutParams);
                        numericET.setInputType(InputType.TYPE_CLASS_NUMBER);
                        numericET.setId(i);
                        numericET.setHint(views.get("hint").toString());
                        linearLayout.addView(numericET);

                        break;

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String loadDataFromAssets()
    {
        String json=null;
        try {
            InputStream inputStream=this.getAssets().open("assignmentJSON_1.5.1.json");
            int size=inputStream.available();
            byte[] buffer=new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json=new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent i=new Intent(MainActivity.this,CustomerDetails.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(MainActivity.this,LocationUpdates.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
