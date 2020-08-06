package remcv.com.github.examprep.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import remcv.com.github.examprep.R;
import remcv.com.github.examprep.model.ExamItem;

public class ExamItemAdapter extends BaseAdapter
{
    // fields
    private List<ExamItem> list;
    private Activity activity;

    // constructor
    public ExamItemAdapter(List<ExamItem> list, Activity activity)
    {
        this.list = list;
        this.activity = activity;
    }

    // methods from parent class
    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public ExamItem getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View oneExamItemView;
        LayoutInflater inflater = LayoutInflater.from(activity);
        oneExamItemView = inflater.inflate(R.layout.exam_item, parent, false);

        // initialize layout
        TextView id_TV = oneExamItemView.findViewById(R.id.idTextView_EI);
        TextView categoryNumber_TV = oneExamItemView.findViewById(R.id.categoryNumberTextView_EI);
        TextView problem_TV = oneExamItemView.findViewById(R.id.problemTextView_EI);

        // put data in view
        ExamItem examItem = list.get(position);
        id_TV.setText(String.valueOf(examItem.getId()));
        categoryNumber_TV.setText(String.valueOf(examItem.getCategoryNumberFromExamList()));
        problem_TV.setText(examItem.getItem());

        return oneExamItemView;
    }
}
