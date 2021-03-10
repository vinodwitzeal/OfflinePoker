package bigcash.poker.font;

public enum FontType {
    ROBOTO_REGULAR("rbr"),ROBOTO_BOLD("rbb");

    private String name;

    FontType(String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }
}
