package remcv.com.github.examprep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class DeleteItemActivity extends AppCompatActivity
{
    // fields - data

    // fields - layout
    private TextInputLayout categoryNumber_til;
    private TextInputLayout problem_til;
    private Button updateButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete_item);

        // initialize layout
        initializeLayout();

        // initialize data
        initializeData();
    }

    // methods - data
    public void initializeData()
    {
        Intent intent = getIntent();

        int categoryNumber = intent.getIntExtra("categoryNumber", 0);
        String problem = intent.getStringExtra("problem");

        categoryNumber_til.getEditText().setText(String.valueOf(categoryNumber));
        problem_til.getEditText().setText(problem);
    }

    // methods - layout
    public void initializeLayout()
    {
        categoryNumber_til = findViewById(R.id.categoryNumberTextInputLayout_AUDI);
        problem_til = findViewById(R.id.problemTextInputLayout_AUDI);
    }
}