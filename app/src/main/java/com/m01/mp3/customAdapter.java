package com.m01.mp3;

/**
 * Created by Administrateur on 02/03/18.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import java.util.ArrayList;


import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.app.Activity;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by Administrateur on 13/02/18.
 */

public class customAdapter extends ArrayAdapter<customArrayList> {


    private static final String LOG_TAG = customAdapter.class.getSimpleName();

    public customAdapter(Activity context, ArrayList<customArrayList> customArrayList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, customArrayList);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        // Get the {@link AndroidFlavor} object located at this position in the list
        customArrayList audioItem = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView songName = (TextView) listItemView.findViewById(R.id.name);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        songName.setText(audioItem.getSongName());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView artist = (TextView) listItemView.findViewById(R.id.artist);

        artist.setText(audioItem.getArtist());


        TextView duration = (TextView) listItemView.findViewById(R.id.duration);
        duration.setText(MainActivity.timeFormat(audioItem.getDuration()));


        return listItemView;
    }

}


