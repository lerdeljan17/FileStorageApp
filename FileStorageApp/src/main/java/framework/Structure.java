package framework;

/**
 * Holds question structure for the console application.
 */
public abstract class Structure {

    /**
     * Main question which is displayed at the beginning.
     */
    private Question question;

    /**
     * One and only default constructor which automatically calls {@link #create()}.
     */
    public Structure() {
        question = create();
    }

    /**
     * Template method meant to be implemented in subclasses.
     * @return Main question which could have nested questions, options and inputs.
     */
    protected abstract Question create();

    Question getQuestion() {
        return question;
    }
}
