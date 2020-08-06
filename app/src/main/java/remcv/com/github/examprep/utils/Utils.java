package remcv.com.github.examprep.utils;

import java.util.List;

import remcv.com.github.examprep.model.ExamItem;

public class Utils
{
    public static int getIndexFromId(int id, List<ExamItem> examItems)
    {
        for (ExamItem e : examItems)
        {
            if (id == e.getId())
            {
                return examItems.indexOf(e);
            }
        }
        return -1;
    }
}
