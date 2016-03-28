package com.jevon.comp3275a2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.jevon.comp3275a2.R;

import java.util.ArrayList;


/**
 * Created by jevon on 28-Mar-16.
 */
public class MainAdapter extends ArrayAdapter<String> {
    private ArrayList<String> titles;

    public MainAdapter(Context context, ArrayList<String> titles) {
        super(context, 0, titles);
        this.titles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if(convertView == null){
            //get inflater and inflate row layout
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.layout_item_main, parent, false);

            //instantiate viewholder and set fields
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.tv_title);
            viewHolder.letter = (ImageView) rowView.findViewById(R.id.iv_letter);

            rowView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) rowView.getTag();


        String firstLetter = String.valueOf(getItem(position).charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(getItem(position));
        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect(firstLetter, color, 25);


        //set values for views
        viewHolder.title.setText(titles.get(position));
        viewHolder.letter.setImageDrawable(drawable);

        return rowView;
    }

    public  class ViewHolder {
        public TextView title;
        public ImageView letter;
    }

}
