package AFS;

/**
 * AFS test code.
 */
public class Test {

    public static void main(String[] args) {
        try {
            Connection conn = new Connection("localhost", 9000);
            conn.writeFile("foo.txt", "x");
            conn.writeFile("foo.txt", "y");
            System.out.println(conn.readFile("foo.txt"));
            System.out.println(conn.readBlock("foo.txt", 2, 4));
            System.out.println(conn.readLine("foo.txt", 0));

            System.out.println(conn.readLines("abc.txt", 1, 4, "2"));
            System.out.println(conn.countLines("abc.txt"));

            conn.createFile("xyz/bar.txt", "1");
            conn.writeFile("xyz/bar.txt", "meow!");
            System.out.println(conn.readFile("xyz/bar.txt", "1"));
            conn.deleteFile("xyz/bar.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
