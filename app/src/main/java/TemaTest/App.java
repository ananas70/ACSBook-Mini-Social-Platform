/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package TemaTest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class App {


    private static void deleteFileContents(String filename) {
        try {
            BufferedWriter fileOut = new BufferedWriter(new FileWriter(filename));
            fileOut.write("");
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processCommandLineArgs(java.lang.String[] strings) {
    //java tema1 "–create-user"     "-u ‘username’"     "-p ‘password’"
    // -    -           0               1                      2
    String command = strings[0];
    switch (command) {
        case "-create-user":
            Utilizator.extractUserCredentials(Arrays.copyOfRange(strings,1,strings.length));
            break;
        case "-create-post":
            Postare.extractPostCredentials(Arrays.copyOfRange(strings,1,strings.length));
            break;
        case "–delete-post-by-id":

            break;
        case "–follow-user-by-username":

            break;
        case "–unfollow-user-by-username":

            break;
        case "–like-post":

            break;
        case "–unlike-post":

            break;
        case "–like-comment":

            break;
        case "–get-followings-posts":

            break;
        case "–get-user-posts":

            break;
        case "–get-post-details":

            break;
        case "–comment-post":

            break;
        case "–delete-comment-by-id":

            break;
        case "–get-following":

            break;
        case "–get-followers":

            break;
        case "–get-most-liked-posts":

            break;
        case "–get-most-commented-posts":

            break;
        case "–get-most-followed-users":

            break;
        case "–get-most-liked-users":

            break;
        case "-cleanup-all":
        deleteFileContents("Users.txt");
            break;
        default:
            System.out.println("Comanda necunoscuta eeh");
    }




}
    public App() {/* compiled code */}

    public static void main(java.lang.String[] strings) {
    processCommandLineArgs(strings);

    }
}