import java.util.Scanner;

/**
 * Created by Radosław Kokoszka on 15.02.2019 13:26
 */


public class Main {
    private static final String STARTING_MESSAGE = "Podaj ścieżkę do pliku(TXT lub XML) który chcesz przetwarzać: ";
    private static final String ERROR_MESSAGE = "Niepoprawny plik!";
    private static Integer rowInserted = 0;
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println(STARTING_MESSAGE);
        String pathToFile = in.nextLine();
        String fileType = pathToFile.substring(pathToFile.length() - 3);

        switch (fileType) {
            case "txt":
                rowInserted = ProcessTxtFile.process(pathToFile);
                System.out.println("Dodano "+rowInserted+" rekordów");
                break;
            case "xml":
                 rowInserted = ProcessXmlFile.process(pathToFile);
                System.out.println("Dodano "+rowInserted+" rekordów");
                break;
            default:
                System.out.println(ERROR_MESSAGE);
        }

    }

}
