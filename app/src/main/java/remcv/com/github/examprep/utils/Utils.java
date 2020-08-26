package remcv.com.github.examprep.utils;

import android.util.Log;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import remcv.com.github.examprep.model.ExamItem;

public class Utils
{
    // fields
    public static final int MINUTES_PER_SUBJECT = 18;

    // methods
    public static String calculateDaysLeft(LocalDate examDate)
    {
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), examDate);
        return String.valueOf(daysLeft);
    }

    public static String calculateMinutes(int numberOfSubjects)
    {
        return String.valueOf(numberOfSubjects * MINUTES_PER_SUBJECT);
    }

    public static List<ExamItem> generateRandomSubjectList(int numberOfSubjects, List<ExamItem> list)
    {
        // setup
        int listSize = list.size();
        long numberOfCateg = list.stream()
                .map(examItem -> Integer.valueOf(examItem.getCategoryNumber()))
                .distinct()
                .count();

        ArrayList<Integer> indexesOfTakenSubjects = new ArrayList<>();
        ArrayList<Integer> categoriesPicked = new ArrayList<>();

        // check if numberOfSubjects is greater than the subjects from the list
        if (numberOfSubjects > listSize)
        {
            return null;
        }
        else if (numberOfSubjects > (int) numberOfCateg)
        {
            return null;
        }
        else
        {
            Random random = new Random();
            int tempNumber;

            while (indexesOfTakenSubjects.size() < numberOfSubjects)
            {
                tempNumber = random.nextInt(listSize);

                if (indexesOfTakenSubjects.contains(tempNumber))
                {
                    continue;
                }
                else
                {
                    if (categoriesPicked.contains(list.get(tempNumber).getCategoryNumber()))
                    {
                        continue;
                    }
                    else
                    {
                        indexesOfTakenSubjects.add(tempNumber);
                        categoriesPicked.add(list.get(tempNumber).getCategoryNumber());
                    }
                }
            }
        }

        ArrayList<ExamItem> listToReturn = new ArrayList<>();

        for (Integer i : indexesOfTakenSubjects)
        {
            listToReturn.add(list.get(i));
        }

        return listToReturn;
    }

    // input validation methods
    public static boolean isNumberValid(TextInputLayout til)
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

    public static boolean isStringInputValid(TextInputLayout til)
    {
        String problem = til.getEditText().getText().toString();

        if (problem.trim().isEmpty())
        {
            til.setError("Empty fields are not allowed");
            return false;
        }
        else
        {
            til.setError(null);
            return true;
        }
    }
}
