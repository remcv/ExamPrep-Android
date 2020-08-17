package remcv.com.github.examprep.controller;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import remcv.com.github.examprep.model.ExamItem;

public class DatabaseHandler implements DatabaseCrud<ExamItem>
{
    // fields
    private static final String FILE_PATH = "database.csv";
    private static final String TAG = "ExamPrep";
    private List<ExamItem> examItemsList;
    private Activity mActivity;

    // constructor
    public DatabaseHandler(Activity activity)
    {
        mActivity = activity;
        examItemsList = new ArrayList<ExamItem>();
    }

    // methods from interface
    @Override
    public boolean add(ExamItem item)
    {
        return examItemsList.add(item);
    }

    @Override
    public ExamItem read(int id)
    {
        // TODO
        return null;
    }

    @Override
    public boolean update(ExamItem item)
    {
        // TODO
        return false;
    }

    @Override
    public boolean delete(int id)
    {
        // TODO
        return false;
    }

    @Override
    public void loadDb(File databaseFile)
    {
        try (BufferedReader br = new BufferedReader(new FileReader(databaseFile)))
        {
            int categoryNumber;
            String problem;
            boolean isDone;

            String s;
            String[] oneRow;

            while((s = br.readLine()) != null)
            {
                oneRow = s.split(",", 3);
                categoryNumber = Integer.parseInt(oneRow[0]);
                problem = oneRow[1];
                isDone = Boolean.parseBoolean(oneRow[2]);

                ExamItem item = new ExamItem(categoryNumber, problem, isDone);
                examItemsList.add(item);
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "loadDb() exception thrown " + e.toString());
            Toast.makeText(mActivity.getApplicationContext(), "Load failed ...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void saveDb(File databaseFile)
    {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(databaseFile)))
        {
            StringBuilder oneItem = new StringBuilder();

            for (ExamItem item : examItemsList)
            {
                oneItem.append(item.getCategoryNumber());
                oneItem.append(",");
                oneItem.append(item.getProblem());
                oneItem.append(",");
                oneItem.append(item.getIsDone());

                bw.write(oneItem.toString());
                Log.d(TAG, "saveDb() item " + item.toString());
                bw.newLine();
                bw.flush();

                oneItem.delete(0, oneItem.length());
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "saveDb() exception thrown " + e.toString());
        }
    }

    // methods - getters
    @Override
    public List<ExamItem> getList()
    {
        return examItemsList;
    }

    @Override
    public String getDatabasePath()
    {
        return FILE_PATH;
    }
}
