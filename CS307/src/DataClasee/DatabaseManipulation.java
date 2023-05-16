package DataClasee;

import java.sql.*;

public class DatabaseManipulation implements DataManipulation {
    private Connection con = null;//数据访问的链接，在访问数据库前一定要建立连接
    private ResultSet resultSet;//结果集，搜索语句的返回结果会放到这个resultSet中

    private String host = "localhost";
    private String dbname = "cs307";
    private String user = "postgres";
    private String pwd = "password970272";
    private String port = "5432";


    @Override
    public void openDatasource() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }

        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            con = DriverManager.getConnection(url, user, pwd);

        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void closeDatasource() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int addOneMovie(String str) {
        int result = 0;
        String sql = "insert into movies (title, country,year_released,runtime) " +
                "values (?,?,?,?)";
        String[] movieInfo = str.split(";");
        try {
            //statement 将sql语句发送到dbms后，编译与执行
            //prepareStatement： 预编译sql语句，执行的时候只负责传参，将？的位置对应成参数执行
            //1、防止sql注入的问题 2、防止传参有问题出现报错 3、对于批量操作可以加快速度
            //
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, movieInfo[0]);
            preparedStatement.setString(2, movieInfo[1]);
            preparedStatement.setInt(3, Integer.parseInt(movieInfo[2]));
            preparedStatement.setInt(4, Integer.parseInt(movieInfo[3]));
            System.out.println(preparedStatement.toString());

            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String allContinentNames() {
        StringBuilder sb = new StringBuilder();
        String sql = "select continent from countries group by continent";
        try {
            Statement statement = con.createStatement();//创建一个数据库链接
            resultSet = statement.executeQuery(sql);//执行sql语句并有返回值
            //指针，指向返回结果集的头。resultSet自身不存数据
            while (resultSet.next()) {
                sb.append(resultSet.getString("continent")).append("\n");
            }
            //返回continent列的值
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public String continentsWithCountryCount() {
        StringBuilder sb = new StringBuilder();
        String sql = "select continent, count(*) countryNumber from countries group by continent;";
        try {
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                sb.append(resultSet.getString("continent")).append("\t");
                sb.append(resultSet.getString("countryNumber"));
                sb.append(System.lineSeparator());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public String FullInformationOfMoviesRuntime(int min, int max) {
        StringBuilder sb = new StringBuilder();
        String sql = "select m.title,c.country_name country,c.continent ,m.runtime " +
                "from movies m " +
                "join countries c on m.country=c.country_code " +
                "where m.runtime between ? and ? order by runtime;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, min);
            preparedStatement.setInt(2, max);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sb.append(resultSet.getString("runtime")).append("\t");
                sb.append(String.format("%-18s", resultSet.getString("country")));
                sb.append(resultSet.getString("continent")).append("\t");
                sb.append(resultSet.getString("title")).append("\t");
                sb.append(System.lineSeparator());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public String findMovieById(int id) {
        StringBuilder sb = new StringBuilder();
        String sql = "select * from movies where movieid = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                sb.append(resultSet.getString("runtime")).append("\t");
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    @Override
    public String findLargestPostID() {
        {
            StringBuilder sb = new StringBuilder();
            String sql = "select post_id from post order by post_id desc limit 1;";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()){
                    sb.append(resultSet.getString("runtime")).append("\t");
                }

            }catch (SQLException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }

    }

}
