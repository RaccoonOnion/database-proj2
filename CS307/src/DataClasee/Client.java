package DataClasee;

public class Client {

    public static void main(String[] args) {
        try {
            DataManipulation dm = new DataFactory().createDataManipulation(args[0]);
            dm.openDatasource();
            System.out.println(dm.FullInformationOfMoviesRuntime(65, 75));
            System.out.println(dm.findMovieById(10));
            dm.closeDatasource();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}

