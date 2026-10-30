import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean windows = System.getProperty("os.name")
                .toLowerCase(Locale.ROOT)
                .startsWith("windows");

        List<String> command = new ArrayList<>();
        if (windows) {
            command.add("cmd.exe");
            command.add("/c");
            command.add("mvnw.cmd");
        } else {
            command.add("./mvnw");
        }
        command.add("spring-boot:run");

        Process process = new ProcessBuilder(command)
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
