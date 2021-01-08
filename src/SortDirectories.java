import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Program takes as input from the command line an absolute path and prints out a list of
 * subdirectories and files contained within that path, sorted by size decreasing.
 *
 * @author Paulina Strzygowska
 */
public class SortDirectories {
    private static Path path = null;
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        if (!(args.length == 0)) {
            path = Path.of(args[0]);
        }
        checkPath();

        Map<Path, Long> pathsMap = mapDirectories(path);
        Map<Path, Long> sortedPaths = sortMap(pathsMap);

        printResult(sortedPaths);
    }

    /**
     * check, if given path is correct
     */
    private static void checkPath() {
        while (path == null || !path.isAbsolute()) {
            System.out.println("Please, give correct absolute path:");
            path = Path.of(getUserInput());
        }
    }

    /**
     * collect all files from given path to a HashMap
     */
    private static Map<Path, Long> mapDirectories(Path path) {
        Map<Path, Long> result = new HashMap<>();
        try (Stream<Path> walk = Files.walk(path, 1)) {
            result = walk
                    .filter(Files::isReadable)
                    .collect(Collectors.toMap(p -> p, p -> getFolderSize(p)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * calculate size of all files from given path - in kB
     */
    private static long getFolderSize(Path path) {
        long length = 0;
        try (Stream<Path> walk = Files.walk(path)) {
            length = walk
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length / 1024;
    }

    /**
     * sort map of paths by sizes (decreasing)
     */
    private static Map<Path, Long> sortMap(Map<Path, Long> paths) {
        Map<Path, Long> result = paths
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return result;
    }

    /**
     * format and print map
     */
    private static void printResult(Map<Path, Long> sortedPaths) {
        sortedPaths.forEach((key, value) -> System.out.println(prefix(key) + key + " - " + value + " kB"));
    }

    /**
     * add prefix 'File: ' or 'Dir: ', depending on whether given path direct to directory or to file
     */
    private static String prefix(Path path) {
        if (path.toFile().isFile()) {
            return "File: ";
        } else {
            return "Dir: ";
        }
    }

    /**
     * take an input from user
     */
    private static String getUserInput() {
        return scanner.nextLine().trim();
    }
}