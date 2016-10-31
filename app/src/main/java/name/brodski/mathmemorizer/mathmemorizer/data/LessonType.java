package name.brodski.mathmemorizer.mathmemorizer.data;

public enum LessonType {
    MULTIPLICATION("Multiplication"),
    DIVISION("Division");

    private String hrText;
    LessonType(String hrText) {
        this.hrText = hrText;
    }

    public String getHRText() {
        return hrText;
    }
}
