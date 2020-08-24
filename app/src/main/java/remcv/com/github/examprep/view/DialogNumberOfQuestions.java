package remcv.com.github.examprep.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
    private DialogNumberOfQuestionsListener dnoq;

    // nested interface
    public interface DialogNumberOfQuestionsListener
    {
        void getNumber(int number);
    }

    // constructor
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        //


        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_of_questions, null);
        EditText numberOfQuestions_ET = view.findViewById(R.id.numberOfQuestions_ET_DNQ);

        builder.setView(view)
                .setTitle("Number of questions for test")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();
                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int numberOfQuestions = Integer.parseInt(numberOfQuestions_ET.getText().toString());
                        dnoq.getNumber(numberOfQuestions);
                    }
                });



        return builder.create();
    }

    // methods
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            dnoq = (DialogNumberOfQuestionsListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement DialogNumberOfQuestionsListener");
        }
    }
}
