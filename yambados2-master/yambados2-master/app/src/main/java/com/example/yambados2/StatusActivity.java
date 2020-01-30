/*
@author Sergio Muñumer Blázquez
@author David Pastor Pérez
 */
package com.example.yambados2;



import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG="StatusActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        if(savedInstanceState == null){

            StatusFragment fragment = new StatusFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

}

