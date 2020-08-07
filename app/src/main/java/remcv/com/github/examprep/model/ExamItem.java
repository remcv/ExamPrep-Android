package remcv.com.github.examprep.model;

public class ExamItem implements Comparable<ExamItem>
{
    // fields
    private int categoryNumber;
    private String problem;

    // constructor
    public ExamItem(int categoryNumber, String problem)
    {
        this.categoryNumber = categoryNumber;
        this.problem = problem;
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
}
