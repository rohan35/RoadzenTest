package com.sony.roadzentest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sony.roadzentest.utils.Constants;
import com.sony.roadzentest.utils.FetchAddressIntentService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Dell on 12-05-2018.
 */

public class CustomerDetails extends AppCompatActivity  {
    HashMap<Integer,Integer> set=new HashMap<>();
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    LinearLayout linearLayout;
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput="";
    private static final int MY_REQUEST_FINE_LOCATION=102;
    String[] result={""};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        getSupportActionBar().setTitle("Customer Details");
         linearLayout=findViewById(R.id.ll1);
        linearLayout.setFocusable(true);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        linearLayout.setFocusableInTouchMode(true);
        mResultReceiver=new AddressResultReceiver(new Handler());
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new
                                String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_REQUEST_FINE_LOCATION);
            }

        }
        else
        {
         // permission is granted
            getLocation();
        }



    }
    public void getLocation()
    {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            mLastLocation=location;
                            startIntentService();

                        }
                    }
                });
    }
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public String loadDataFromAssets()
    {
        String json=null;
        try {
            InputStream inputStream=this.getAssets().open("assignmentJSON_1.1.json");
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
    public boolean validateEditText(HashMap<Integer,Integer> ids)
    {
        boolean isError = false;

        for (Map.Entry<Integer,Integer> entry : ids.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if(value>0)
            {
                EditText et = findViewById(key);
                if(et.getText().length()<value) {
                    et.setError("Length must be " + value);
                    isError = true;
                }
            }

        }

        return isError;
    }

    @Override
    protected void onPause() {


        super.onPause();

    }

    @Override
    protected void onStop() {


        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if(!validateEditText(set))
        super.onBackPressed();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted,
                    getLocation();

                } else {
                    Toast.makeText(this, "Please provide permission ", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }

    }
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (mAddressOutput == null) {
                mAddressOutput = "";
            }
            result=mAddressOutput.split("Seperator");
            try {

                JSONArray jsonArray=new JSONArray(loadDataFromAssets());
                JSONObject jsonObject=jsonArray.getJSONObject(0);
                JSONArray array=jsonObject.getJSONArray("questions");
                for(int i=0;i<array.length();i++)
                {
                    JSONObject views=array.getJSONObject(i);
                    JSONObject validation;
                    int size=0;
                    if(views.has("validation"))
                    {
                        validation=views.getJSONObject("validation");
                        size=validation.getInt("size");
                    }
                    // populate the layout with the views
                    switch (views.get("type").toString())
                    {
                        case "text":
                            EditText et = new EditText(CustomerDetails.this);
                            LinearLayout.LayoutParams p = new
                                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            et.setLayoutParams(p);
                            et.setId(i);
                            set.put(i,size);
                            et.setHint(views.get("hint").toString());
                            if(views.get("hint").toString().equals("Customer Address"))
                            {
                                if(result.length>1)
                                    et.setText(result[0]);

                            }
                            linearLayout.addView(et);

                            break;
                        case "textNumeric":
                            EditText numericET = new EditText(CustomerDetails.this);
                            LinearLayout.LayoutParams layoutParams = new
                                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            numericET.setLayoutParams(layoutParams);
                            numericET.setInputType(InputType.TYPE_CLASS_NUMBER);
                            numericET.setId(i);
                            if(views.get("hint").toString().equals("ZipCode"))
                            {
                                if(result.length>1)
                                    numericET.setText(result[1]);

                            }
                            set.put(i,size);
                            numericET.setHint(views.get("hint").toString());;
                            linearLayout.addView(numericET);

                            break;

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }





        }
    }

}
