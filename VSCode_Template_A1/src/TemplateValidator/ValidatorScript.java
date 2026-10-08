/*
 *            (  )   (   )  )
 *             ) (   )  (  (
 *             ( )  (    ) )
 *             _____________
 *            <_____________> ___
 *            |             |/ _ \
 *            |    HY252      | | |
 *            |   Validator   |_| |
 *         ___|             |\___/
 *        /    \___________/    \
 *        \_____________________/
 *
 * HY252 - Object Oriented Programming - Template Validator
 *
 * Run this before every submission.
 *
 * Checks:
 *   ERROR   #1  the src folder exists
 *   ERROR   #2  all exercise packages (A11, A12, ...) exist
 *   ERROR   #3  there are no java files outside the exercise packages
 *   WARNING #1  some exercise package is empty
 *   WARNING #2  files larger than 5MB (these are left out of the zip)
 *   WARNING #3  files that don't compile
 *
 * Errors must be fixed before a zip can be created. Warnings are informative.
 *
 * The zip is named <IDE>_A<series>_<AM>.zip and is saved next to the project
 * folder. Running the script again replaces the old zip.
 *
 * Don't change this file.
 */
package TemplateValidator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class ValidatorScript {

    private static final int SERIES = 1;
    private static final int EXERCISES = 4;
    private static final int AM_DIGITS = 4;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String SCRIPT_PACKAGE = "TemplateValidator";
    private static final String GUIDE = "the submission guide (Odigies Paradosis Askiseon) on elearn";
    private static final List<String> SKIPPED_DIRS = Arrays.asList(".git", ".metadata", "bin", "out", "build", "dist");
    private static final String[] IDES = {"NetBeans", "Eclipse", "IntelliJ", "VSCode"};

    private static final Color GREEN = new Color(0x2E7D32);
    private static final Color ORANGE = new Color(0xB26A00);
    private static final Color RED = new Color(0xC62828);

    enum Level { OK, WARNING, ERROR }

    static class Result {
        final Level level;
        final int code;
        final String text;

        Result(Level level, int code, String text) {
            this.level = level;
            this.code = code;
            this.text = text;
        }

        String tag() {
            if (level == Level.OK)
                return "[ OK ]";
            return "[" + level + " #" + code + "]";
        }
    }

    private final Path projectDir;
    private final Path srcDir;
    private final List<String> packages = new ArrayList<>();
    private final List<Result> results = new ArrayList<>();
    private Path zipFile;

    private ValidatorScript(Path projectDir) {
        this.projectDir = projectDir;
        this.srcDir = projectDir.resolve("src");
        for (int i = 1; i <= EXERCISES; i++)
            packages.add("A" + SERIES + i);
    }

    private void validate() throws IOException {
        if (!Files.isDirectory(srcDir)) {
            add(Level.ERROR, 1, "Source folder " + srcDir + " not found.\n"
                    + "Run the script from the project folder and don't change the template structure.");
            return;
        }
        add(Level.OK, 0, "Source folder exists: " + srcDir);

        List<String> missing = new ArrayList<>();
        List<String> empty = new ArrayList<>();
        for (String pack : packages) {
            Path dir = srcDir.resolve(pack);
            if (!Files.isDirectory(dir))
                missing.add(pack);
            else if (javaFiles(dir).isEmpty())
                empty.add(pack);
        }
        if (!missing.isEmpty()) {
            add(Level.ERROR, 2, "Missing packages " + missing + ".\n"
                    + "Don't rename or delete the packages " + packages + ", even if you skip an exercise.");
            return;
        }
        add(Level.OK, 0, "All packages exist");

        if (!empty.isEmpty())
            add(Level.WARNING, 1, "Empty packages: " + empty + "\nIgnore this if you skipped these exercises.");
        else
            add(Level.OK, 0, "No empty packages");

        List<Path> outside = new ArrayList<>();
        for (Path file : javaFiles(srcDir))
            if (!inPackage(file, packages) && !inPackage(file, Arrays.asList(SCRIPT_PACKAGE)))
                outside.add(file);
        if (!outside.isEmpty())
            add(Level.ERROR, 3, "Java files outside packages " + packages + ":\n" + fileList(outside)
                    + "Move them into the right package, or delete them if they are not needed.");
        else
            add(Level.OK, 0, "No java files outside the packages");

        checkLargeFiles();
        checkCompilation();
    }

    private void checkLargeFiles() throws IOException {
        List<Path> large = new ArrayList<>();
        for (Path file : projectFiles())
            if (Files.size(file) > MAX_FILE_SIZE)
                large.add(file);
        if (large.isEmpty())
            add(Level.OK, 0, "No files larger than " + MAX_FILE_SIZE / (1024 * 1024) + "MB");
        else
            add(Level.WARNING, 2, "These files are larger than " + MAX_FILE_SIZE / (1024 * 1024)
                    + "MB and will not be included in the zip:\n" + fileList(large)
                    + "Don't rely on them being submitted.");
    }

    private void checkCompilation() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            return;

        List<File> sources = new ArrayList<>();
        for (Path file : javaFiles(srcDir))
            if (!inPackage(file, Arrays.asList(SCRIPT_PACKAGE)))
                sources.add(file.toFile());
        if (sources.isEmpty())
            return;

        Path tmp = Files.createTempDirectory("hy252");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            List<String> options = Arrays.asList("-d", tmp.toString(), "-cp", classpath(), "-encoding", "UTF-8");
            compiler.getTask(null, fm, diagnostics, options, null, fm.getJavaFileObjectsFromFiles(sources)).call();
        } finally {
            deleteDir(tmp);
        }

        // keep only the first error per file
        Map<String, String> errors = new TreeMap<>();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() != Diagnostic.Kind.ERROR || d.getSource() == null)
                continue;
            String file = projectDir.relativize(Paths.get(d.getSource().toUri())).toString();
            if (!errors.containsKey(file))
                errors.put(file, "line " + d.getLineNumber() + ": " + d.getMessage(null).split("\n")[0]);
        }

        if (errors.isEmpty()) {
            add(Level.OK, 0, "All files compile");
            return;
        }
        StringBuilder sb = new StringBuilder("These files don't compile:\n");
        for (Map.Entry<String, String> e : errors.entrySet())
            sb.append("  ").append(e.getKey()).append(" (").append(e.getValue()).append(")\n");
        sb.append("The zip will still be created, but code that doesn't compile may not be graded.");
        add(Level.WARNING, 3, sb.toString());
    }

    private boolean hasErrors() {
        for (Result r : results)
            if (r.level == Level.ERROR)
                return true;
        return false;
    }

    private int count(Level level) {
        int n = 0;
        for (Result r : results)
            if (r.level == level)
                n++;
        return n;
    }

    private void add(Level level, int code, String text) {
        results.add(new Result(level, code, text));
    }

    // fallback when there is no display, e.g. over ssh
    private void printResults() {
        for (Result r : results) {
            if (r.level == Level.OK)
                System.out.println(r.tag() + " " + r.text);
            else
                System.out.println("\n" + r.tag() + " " + r.text + "\nSee " + GUIDE + "\n");
        }
        System.out.println("\nDone with " + count(Level.ERROR) + " errors, " + count(Level.WARNING) + " warnings");
        System.out.println("No display available, zip was not created");
    }

    private Path zipProject(String ide, String am) throws IOException {
        Path zip = projectDir.getParent().resolve(ide + "_A" + SERIES + "_" + am + ".zip");
        Files.deleteIfExists(zip);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip))) {
            for (Path file : projectFiles()) {
                if (Files.size(file) > MAX_FILE_SIZE)
                    continue;
                String entry = projectDir.relativize(file).toString().replace('\\', '/');
                zos.putNextEntry(new ZipEntry(entry));
                Files.copy(file, zos);
                zos.closeEntry();
            }
        }
        return zip;
    }

    private void showWindow() {
        JFrame frame = new JFrame("HY252 - Assignment " + SERIES + " submission");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (Result r : results) {
            list.add(resultLabel(r));
            list.add(Box.createVerticalStrut(6));
        }

        String detected = detectIde();
        JComboBox<String> ideBox = new JComboBox<>(IDES);
        if (detected != null)
            ideBox.setSelectedItem(detected);
        JTextField amField = new JTextField(6);
        JButton zipButton = new JButton("Create zip");
        JButton openButton = new JButton("Open folder");
        openButton.setVisible(false);
        JLabel status = new JLabel(" ");

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("IDE:"));
        form.add(ideBox);
        form.add(new JLabel("AM:"));
        form.add(amField);
        form.add(zipButton);
        form.add(openButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 6, 8, 6));
        bottom.add(form, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);

        if (hasErrors()) {
            zipButton.setEnabled(false);
            amField.setEnabled(false);
            ideBox.setEnabled(false);
            setStatus(status, RED, "Fix the errors above and run the script again.");
        } else {
            setStatus(status, Color.DARK_GRAY, count(Level.WARNING) + " warnings. Choose your IDE, give your AM and create the zip.");
        }

        zipButton.addActionListener(e -> {
            String ide = (String) ideBox.getSelectedItem();
            if (detected != null && !detected.equals(ide)) {
                setStatus(status, RED, "This project is for " + detected + ", not " + ide + ". Choose " + detected + ".");
                return;
            }
            String am = amField.getText().trim();
            if (!am.matches("\\d{" + AM_DIGITS + "}")) {
                setStatus(status, RED, "AM must be exactly " + AM_DIGITS + " digits.");
                return;
            }
            try {
                zipFile = zipProject(ide, am);
                setStatus(status, GREEN, "Your project zipped successfully: " + zipFile);
                openButton.setVisible(Desktop.isDesktopSupported());
                frame.revalidate();
            } catch (IOException ex) {
                setStatus(status, RED, "Could not create the zip: " + ex.getMessage());
            }
        });
        openButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(zipFile.getParent().toFile());
            } catch (IOException ex) {
                setStatus(status, RED, "Could not open the folder: " + ex.getMessage());
            }
        });
        frame.getRootPane().setDefaultButton(zipButton);

        frame.add(new JScrollPane(list), BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(720, 460);
        frame.setLocationRelativeTo(null);

        // otherwise it may open behind the IDE
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        frame.setAlwaysOnTop(false);
        amField.requestFocusInWindow();
    }

    // the zip name must match the template the project came from
    private String detectIde() {
        if (Files.isDirectory(projectDir.resolve("nbproject")))
            return "NetBeans";
        if (Files.exists(projectDir.resolve(".project")))
            return "Eclipse";
        if (Files.isDirectory(projectDir.resolve(".idea")))
            return "IntelliJ";
        if (Files.isDirectory(projectDir.resolve(".vscode")))
            return "VSCode";
        return null;
    }

    private static JLabel resultLabel(Result r) {
        Color color = r.level == Level.OK ? GREEN : r.level == Level.WARNING ? ORANGE : RED;
        String[] lines = escape(r.text).split("\n");
        StringBuilder html = new StringBuilder("<html><b style='color:" + hex(color) + "'>" + r.tag() + "</b> " + lines[0]);
        for (int i = 1; i < lines.length; i++)
            html.append("<br>").append(lines[i].replace("  ", "&nbsp;&nbsp;&nbsp;&nbsp;"));
        return new JLabel(html.append("</html>").toString());
    }

    private static void setStatus(JLabel label, Color color, String text) {
        label.setForeground(color);
        label.setText(text);
    }

    private static String hex(Color c) {
        return String.format("#%06X", c.getRGB() & 0xFFFFFF);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String fileList(List<Path> files) {
        StringBuilder sb = new StringBuilder();
        for (Path file : files)
            sb.append("  ").append(projectDir.relativize(file)).append("\n");
        return sb.toString();
    }

    private List<Path> projectFiles() throws IOException {
        final List<Path> files = new ArrayList<>();
        Files.walkFileTree(projectDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (!dir.equals(projectDir) && SKIPPED_DIRS.contains(dir.getFileName().toString()))
                    return FileVisitResult.SKIP_SUBTREE;
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (!file.toString().endsWith(".class"))
                    files.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    private List<Path> javaFiles(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            return paths.filter(p -> Files.isRegularFile(p) && p.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }
    }

    private boolean inPackage(Path file, List<String> packs) {
        for (String pack : packs)
            if (file.startsWith(srcDir.resolve(pack)))
                return true;
        return false;
    }

    // include jars added to the project, e.g. jfugue
    private String classpath() throws IOException {
        StringBuilder cp = new StringBuilder(System.getProperty("java.class.path", ""));
        for (Path file : projectFiles())
            if (file.toString().endsWith(".jar"))
                cp.append(File.pathSeparator).append(file);
        return cp.toString();
    }

    private void deleteDir(Path dir) throws IOException {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(dir)) {
            paths = walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }
        for (Path p : paths)
            Files.deleteIfExists(p);
    }

    public static void main(String[] args) throws IOException {
        Path projectDir = Paths.get(args.length > 0 ? args[0] : "").toAbsolutePath().normalize();
        ValidatorScript validator = new ValidatorScript(projectDir);

        validator.validate();

        if (GraphicsEnvironment.isHeadless()) {
            validator.printResults();
            System.exit(validator.hasErrors() ? 1 : 0);
        }
        SwingUtilities.invokeLater(validator::showWindow);
    }
}
