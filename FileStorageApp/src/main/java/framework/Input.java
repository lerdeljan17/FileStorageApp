package framework;

/**
 * Holds user input as key value pair.
 */
public class Input {

    private String key, value, title;

    /**
     * Basic constructor that accepts just an input key which is copied to title.
     * @param key Input key which is user to fetch input value.
     */
    public Input(String key) {
        this.key = key;
        this.title = key;
    }

    /**
     * Another constructor that accepts an input key and an input title.
     * @param key Input key which is user to fetch input value.
     * @param title Input title which is displayed to the user.
     */
    public Input(String key, String title) {
        this.key = key;
        this.title = title;
    }

    String getKey() {
        return key;
    }

    String getTitle() {
        return title;
    }

    /**
     * Returns value which is entered by the user.
     */
    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
