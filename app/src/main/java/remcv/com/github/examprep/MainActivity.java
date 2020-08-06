package remcv.com.github.examprep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import remcv.com.github.examprep.controller.DatabaseCrud;
import remcv.com.github.examprep.controller.DatabaseHandler;
import remcv.com.github.examprep.model.ExamItem;
import remcv.com.github.examprep.utils.Utils;
import remcv.com.github.examprep.view.ExamItemAdapter;

public class MainActivity extends AppCompatActivity
{
    // fields - data
    private File sourceFile;
    private DatabaseCrud<ExamItem> databaseHandler;
    private ExamItemAdapter adapter;
    private static final String TAG = "ExamPrep";
    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int UPDATE_ITEM_REQUEST_CODE = 2;
    private static final int DELETE_ITEM_REQUEST_CODE = 3;

    // fields - layout
    private TextView countdown_TV;
    private ListView problems_LV;
    private ToggleButton generateListOfProblems_TB;
    private Button addButton;
    private Button updateButton;
    private Button deleteButton;

    // methods - lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize data
        initializeData();

        // initiate layout
        initializeLayout();
        countdown_TV.setText(String.format("Days left - %s", calculateDaysLeft()));

        // ListView adapter
        adapter = new ExamItemAdapter(databaseHandler.getList(), MainActivity.this);
        problems_LV.setAdapter(adapter);

        // handle button events
        addButton.setOnClickListener((v) -> onAddButtonClicked());
        updateButton.setOnClickListener((v) -> onUpdateButtonClicked());
        deleteButton.setOnClickListener((v) -> onDeleteButtonClicked());
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // save database
        databaseHandler.saveDb(sourceFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case ADD_ITEM_REQUEST_CODE:
                    onAddItemReturn(data);
                    break;
                case UPDATE_ITEM_REQUEST_CODE:
                    //
                    break;
                case DELETE_ITEM_REQUEST_CODE:
                    //
                    break;
            }
        }
    }

    // methods - button events
    public void onAddButtonClicked()
    {
        Intent addIntent = new Intent(MainActivity.this, AddExamItemActivity.class);
        startActivityForResult(addIntent, ADD_ITEM_REQUEST_CODE);
    }

    public void onUpdateButtonClicked()
    {
//        Intent updateIntent = new Intent(MainActivity.this, UpdateExamItemActivity.class);
//        startActivityForResult(updateIntent, UPDATE_ITEM_REQUEST_CODE);
    }

    public void onDeleteButtonClicked()
    {
//        Intent deleteIntent = new Intent(MainActivity.this, DeleteExamItemActivity.class);
//        startActivityForResult(deleteIntent, DELETE_ITEM_REQUEST_CODE);
    }

    // methods - data
    public void initializeData()
    {
        // instantiate a DatabaseCrud implementation
        databaseHandler = new DatabaseHandler();

        // instantiate the database File object by accessing the Android device app storage
        sourceFile = new File(getApplicationContext().getFilesDir(), databaseHandler.getDatabasePath());

        // create the .csv file if it doesn't exist (for first time users)
        try
        {
            sourceFile.createNewFile();
        }
        catch (IOException e)
        {
            Log.e(TAG, "initializeData() " + e.getMessage());
        }

        // load the database from storage
        databaseHandler.loadDb(sourceFile);
    }

    public void onAddItemReturn(Intent data)
    {
        int id = data.getIntExtra("id", 0);

        // check index
        int index = Utils.getIndexFromId(id, databaseHandler.getList());

        if (index == -1) // id from input was not found in list
        {
            int categoryNumber = data.getIntExtra("categoryNumber", 0);
            String problem = data.getStringExtra("problem");

            ExamItem newExamItem = new ExamItem(id, categoryNumber, problem);
            databaseHandler.getList().add(newExamItem);

            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Exam item added", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "ID already in list", Toast.LENGTH_SHORT).show();
        }
    }

    public String calculateDaysLeft()
    {
        long daysLeft =  ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(2020,9,24));
        return String.valueOf(daysLeft);
    }

    // methods layout
    public void initializeLayout()
    {
        countdown_TV = findViewById(R.id.countdownTextView_AM);
        problems_LV = findViewById(R.id.problemsListView_AM);
        generateListOfProblems_TB = findViewById(R.id.generateListOfProblemsToggleButton_AM);
        addButton = findViewById(R.id.addButton_AM);
        updateButton = findViewById(R.id.updateButton_AM);
        deleteButton = findViewById(R.id.deleteButton_AM);
    }
}