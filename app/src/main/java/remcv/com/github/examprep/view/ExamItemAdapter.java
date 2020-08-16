package remcv.com.github.examprep.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        TextView categoryNumber_TV = oneExamItemView.findViewById(R.id.categoryNumberTextView_EI);
        TextView problem_TV = oneExamItemView.findViewById(R.id.problemTextView_EI);
        ImageView isDoneImage = oneExamItemView.findViewById(R.id.isDoneImageView_EI);

        // put data in view
        ExamItem examItem = list.get(position);
        categoryNumber_TV.setText(String.valueOf(examItem.getCategoryNumber()));
        problem_TV.setText(examItem.getProblem());
        if (examItem.getIsDone())
        {
            isDoneImage.setImageResource(R.drawable.check_icon);
        }

        return oneExamItemView;
    }

    // methods - setters
    public void setList(List<ExamItem> list)
    {
        this.list = list;
    }
}
