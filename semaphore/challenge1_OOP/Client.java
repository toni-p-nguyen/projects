
public class Client {

    public static void main(String[] args) {
        LanguageContext context = new LanguageContext();

        context.setStrategy(new English());
        context.hello();

        context.setStrategy(new Spanish());
        context.hello();

        context.setStrategy(new Utf8());
        context.hello();
    }
}
