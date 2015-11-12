package org.codeforafrica.storycheck.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rey.material.widget.CheckBox;

import org.codeforafrica.storycheck.R;
import org.codeforafrica.storycheck.data.DBHelper;
import org.codeforafrica.storycheck.data.QuestionObject;
import org.codeforafrica.storycheck.view.AvenirTextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionsListAdapter extends BaseAdapter{

    private Context mContext;
    private String checklist_id;
    private List<QuestionObject> questions = new ArrayList<>();

    public QuestionsListAdapter(Context context, String _checklist){
        this.mContext = context;
        this.checklist_id = _checklist;

        //get all questions for the checklist
        questions = getQuestions(checklist_id);
    }

    public QuestionObject getQuestion(int i) {
        return questions.get(i);
    }

    static class ViewHolder{
        AvenirTextView questionText;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View mView = convertView;

        if(mView == null) {
            mView = inflater.inflate(R.layout.questions_list_item, parent, false);
            //configure viewholder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.questionText = (AvenirTextView)mView.findViewById(R.id.question_text);
            viewHolder.checkBox = (CheckBox)mView.findViewById(R.id.checkBox);

            mView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder)mView.getTag();

        QuestionObject questionObject = questions.get(position);
        holder.questionText.setText(questionObject.getText());

        return mView;
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Function to get all the questions for the given checklist
     * param checklist_id
     * @return questions
     */
    public List<QuestionObject> getQuestions(String checklist_id){

        List<QuestionObject> questionObjects = new ArrayList<>();

        String queryString = "SELECT * FROM " + DBHelper.TABLE_QUESTIONS + " WHERE " + DBHelper.COLUMN_QUESTION_CHECKLIST + "=?";

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(queryString, new String[]{checklist_id});

        while (cursor.moveToNext()) {

            QuestionObject questionObject = new QuestionObject();

            questionObject.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_QUESTION_TEXT)));
            questionObject.setId(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_QUESTION_ID)));
            questionObjects.add(questionObject);

        }

        cursor.close();

        return questionObjects;
    }
}
