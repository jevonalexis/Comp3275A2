package com.jevon.comp3275a2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import com.jevon.comp3275a2.R;
import com.jevon.comp3275a2.pojo.CustomLocation;




/**
 * Created by jevon on 27-Mar-16.
 */
public class LocationDataAdapter extends ArrayAdapter<CustomLocation>{
    private ArrayList<CustomLocation> locations;

    public LocationDataAdapter(Context context, ArrayList<CustomLocation> locations) {
        super(context, 0, locations);
        this.locations = locations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if(convertView == null){
            //get inflater and inflate row layout
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.layout_row, parent, false);

            //instantiate viewholder and set fields
            viewHolder = new ViewHolder();
            viewHolder.lat = (TextView) rowView.findViewById(R.id.tv_lat_val);
            viewHolder.lng = (TextView) rowView.findViewById(R.id.tv_lng_val);
            viewHolder.alt = (TextView) rowView.findViewById(R.id.tv_alt_val);
            viewHolder.time = (TextView) rowView.findViewById(R.id.tv_time_val);

            rowView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) rowView.getTag();

        //set values for views
        viewHolder.lat.setText(locations.get(position).getLatitude()+"");
        viewHolder.lng.setText(locations.get(position).getLongitude() + "");
        viewHolder.alt.setText(locations.get(position).getAltitude()+"");
        viewHolder.time.setText(locations.get(position).getTime());

        return rowView;
    }


    public  class ViewHolder {
        public TextView lat;
        public TextView lng;
        public TextView alt;
        public TextView time;
    }
}
