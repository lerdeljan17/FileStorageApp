package framework;

import java.util.LinkedList;
import java.util.List;

/**
 * Holds question title and available options.
 */
public class Question {

    private String title;
    private Question parent;
    private List<Option> options;

    /**
     * One and only constructor.
     * @param title Question title which is displayed to the user.
     */
    public Question(String title) {
        this.title = title;

        options = new LinkedList<>();
    }

    String getTitle() {
        return title;
    }

    boolean hasParent() {
        return parent != null;
    }

    Question getParent() {
        return parent;
    }

    void setParent(Question parent) {
        this.parent = parent;
    }

    List<Option> getOptions() {
        return new LinkedList<>(options);
    }

    /**
     * Builder method that adds an option to collection set of options.
     * @param option Option that is displayed below the question.
     * @return Current {@code Question} instance.
     */
    public Question addOption(Option option) {
        options.add(option);

        if(option.hasQuestion()) {
            option.getQuestion().setParent(this);
        }

        return this;
    }
}
