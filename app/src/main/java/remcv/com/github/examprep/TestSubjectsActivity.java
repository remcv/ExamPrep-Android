package remcv.com.github.examprep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import remcv.com.github.examprep.model.ExamItem;
import remcv.com.github.examprep.utils.TableConstants;
import remcv.com.github.examprep.utils.Utils;
import remcv.com.github.examprep.view.ExamItemAdapter;

public class TestSubjectsActivity extends AppCompatActivity implements TableConstants
{
    // fields - layout
    private TextView minutes_TV;
    private ListView subjects_LV;

    // fields - data
    private List<ExamItem> examItems;
    private ExamItemAdapter adapter;

    // methods - life cycle
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_subjects);

        // initialize layout
        initializeLayout();

        // initialize data
        initialDataSetup();

        // update layout
        updateLayout();
    }

    // methods - data
    public void initialDataSetup()
    {
        // get list of Exam items
        Intent getListIntent = getIntent();
        String listStringJson = getListIntent.getStringExtra(TableConstants.RANDOM_LIST);

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ExamItem>>() {}.getType();

        examItems = gson.fromJson(listStringJson, type);

        // set adapter
        adapter = new ExamItemAdapter(examItems, TestSubjectsActivity.this);
    }

    // methods - layout
    public void initializeLayout()
    {
        minutes_TV = findViewById(R.id.minutesTextView_ATS);
        subjects_LV = findViewById(R.id.problemsListView_ATS);
    }

    public void updateLayout()
    {
        // minutes
        String minutes = Utils.calculateMinutes(examItems.size());
        minutes_TV.setText(minutes);

        // put items in listView
        subjects_LV.setAdapter(adapter);
    }
}
