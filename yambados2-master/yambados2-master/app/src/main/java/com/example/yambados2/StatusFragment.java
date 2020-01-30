/*
@author Sergio Muñumer Blázquez
@author David Pastor Pérez
 */
package com.example.yambados2;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;

import android.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class StatusFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final String TAG="StatusActivity";
    EditText editStatus;
    Button buttonTweet;
    Twitter twitter;
    TextView textCount;
    Snackbar snackbar;
    private ProgressBar progressBar;
    SharedPreferences prefs;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_status, container, false );
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        editStatus=(EditText) view.findViewById(R.id.editStatus);
        buttonTweet=(Button)view.findViewById(R.id.buttonTweet);
        textCount=(TextView)view.findViewById(R.id.textCount);
        buttonTweet.setOnClickListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        textCount.setText(Integer.toString(280));
        textCount.setTextColor(Color.GREEN);
        editStatus.addTextChangedListener(this);

        return view;
    }

    @Override
    public void afterTextChanged(Editable statusText) {
        int count = 280 - statusText.length();

        textCount.setText(Integer.toString(count));
        textCount.setTextColor(Color.GREEN);
        if (count < 10)
            textCount.setTextColor(Color.YELLOW);
        if (count < 0)
            textCount.setTextColor(Color.RED);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onClick(View v) {

        String status=editStatus.getText().toString();
        Log.d(TAG,"onClicked");
        if(!status.equals(""))
        {
        new PostTask().execute(status);
        progressBar.setVisibility(v.VISIBLE);
        }else {
            snackbar.make(StatusFragment.this.getView(),"Texto vacio",snackbar.LENGTH_LONG).show();
        }

    }

    private final class PostTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params){

            String accesstoken = prefs.getString("accesstoken", "");
            String accesstokensecret = prefs.getString("accesstokensecret", "");
            // Comprobar si el nombre de usuario o el password están vacíos.
            // Si lo están, indicarlo mediante un Toast y redirigir al usuario a Settings
            if (TextUtils.isEmpty(accesstoken) || TextUtils.isEmpty(accesstokensecret)) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                return "Por favor, actualiza tu nombre de usuario y tu contraseña";
            }

            //accessToken  1181578365536477184-88c557ir6QVUreXeqwu9gt77nprFdT
            //accesstokensecret   Jc31P8kKza149anUg5zaOe21tOdlou60VDGZ6zwULk6fY

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey("NtXbTkScnPMcuHyLiH0wVwYy1")
                    .setOAuthConsumerSecret("ZTkOqiDynw2rvpPKmUx0yNHEYC5itBokDs7DSUCde3jj173NLL")
                    .setOAuthAccessToken(accesstoken)
                    .setOAuthAccessTokenSecret(accesstokensecret);
            TwitterFactory factory = new TwitterFactory(builder.build());
            twitter = factory.getInstance();

            try{
                twitter.updateStatus(params[0]);
                return "Tweet enviado correctamente";

            }catch (TwitterException e){
                Log.e(TAG,"Fallo en el envio");
                e.printStackTrace();
                return "Fallo en el envio del tweet";
            }
        }

        @Override
        protected void onPostExecute(String result){

            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            snackbar.make(StatusFragment.this.getView(),result,snackbar.LENGTH_LONG).show();
            editStatus.setText("");
        }
    }

}