package remcv.com.github.examprep.model;

public class ExamItem
{
    // fields
    private int id;
    private int categoryNumberFromExamList;
    private String item;

    // constructor
    public ExamItem(int id, int categoryNumberFromExamList, String item)
    {
        this.id = id;
        this.categoryNumberFromExamList = categoryNumberFromExamList;
        this.item = item;
    }

    // methods
    public int getId()
    {
        return id;
    }

    public int getCategoryNumberFromExamList()
    {
        return categoryNumberFromExamList;
    }

    public String getItem()
    {
        return item;
    }
}
