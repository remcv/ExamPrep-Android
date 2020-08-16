package remcv.com.github.examprep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputLayout;

import remcv.com.github.examprep.utils.TableConstants;
import remcv.com.github.examprep.utils.Utils;

public class AddExamItemActivity extends AppCompatActivity
{
    // fields - data
    public static final String TAG = "ExamPrep";

    // fields - layout
    private TextInputLayout categoryNumber_TIL;
    private TextInputLayout problem_TIL;
    private Button addItem_Button;
    private CheckBox subjectIsSolved_CB;

    // methods - lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam_item);

        // initialize data

        // initialize layout
        initializeLayout();

        // handle button events
        addItem_Button.setOnClickListener((v) -> onAddItemButtonClicked());
    }

    // methods - handle button clicks
    public void onAddItemButtonClicked()
    {
        // check input data validity
        if (Utils.isNumberValid(categoryNumber_TIL) & Utils.isStringInputValid(problem_TIL))
        {
            int categoryNumber = Integer.parseInt(categoryNumber_TIL.getEditText().getText().toString());
            String problem = problem_TIL.getEditText().getText().toString();
            boolean isDone = subjectIsSolved_CB.isChecked();

            Log.d(TAG, "onAddItemButtonClicked: isDone before - " + isDone);

            Intent intent = new Intent(AddExamItemActivity.this, MainActivity.class);
            intent.putExtra(TableConstants.CATEGORY_NUMBER, categoryNumber);
            intent.putExtra(TableConstants.PROBLEM, problem);
            intent.putExtra(TableConstants.IS_DONE, isDone);

            // set result to pass to MainActivity
            setResult(RESULT_OK, intent);

            // finish current activity without starting a new MainActivity
            finish();
        }
    }

    // methods - data
    public void initializeData()
    {
        // TODO
    }

    // methods layout
    public void initializeLayout()
    {
        categoryNumber_TIL = findViewById(R.id.categoryNumberTextInputLayout_AAEI);
        problem_TIL = findViewById(R.id.problemTextInputLayout_AAEI);
        addItem_Button = findViewById(R.id.addItemButton_AAEI);
        subjectIsSolved_CB = findViewById(R.id.subjectSolvedCheckBox_AAEI);
    }
}