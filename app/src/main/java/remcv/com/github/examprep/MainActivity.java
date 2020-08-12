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
import java.util.Collections;
import java.util.List;

import remcv.com.github.examprep.controller.DatabaseCrud;
import remcv.com.github.examprep.controller.DatabaseHandler;
import remcv.com.github.examprep.model.ExamItem;
import remcv.com.github.examprep.utils.TableConstants;
import remcv.com.github.examprep.utils.Utils;
import remcv.com.github.examprep.view.ExamItemAdapter;

public class MainActivity extends AppCompatActivity implements TableConstants
{
    // fields - data
    private File sourceFile;
    private DatabaseCrud<ExamItem> databaseHandler;
    private ExamItemAdapter adapter;
    private static final String TAG = "ExamPrep";
    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int UPDATE_DELETE_ITEM_REQUEST_CODE = 2;

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
        countdown_TV.setText(String.valueOf(Utils.calculateDaysLeft()));

        // ListView adapter
        adapter = new ExamItemAdapter(databaseHandler.getList(), MainActivity.this);
        problems_LV.setAdapter(adapter);

        // handle events
        addButton.setOnClickListener((v) -> onAddButtonClicked());
        generateListOfProblems_TB.setOnCheckedChangeListener((buttonView, isChecked) -> onToggleButtonStateChanged(isChecked));
        problems_LV.setOnItemClickListener((parent, view, position, id) -> onListViewItemClicked(position));
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
                case UPDATE_DELETE_ITEM_REQUEST_CODE:
                    onUpdateDeleteReturn(data);
                    break;
            }
        }

        Collections.sort(databaseHandler.getList());
    }

    // methods - handle events
    public void onListViewItemClicked(int position)
    {
        // run code only if the toggle button is off
        if (!generateListOfProblems_TB.isChecked())
        {
            ExamItem examItem = databaseHandler.getList().get(position);

            Intent intent = new Intent(MainActivity.this, UpdateDeleteItemActivity.class);
            intent.putExtra(TableConstants.CATEGORY_NUMBER, examItem.getCategoryNumber());
            intent.putExtra(TableConstants.PROBLEM, examItem.getProblem());
            intent.putExtra(TableConstants.INDEX, position);

            int requestCode = 2;
            startActivityForResult(intent, requestCode);
        }
    }


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

    public void onUpdateDeleteReturn(Intent data)
    {
        // get metadata
        String buttonName = data.getStringExtra(TableConstants.BUTTON_NAME);
        int index = data.getIntExtra(TableConstants.INDEX, 0);

        if (buttonName.equals("deleteButton"))
        {
            onDeleteReturn(index);
        }
        else // buttonName.equals("updateButton")
        {
            onUpdateReturn(index, data);
        }

        // notify adapter of changes
        adapter.notifyDataSetChanged();
    }

    public void onDeleteReturn(int index)
    {
        databaseHandler.getList().remove(index);
        Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    public void onUpdateReturn(int index, Intent data)
    {
        // gather data
        int categoryNumber = data.getIntExtra(TableConstants.CATEGORY_NUMBER, -1);
        String problem = data.getStringExtra(TableConstants.PROBLEM);

        // create ExamItem and update it in list
        databaseHandler.getList().set(index, new ExamItem(categoryNumber, problem));
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