package remcv.com.github.examprep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class AddExamItemActivity extends AppCompatActivity
{
    // fields - data

    // fields - layout
    private TextInputLayout categoryNumber_TIL;
    private TextInputLayout problem_TIL;
    private Button addItem_Button;

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
        if (isNumberValid(categoryNumber_TIL) & isProblemValid())
        {
            int categoryNumber = Integer.parseInt(categoryNumber_TIL.getEditText().getText().toString());
            String problem = problem_TIL.getEditText().getText().toString();

            Intent intent = new Intent(AddExamItemActivity.this, MainActivity.class);
            intent.putExtra("categoryNumber", categoryNumber);
            intent.putExtra("problem", problem);

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
    }

    // methods - validation
    private boolean isNumberValid(TextInputLayout til)
    {
        String idString = til.getEditText().getText().toString();

        // check if field is empty
        if (idString.trim().isEmpty())
        {
            til.setError("Empty fields are not allowed");
            return false;
        }
        else
        {
            try
            {
                Integer.parseInt(idString);
                til.setError(null);
                return true;
            }
            catch (NumberFormatException e)
            {
                til.setError("Invalid number");
                return false;
            }
        }
    }

    private boolean isProblemValid()
    {
        String problem = problem_TIL.getEditText().getText().toString();

        if (problem.trim().isEmpty())
        {
            problem_TIL.setError("Empty fields are not allowed");
            return false;
        }
        else
        {
            problem_TIL.setError(null);
            return true;
        }
    }
}