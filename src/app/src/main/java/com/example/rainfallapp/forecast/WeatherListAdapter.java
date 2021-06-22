package com.example.rainfallapp.forecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rainfallapp.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

public class WeatherListAdapter extends ArrayAdapter<Weather> {

    private Context context;
    private int resource;

    public WeatherListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Weather> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ImageLoader imageLoader = ImageLoader.getInstance();

        // Checks if imageLoader has been created
        // creates it if not
        if (!imageLoader.isInited()) {
            setupImageLoader();
        }

        // Getting the weather information
        String location = getItem(position).getLocation();
        String description = getItem(position).getDescription();
        String date = getItem(position).getDate();
        String precipitation = getItem(position).getPrecipitation();
        String temp = getItem(position).getTemp();
        String imageView = getItem(position).getImageView();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvLocation = (TextView) convertView.findViewById(R.id.listviewLocation);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.listviewDescription);
        TextView tvDate = (TextView) convertView.findViewById(R.id.listviewDate);
        TextView tvPrecipitation = (TextView) convertView.findViewById(R.id.listviewPrecipitation);
        TextView tvTemp = (TextView) convertView.findViewById(R.id.listviewTemp);
        ImageView ivImageView = (ImageView) convertView.findViewById(R.id.imageView);

        int defaultImage = context.getResources().getIdentifier("@drawable/u00d",null, context.getPackageName());

        //create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        //download and display image from url
        imageLoader.displayImage(imageView, ivImageView, options);

        tvLocation.setText(location);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvPrecipitation.setText("Precipitation: " + precipitation);
        tvTemp.setText(temp + " °C");

        return convertView;
    }

    /**
     * Required for setting up the Universal Image loader Library
     */
    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
