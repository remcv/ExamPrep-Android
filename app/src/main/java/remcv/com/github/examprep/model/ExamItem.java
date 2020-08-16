package remcv.com.github.examprep.model;

public class ExamItem implements Comparable<ExamItem>
{
    // fields
    private int categoryNumber;
    private String problem;
    private boolean isDone;

    // constructor
    public ExamItem(int categoryNumber, String problem, boolean isDone)
    {
        this.categoryNumber = categoryNumber;
        this.problem = problem;
        this.isDone = isDone;
    }

    // methods - getters
    public int getCategoryNumber()
    {
        return categoryNumber;
    }

    public String getProblem()
    {
        return problem;
    }

    public boolean getIsDone()
    {
        return isDone;
    }

    // methods - interface
    @Override
    public int compareTo(ExamItem other)
    {
        // by category number, than alphabetical
        if (this.categoryNumber == other.getCategoryNumber())
        {
            return this.problem.compareTo(other.getProblem());
        }
        else
        {
            return this.categoryNumber - other.getCategoryNumber();
        }
    }

    // methods - toString
    @Override
    public String toString()
    {
        return "ExamItem{" +
                "categoryNumber=" + categoryNumber +
                ", problem='" + problem + '\'' +
                ", isDone=" + isDone +
                '}';
    }
}
