package restore.sql;

import org.jetbrains.annotations.Nullable;

import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;

import restore.sql.hibernate.BasicFormatterImpl;
import restore.sql.hibernate.Formatter;
import restore.sql.hibernate.StringHelper;

/**
 * @author ob
 */
public class RestoreSqlFilter implements Filter {
    private final Project project;
    private final String defaultFileName = "restore.sql";
    public static String filePath;
    private static String prevLine = "";
    public static boolean sqlFormat = false;

    public RestoreSqlFilter(Project project) {
        this.project = project;
        this.filePath = project.getBasePath() + "/" + this.defaultFileName;
    }

    @Nullable
    @Override
    public Result applyFilter(final String currentLine, int endPoint) {
        if(currentLine.contains("Parameters:") && StringHelper.isNotEmpty(prevLine) && prevLine.contains("Preparing:")) {
            String preStr = "-- " + currentLine.split("Parameters:")[0].trim();
            String restoreSql = RestoreSqlUtil.restoreSql(prevLine, currentLine);
            println(preStr);
            if(sqlFormat) {
                restoreSql = format(restoreSql);
            }
            println(restoreSql);
            println("-- ----------------------------------------------------------------------------------------------------------------------");
        }
        prevLine = currentLine;
        return null;
    }

    public void println(String line) {
        FileUtil.appendTo(filePath, line);
    }

    public static String format(String sql) {
        Formatter formatter = new BasicFormatterImpl();
        return formatter.format(sql);
    }
}