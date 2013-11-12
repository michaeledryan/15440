package AFS;

/**
 * AFS test code.
 */
public class Test {

    public static void main(String[] args) {
        try {
            Connection conn = new Connection("localhost", 9000);
            conn.writeFile("foo.txt", "abcd");
            conn.writeFile("foo.txt", "defghi");
            System.out.println(conn.readFile("foo.txt"));
            System.out.println(conn.readBlock("foo.txt", 2, 4));

            conn.createFile("xyz/bar.txt", "alex2-Desktop:8000");
            conn.writeFile("xyz/bar.txt", "meow!");
            System.out.println(conn.readFile("xyz/bar.txt"));
            conn.deleteFile("xyz/bar.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
