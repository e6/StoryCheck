package org.codeforafrica.storycheck.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.codeforafrica.storycheck.R;
import org.codeforafrica.storycheck.data.AnswerObject;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.List;

/**
 * Created by nickhargreaves on 11/12/15.
 */
public class AnswersAdapter extends BaseAdapter {

    private Context mContext;
    private List<AnswerObject> answers;

    public AnswersAdapter(Context context, List<AnswerObject> _answerObjects){
        this.mContext = context;
        this.answers = _answerObjects;
    }
    static class ViewHolder{
        AvenirTextView answerText;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mView = convertView;

        if(mView == null) {
            mView = inflater.inflate(R.layout.item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.answerText = (AvenirTextView)mView.findViewById(R.id.answer_text);
            mView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder)mView.getTag();

        AnswerObject answerObject = answers.get(position);
        holder.answerText.setText(answerObject.getQuestion());

        return mView;
    }

    @Override
    public int getCount() {
        return answers.size();
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
