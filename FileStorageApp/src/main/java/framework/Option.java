package framework;

import java.util.*;

/**
 * Holds option title, nested question and required inputs.
 */
public class Option implements SelectionCallback {

    private String title;
    private Question question;
    private Map<String, Input> inputs;

    /**
     * One and only constructor.
     * @param title Option title which is displayed to the user.
     */
    public Option(String title) {
        this.title = title;

        inputs = new LinkedHashMap<>();
    }

    String getTitle() {
        return title;
    }

    boolean hasQuestion() {
        return question != null;
    }

    /**
     * Builder method that adds nested question.
     * @param question Question that will be displayed after selecting current option.
     * @return Current {@code Option} instance.
     */
    public Option setQuestion(Question question) {
        this.question = question;

        return this;
    }

    Question getQuestion() {
        return question;
    }

    boolean hasInputs() {
        return !inputs.isEmpty();
    }

    /**
     * Returns {@code Input} instance from the input collection of current {@code Option}.
     * @param title Input title which acts as a key to search by in a collection of {@code Input} key value pairs
     * @return Requested {@code Input} instance that can be used to retrieve user input.
     */
    public Input getInput(String title) {
        return inputs.get(title.toLowerCase());
    }

    /**
     * Builder method that adds and input to the collection of inputs.
     * @param input Input that will be required from the user after selecting current option.
     * @return Current {@code Option} instance.
     */
    public Option addInput(Input input) {
        inputs.put(input.getKey().toLowerCase(), input);

        return this;
    }

    Collection<Input> getInputs() {
        return inputs.values();
    }

    @Override
    public void onSelected() {

    }
}
