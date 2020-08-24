package remcv.com.github.examprep.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import remcv.com.github.examprep.R;

public class DialogNumberOfQuestions extends AppCompatDialogFragment
{
    // fields

    // constructor
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        //
        EditText numberOfQuestions_ET;

        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_of_questions, null);

        builder.setView(view)
                .setTitle("Number of questions for test")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });

        numberOfQuestions_ET = view.findViewById(R.id.numberOfQuestions_ET_DNQ);

        return builder.create();
    }


    // methods
}
