package org.codeforafrica.storycheck.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.codeforafrica.storycheck.R;

/**
 * Created by nickhargreaves on 11/4/15.
 */
public class MyPostsAdapter extends BaseAdapter{

    private Context mContext;

    public MyPostsAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mView = inflater.inflate(R.layout.my_posts_list_item, parent, false);


        return mView;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
