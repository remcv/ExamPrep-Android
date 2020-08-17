package remcv.com.github.examprep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputLayout;

import remcv.com.github.examprep.utils.TableConstants;
import remcv.com.github.examprep.utils.Utils;

public class UpdateDeleteItemActivity extends AppCompatActivity implements TableConstants
{
    // fields - data
    private int indexOfItemInList;

    // fields - layout
    private TextInputLayout categoryNumber_til;
    private TextInputLayout problem_til;
    private Button updateButton;
    private Button deleteButton;
    private CheckBox subjectIsSolved_CB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_item);

        // initialize layout
        initializeLayout();

        // initialize data
        initializeData();

        // handle events
        deleteButton.setOnClickListener(v -> onDeleteButtonClicked());
        updateButton.setOnClickListener(v -> onUpdateButtonClicked());
    }

    // methods - handle events
    public void onDeleteButtonClicked()
    {
        // create intent
        Intent intent = new Intent(UpdateDeleteItemActivity.this, MainActivity.class);
        intent.putExtra(TableConstants.INDEX, indexOfItemInList);
        intent.putExtra(TableConstants.BUTTON_NAME, "deleteButton");

        // set the result code
        setResult(RESULT_OK, intent);

        // close activity
        finish();
    }

    public void onUpdateButtonClicked()
    {
        // validate user input
        if (Utils.isNumberValid(categoryNumber_til) & Utils.isStringInputValid(problem_til))
        {
            // gather data
            int categoryNumber = Integer.parseInt(categoryNumber_til.getEditText().getText().toString());
            String problem = problem_til.getEditText().getText().toString();
            boolean isDone = subjectIsSolved_CB.isChecked();

            // make Intent and put the data in extras
            Intent intent = new Intent(UpdateDeleteItemActivity.this, MainActivity.class);
            intent.putExtra(TableConstants.CATEGORY_NUMBER, categoryNumber);
            intent.putExtra(TableConstants.PROBLEM, problem);
            intent.putExtra(TableConstants.IS_DONE, isDone);
            intent.putExtra(TableConstants.INDEX, indexOfItemInList);
            intent.putExtra(TableConstants.BUTTON_NAME, "updateButton");

            // set result to pass to MainActivity
            setResult(RESULT_OK, intent);

            // finish current activity without starting a new MainActivity
            finish();
        }
    }

    // methods - data
    public void initializeData()
    {
        Intent intent = getIntent();

        int categoryNumber = intent.getIntExtra(TableConstants.CATEGORY_NUMBER, 0);
        String problem = intent.getStringExtra(TableConstants.PROBLEM);
        boolean isDone = intent.getBooleanExtra(TableConstants.IS_DONE, false);
        indexOfItemInList = intent.getIntExtra(TableConstants.INDEX, 0);

        categoryNumber_til.getEditText().setText(String.valueOf(categoryNumber));
        problem_til.getEditText().setText(problem);
        subjectIsSolved_CB.setChecked(isDone);
    }

    // methods - layout
    public void initializeLayout()
    {
        categoryNumber_til = findViewById(R.id.categoryNumberTextInputLayout_AUDI);
        problem_til = findViewById(R.id.problemTextInputLayout_AUDI);
        updateButton = findViewById(R.id.updateItemButton_AUDI);
        deleteButton = findViewById(R.id.deleteItemButton_AUDI);
        subjectIsSolved_CB = findViewById(R.id.subjectSolvedCheckBox_AUDI);
    }
}