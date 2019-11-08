package framework;

/**
 * Same as {@code Option} class but with behaviour attached to it.
 */
public abstract class ExecuteOption extends Option {

    /**
     * One and only constructor which just calls super constructor.
     * @param title Option title which is displayed to the user.
     */
    public ExecuteOption(String title) {
        super(title);
    }

    /**
     * Template method meant to be implemented in subclasses. Gets called after selecting current option.
     */
    public abstract void execute();

    @Override
    public void onSelected() {
        execute();
    }
}
