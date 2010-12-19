
public class LanguageContext {

    Language language;

    public LanguageContext() {
    }

    public void setStrategy(Language l) {
        language = l;
    }

    public void hello() {
        language.hello();
    }
}
