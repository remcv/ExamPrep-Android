package remcv.com.github.examprep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
        countdown_TV.setText(String.format("Days left - %s", Utils.calculateDaysLeft()));

        // ListView adapter
        adapter = new ExamItemAdapter(databaseHandler.getList(), MainActivity.this);
        problems_LV.setAdapter(adapter);

        // handle button events
        addButton.setOnClickListener((v) -> onAddButtonClicked());
        generateListOfProblems_TB.setOnCheckedChangeListener((buttonView, isChecked) -> onToggleButtonStateChanged(isChecked));
        problems_LV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (!generateListOfProblems_TB.isChecked())
                {
                    ExamItem examItem = databaseHandler.getList().get(position);

                    Intent intent = new Intent(MainActivity.this, UpdateDeleteItemActivity.class);
                    intent.putExtra("categoryNumber", examItem.getCategoryNumber());
                    intent.putExtra("problem", examItem.getProblem());

                    int requestCode = 2;
                    startActivityForResult(intent, requestCode);
                }
            }
        });
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

        /*
        1 - add item
        2 - update OR delete
         */

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
//                    onDeleteItem();
                    break;
            }
        }

        Collections.sort(databaseHandler.getList());
    }

    // methods - button events
    public void onAddButtonClicked()
    {
        if (generateListOfProblems_TB.isChecked())
        {
            Toast.makeText(this, "Uncheck the toggle button first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent addIntent = new Intent(MainActivity.this, AddExamItemActivity.class);
            startActivityForResult(addIntent, ADD_ITEM_REQUEST_CODE);
        }
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

    public void onToggleButtonOn()
    {
        List<ExamItem> list = Utils.generateRandomSubjectList(3, databaseHandler.getList());

        if (list == null)
        {
            Toast.makeText(this, "Not enough subjects in your list", Toast.LENGTH_SHORT).show();
            generateListOfProblems_TB.setChecked(false);
        }
        else
        {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    public void onToggleButtonOff()
    {
        adapter.setList(databaseHandler.getList());
        adapter.notifyDataSetChanged();
    }

    public void onToggleButtonStateChanged(boolean isChecked)
    {
        if (isChecked)
        {
            onToggleButtonOn();
        }
        else
        {
            onToggleButtonOff();
        }
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
        Collections.sort(databaseHandler.getList());
    }

    public void onAddItemReturn(Intent data)
    {
        int categoryNumber = data.getIntExtra("categoryNumber", 0);
        String problem = data.getStringExtra("problem");

        ExamItem newExamItem = new ExamItem(categoryNumber, problem);
        databaseHandler.getList().add(newExamItem);

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Exam item added", Toast.LENGTH_SHORT).show();
    }

    public void onDeleteItem(int index)
    {
        databaseHandler.getList().remove(index);
        Toast.makeText(this, "Exam item deleted", Toast.LENGTH_SHORT).show();
    }

    // methods layout
    public void initializeLayout()
    {
        countdown_TV = findViewById(R.id.countdownTextView_AM);
        problems_LV = findViewById(R.id.problemsListView_AM);
        generateListOfProblems_TB = findViewById(R.id.generateListOfProblemsToggleButton_AM);
        addButton = findViewById(R.id.addButton_AM);
    }
}