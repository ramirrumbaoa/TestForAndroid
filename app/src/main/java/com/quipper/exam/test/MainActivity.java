package com.quipper.exam.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {
    private MainActivityFragment fragment;
    private LoadTask loadTask;

    private static final SimpleDateFormat IMAGE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHH", Locale.US);
    private static final SimpleDateFormat LABEL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:00", Locale.US);

    private static SharedPreferences sharedpreferences;

    static {
        TimeZone jst = TimeZone.getTimeZone("GMT+09:00");
        IMAGE_TIME_FORMAT.setTimeZone(jst);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.setRetainInstance(true);
        sharedpreferences = getSharedPreferences("SAVE", Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(decodeBase64(sharedpreferences.getString("IMAGE",null)) != null)
            fragment.showImage(decodeBase64(sharedpreferences.getString("IMAGE",null)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void load() {
        URL imageUrl;
        Date dateToShow = new Date(new Date().getTime() - 30 * 60 * 1000);
        //http://vba-m.com/forum/uploads/avatars/avatar_955.png
        //http://www.jma.go.jp/en/gms/imgs/5/infrared/1/201505190700-00.png
        String url = String.format("http://vba-m.com/forum/uploads/avatars/avatar_955.png",
                IMAGE_TIME_FORMAT.format(dateToShow));
        try {
            imageUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        if (loadTask != null &&
                loadTask.getStatus() == AsyncTask.Status.RUNNING &&
                !loadTask.isCancelled()) {
            return;
        }
        loadTask = new LoadTask(fragment);
        loadTask.execute(imageUrl);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("LABEL", LABEL_FORMAT.format(dateToShow));
        editor.commit();

        if(sharedpreferences.getString("LABEL","")!=null){
            fragment.setDateLabel(sharedpreferences.getString("LABEL",null));
        }else{
            fragment.setDateLabel(LABEL_FORMAT.format(dateToShow));
        }
    }

    static class LoadTask extends AsyncTask<URL, Void, List<Bitmap>> {
        private MainActivityFragment fragment;



        LoadTask(MainActivityFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected List<Bitmap> doInBackground(URL... params) {
            List<Bitmap> results = new ArrayList<>();
            try {
                for (URL url : params) {
                    if (isCancelled()) {
                        break;
                    }
                    URLConnection connection = url.openConnection();
                    Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    if (bitmap != null) {
                        results.add(bitmap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            if (!bitmaps.isEmpty()) {
                editor.putString("IMAGE", encodeTobase64(bitmaps.get(0)));
                editor.commit();
                fragment.showImage(bitmaps.get(0));
            }

            if(decodeBase64(sharedpreferences.getString("IMAGE",null)) != null)
                fragment.showImage(decodeBase64(sharedpreferences.getString("IMAGE",null)));

        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        //Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }


}
