public class EnumTest {
    public static void main(String[] args) {
        TestEnum testEnum = TestEnum.valueOf("hello");
        System.out.println(testEnum);
    }
}
