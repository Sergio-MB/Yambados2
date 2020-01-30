package com.example.yambados2;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;


public class TimelineFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = TimelineFragment.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private ImageView imagenuser;


    private static final String[] FROM = {StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT,
            StatusContract.Column.IMAGE};
    private static final int[] TO = {R.id.list_item_text_user, R.id.list_item_text_message, R.id.list_item_text_created_at,R.id.list_item_image_user};

    private static final int LOADER_ID = 42;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("Sin datos...");
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), StatusContract.CONTENT_URI, null, null,
                null, StatusContract.DEFAULT_SORT);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // Inner class...
    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

           if (view.getId() == R.id.list_item_text_created_at){
                long timestamp = cursor.getLong(columnIndex);
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp);
                ((TextView)view).setText(relativeTime);
            }else if(view.getId() == R.id.list_item_image_user){
                imagenuser= ((ImageView)view.findViewById( R.id.list_item_image_user));
                String UrlFoto= cursor.getString(columnIndex);
                CargaImagenes nuevaTarea = new CargaImagenes();
                nuevaTarea.execute(UrlFoto);

            }else{
                return false;
            }
//editStatus=(EditText) view.findViewById(R.id.editStatus);
           /* if(view.getId() == R.id.list_item_image_user){
                ImageView imagenuser= ((ImageView)view.findViewById( R.id.list_item_image_user));
                String UrlFoto= cursor.getString(columnIndex);

                Bitmap obtener_imagen = get_imagen(UrlFoto);
               imagenuser.setImageBitmap(obtener_imagen);
            }*/
               // return false;
            // Convertimos el timestamp a tiempo relativo

            return true;
        }
    }

    //Pasar URL de imagen para mostrar en ImageView
    private class CargaImagenes extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            imagenuser.setImageBitmap(result);
            Log.d(TAG, " TENEMOS IMAGEN "+result);
        }

    }
    private Bitmap descargarImagen (String imageHttpAddress){
        URL imageUrl ;
        Bitmap imagen = null;
        try{
            imageUrl = new URL(imageHttpAddress);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.connect();
            imagen = BitmapFactory.decodeStream(conn.getInputStream());

        }catch(IOException ex){
            Log.d(TAG, " ERROR "+ex);
            ex.printStackTrace();
        }
        return imagen;
    }




}
