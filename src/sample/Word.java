package sample;

public class Word {
    private String name;
    private boolean isSelected = false;
    private int id;

    Word(String name, int id) {
        this.name = name;
        this.isSelected = false;
        this.id = id;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
