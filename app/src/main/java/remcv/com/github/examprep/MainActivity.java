package remcv.com.github.examprep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    private static final int UPLOAD_CSV_REQUEST_CODE = 3;

    // fields - layout
    private TextView countdown_TV;
    private ListView problems_LV;
    private ToggleButton generateListOfProblems_TB;
    private Button addButton;
    private Button uploadButton;

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
        uploadButton.setOnClickListener(v -> onUploadButtonClicked());
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
        3 - upload database from csv file
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
                case UPLOAD_CSV_REQUEST_CODE:
                    onUploadReturn(data);
                    break;
            }
        }

        Collections.sort(databaseHandler.getList());
        adapter.notifyDataSetChanged();
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
            intent.putExtra(TableConstants.IS_DONE, examItem.getIsDone());
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
        List<ExamItem> list = databaseHandler.getList().stream()
                .filter(examItem -> !examItem.getIsDone())
                .collect(Collectors.toList());
        Log.d(TAG, "onToggleButtonOn: list from stream is " + list);

        list = Utils.generateRandomSubjectList(3, list);

        if (list == null)
        {
            Toast.makeText(this, "Not enough subjects or categories in your list", Toast.LENGTH_SHORT).show();
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

    public void onUploadButtonClicked()
    {
        // create Intent to open an external csv file
        Intent getCsvIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        getCsvIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getCsvIntent.setType("text/comma-separated-values");

        startActivityForResult(getCsvIntent, UPLOAD_CSV_REQUEST_CODE);
    }

    // methods - data
    public void initializeData()
    {
        // instantiate a DatabaseCrud implementation
        databaseHandler = new DatabaseHandler(MainActivity.this);

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
        int categoryNumber = data.getIntExtra(TableConstants.CATEGORY_NUMBER, 0);
        String problem = data.getStringExtra(TableConstants.PROBLEM);
        boolean isDone = data.getBooleanExtra(TableConstants.IS_DONE, false);

        ExamItem newExamItem = new ExamItem(categoryNumber, problem, isDone);
        databaseHandler.getList().add(newExamItem);

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
        boolean isDone = data.getBooleanExtra(TableConstants.IS_DONE, false);

        // create ExamItem and update it in list
        databaseHandler.getList().set(index, new ExamItem(categoryNumber, problem, isDone));
    }

    public void onDeleteItem(int index)
    {
        databaseHandler.getList().remove(index);
        Toast.makeText(this, "Exam item deleted", Toast.LENGTH_SHORT).show();
    }

    public void onUploadReturn(Intent data)
    {
        Uri csvUri = data.getData();
        InputStream inputStream = null;

        try
        {
            inputStream = getContentResolver().openInputStream(csvUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // if no exception was thrown until here, then clear the old list
            databaseHandler.getList().clear();

            String line;
            String[] lineArray;

            while ((line = reader.readLine()) != null)
            {
                lineArray = line.split(",", 3);
                int categoryNumber = Integer.parseInt(lineArray[0]);
                String problem = lineArray[1];
                boolean isDone = Boolean.parseBoolean(lineArray[2]);

                ExamItem item = new ExamItem(categoryNumber, problem, isDone);
                databaseHandler.getList().add(item);
            }

            reader.close();
            inputStream.close();
        }
        catch (Exception e)
        {
            Log.d(TAG, "onUploadReturn: exception " + e.getMessage());
            Toast.makeText(this, "File loading failed", Toast.LENGTH_SHORT).show();
        }
    }

    // methods layout
    public void initializeLayout()
    {
        countdown_TV = findViewById(R.id.countdownTextView_AM);
        problems_LV = findViewById(R.id.problemsListView_AM);
        generateListOfProblems_TB = findViewById(R.id.generateListOfProblemsToggleButton_AM);
        addButton = findViewById(R.id.addButton_AM);
        uploadButton = findViewById(R.id.uploadButton_AM);
    }
}