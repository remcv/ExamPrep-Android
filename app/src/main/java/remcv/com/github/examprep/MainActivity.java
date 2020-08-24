package remcv.com.github.examprep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import remcv.com.github.examprep.controller.DatabaseCrud;
import remcv.com.github.examprep.controller.DatabaseHandler;
import remcv.com.github.examprep.model.ExamItem;
import remcv.com.github.examprep.utils.TableConstants;
import remcv.com.github.examprep.utils.Utils;
import remcv.com.github.examprep.view.DialogNumberOfQuestions;
import remcv.com.github.examprep.view.ExamItemAdapter;

public class MainActivity extends AppCompatActivity implements TableConstants, DialogNumberOfQuestions.DialogNumberOfQuestionsListener
{
    // fields - data
    private File sourceFile;
    private DatabaseCrud<ExamItem> databaseHandler;
    private ExamItemAdapter adapter;
    private int numberOfSubjectsToTest;
    private LocalDate examDate;

    // fields - static final
    private static final String TAG = "ExamPrep";
    private static final int ADD_ITEM_REQUEST_CODE = 1;
    private static final int UPDATE_DELETE_ITEM_REQUEST_CODE = 2;
    private static final int UPLOAD_TSV_REQUEST_CODE = 3;
    public static final String SHARED_PREFS = "mySharedPrefs";
    public static final String NUMBER_OF_SUBJECTS = "numberOfSubjects";

    // fields - layout
    private TextView countdown_TV;
    private ListView problems_LV;

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
        countdown_TV.setText(Utils.calculateDaysLeft(LocalDate.of(2020, Month.SEPTEMBER, 23)));

        // ListView adapter
        adapter = new ExamItemAdapter(databaseHandler.getList(), MainActivity.this);
        problems_LV.setAdapter(adapter);

        // handle events
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
        3 - import database from tsv file
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
                case UPLOAD_TSV_REQUEST_CODE:
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
        ExamItem examItem = databaseHandler.getList().get(position);

        Intent intent = new Intent(MainActivity.this, UpdateDeleteItemActivity.class);
        intent.putExtra(TableConstants.CATEGORY_NUMBER, examItem.getCategoryNumber());
        intent.putExtra(TableConstants.PROBLEM, examItem.getProblem());
        intent.putExtra(TableConstants.IS_DONE, examItem.getIsDone());
        intent.putExtra(TableConstants.INDEX, position);

        int requestCode = 2;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settingsMenu_itemAdd:
                onAddButtonClicked();
                break;
            case R.id.settingsMenu_itemGenerateRandomList:
                onGenerateRandomListOfSubjectsClicked();
                break;
            case R.id.settingsMenu_itemChangeExamDate:
                // TODO onChangeExamDateClicked();
                break;
            case R.id.settingsMenu_itemSetNoSubjectsInTest:
                createAlertDialogNumberOfQuestions();
                break;
            case R.id.settingsMenu_itemImport:
                onImportButtonClicked();
                break;
        }

        return true;
    }

    public void onAddButtonClicked()
    {
        Intent addIntent = new Intent(MainActivity.this, AddExamItemActivity.class);
        startActivityForResult(addIntent, ADD_ITEM_REQUEST_CODE);
    }

    public void onGenerateRandomListOfSubjectsClicked()
    {
        List<ExamItem> list = databaseHandler.getList().stream()
                .filter(examItem -> !examItem.getIsDone())
                .collect(Collectors.toList());

        list = Utils.generateRandomSubjectList(numberOfSubjectsToTest, list);

        if (list == null)
        {
            Toast.makeText(this, "Not enough subjects or categories in your list", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // send the list to TestSubjectsActivity
            Gson gson = new Gson();
            String listJson = gson.toJson(list);

            Intent toTestSubjectsActivityIntent = new Intent(MainActivity.this, TestSubjectsActivity.class);
            toTestSubjectsActivityIntent.putExtra(TableConstants.RANDOM_LIST, listJson);

            startActivity(toTestSubjectsActivityIntent);
        }
    }

    public void onImportButtonClicked()
    {
        // create Intent to open an external csv file
        Intent getCsvIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        getCsvIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getCsvIntent.setType("text/tab-separated-values");

        startActivityForResult(getCsvIntent, UPLOAD_TSV_REQUEST_CODE);
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

        // load the number of subjects (default value is 5)
        numberOfSubjectsToTest = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE).getInt(NUMBER_OF_SUBJECTS, 5);
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
                lineArray = line.split("\t", 3);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    // methods - alert dialog
    public void createAlertDialogNumberOfQuestions()
    {
        DialogNumberOfQuestions dialog = new DialogNumberOfQuestions();
        dialog.show(getSupportFragmentManager(), "DialogNumberOfQuestions");
    }

    @Override
    public void getNumber(int number)
    {
        numberOfSubjectsToTest = number;

        // save the new value to SharedPreferences
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(NUMBER_OF_SUBJECTS, numberOfSubjectsToTest);
        editor.apply();

        Toast.makeText(MainActivity.this, "Number of subjects was changed to " + numberOfSubjectsToTest, Toast.LENGTH_LONG).show();
    }
}
