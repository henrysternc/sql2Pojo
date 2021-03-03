package personal.stone.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Sql2PoJo {

    static final String CREATE = "CREATE";

    static final String create = "create";

    static final String SELECT = "SELECT";

    static final String select = "select";

    static final String AS = "AS";

    static final String as = "as";

    static final String lineFeed = "\r\n";

    static final String suffix = ".java";

    static final String sourcePath = "C:\\Users\\Administrator\\Videos\\Captures\\";

    static final String FROM = " FROM ";

    static final String from = " from ";

    static final String lineEnd = ";";

    static final String importLombok = "import lombok.Data;";

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\Administrator\\Videos\\Captures\\1.txt";
        String packageName = "personal.stone.test";
        String fileName =  "Test";
        String sql = getFileSqlString(filePath);
        System.out.println(sql);
        sql2PoJo(sql, sourcePath, packageName, fileName);
    }

    private static void sql2PoJo(String sql, String pojoPath, String packageName, String fileName) throws IOException {
        String checkedStr = checkStr(sql);
        List<String> columns = getSelectSQLColumns(checkedStr);
        generateJavaFile(columns, pojoPath, packageName, fileName);
    }

    private static void generateJavaFile(List<String> columns,
                                         String pojoPath,
                                         String packageName,
                                         String fileName) throws IOException{
        Path path = Paths.get(pojoPath+fileName+suffix);

        // 使用newBufferedWriter创建文件并写文件
        // 这里使用了try-with-resources方法来关闭流，不用手动关闭
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("package " + packageName + lineEnd + lineFeed + lineFeed +
                    importLombok + lineFeed + lineFeed + "@Data" + lineFeed
                    + "public class "+ fileName + " {"+lineFeed);

        }
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {

            for(int i = 0;i<columns.size();i++){
                String s = columns.get(i);
                //追加写模式
                writer.write("      private String "+s+";"+lineFeed);

            }
            writer.write("}");
        }
    }

    private static String splitFrom(String trimSql){
        if(trimSql.contains(FROM)){
            return trimSql.split(FROM)[0];
        }else if(trimSql.contains(from)){
            return trimSql.split(from)[0];
        }
        return trimSql;
    }

    private static List<String> getSelectSQLColumns(String trimSql){
        List<String> columns = new ArrayList<>();

        String columnStrs = splitFrom(trimSql);

        String[] columnStrArr = columnStrs.trim().split(",");

        for(int i=0;i<columnStrArr.length;i++){
            String columnStr = columnStrArr[i];
            if(columnStr == null || "".equals(columnStr))
                continue;
            if(columnStr.contains(AS)) {
                String[] s = columnStr.split(AS);
                columns.add(s[1].trim());
            }else if(columnStr.contains(as)){
                String[] s = columnStr.split(as);
                columns.add(s[1].trim());
            }else{
                columns.add(getCamelColumnStr(columnStr));
            }
        }
        return columns;
    }

    private static String getCamelColumnStr(String columnStr){
        String trim = columnStr.trim();
        if(trim.contains(".")){
            String[] split = trim.split("\\.");
            trim = split[1];
        }
        if(trim.contains("_")){
            String[] s = trim.split("_");
            StringBuffer sb = new StringBuffer();
            sb.append(s[0]);
            for(int i=1;i<s.length;i++){
                sb.append(titleCase(s[i]));
            }
            return sb.toString();
        }else{
            return trim;
        }
    }

    private static String titleCase(String s){
        String[] split = s.split("");
        StringBuffer sb = new StringBuffer();
        sb.append(split[0].toUpperCase());
        for(int i=1;i<split.length;i++){
            sb.append(split[i]);
        }
        return sb.toString();
    }

    /**
     * 校验SQL是否合法
     * @param sql
     * @return
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    private static String checkStr(String sql) throws NullPointerException, IllegalArgumentException{
        String trimSql = sql.trim();
        if(null == sql || "".equals(trimSql)){
            throw new NullPointerException("sql语句不能为空");
        }

        if(trimSql.startsWith(CREATE))
            return getNonPrefixKeySql(trimSql, CREATE);
        if(trimSql.startsWith(create))
            return getNonPrefixKeySql(trimSql, create);
        if(trimSql.startsWith(SELECT))
            return getNonPrefixKeySql(trimSql, SELECT);
        if(trimSql.startsWith(select))
            return getNonPrefixKeySql(trimSql, select);

        throw new IllegalArgumentException();
    }

    private static String getNonPrefixKeySql(String trimSql, String prefix){
        trimSql = trimSql.replace(prefix, "");
        return trimSql;
    }

    /**
     * 获取sql字符串
     * @param filePath
     * @return
     */
    private static String getFileSqlString(String filePath) {
        /* 读取数据 */
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)),
                    "UTF-8"));
            String lineTxt = null;

            StringBuffer sb =  new StringBuffer();
            while ((lineTxt = br.readLine()) != null) {
                sb.append(lineTxt);
                sb.append(" ");
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }

        return null;
    }
}
