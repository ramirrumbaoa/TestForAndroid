package com.quipper.exam.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public interface Callback {
        void load();
    }

    private Button loadButton;
    private ImageView earthImage;
    private TextView dateText;
    private Callback callback;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        loadButton = (Button) view.findViewById(R.id.load_button);
        earthImage = (ImageView) view.findViewById(R.id.earth_image);
        dateText = (TextView) view.findViewById(R.id.date_text);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.load();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        callback = (Callback) activity;
    }

    public void showImage(Bitmap bitmap) {
        earthImage.setImageBitmap(bitmap);
    }

    public void setDateLabel(CharSequence dateLabel) {
        dateText.setText(dateLabel);
    }
}
