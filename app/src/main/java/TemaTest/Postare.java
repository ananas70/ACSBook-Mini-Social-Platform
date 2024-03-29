package TemaTest;

import java.io.*;
import java.util.*;

public class Postare implements Likeable {
    private Utilizator user;
    private String text;
    private int likes, id, commentsCounter;
    private Date timestamp;
    private static int idCounter = 0;
    static ArrayList<Postare> PostsArray = new ArrayList<>();
    String string;
    public Postare() {}

    public Postare(Utilizator user, String text) {
        this.user = user;
        this.text = text;
        this.likes = 0;
        this.timestamp = new Date();
        this.commentsCounter = 0;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public int getCommentsCounter() {
        return commentsCounter;
    }
    public void incrementCommentsCounter() {
        this.commentsCounter++;
    }
    public static void createSystemPost(java.lang.String[] args) {
        //"-u 'test'", "-p 'test'", "-text 'Astazi ma simt bine'"
        //1. Paramentrii -u sau -p lipsa
        if (args.length == 0 || args.length == 1) {
            System.out.println("{ 'status' : 'error', 'message' : 'You need to be authenticated'}");
            return;
        }

        String extractedUsername = args[0].substring(4, args[0].length() - 1);
        String extractedParola = args[1].substring(4, args[1].length() - 1);
        String extractedText;
        if (args.length == 3)
            extractedText = args[2].substring(7, args[2].length() - 1);
        else
            extractedText = "";
        Utilizator newUser = Utilizator.createUser(extractedUsername, extractedParola);

        //2. Username nu există, sau username și parola sunt greșite

        if (!Utilizator.verifyUserByCredentialsFILE(newUser, "Users.txt")) {
            System.out.println("{ 'status' : 'error', 'message' : 'Login failed'}");
            return;
        }
        //3. Postarea nu include nici un text
        if (args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No text provided'}");
            return;
        }

        //4.Postarea are peste 300 de caractere
        if (extractedText.length() > 300) {
            System.out.println("{ 'status' : 'error', 'message' : 'Post text length exceeded'}");
            return;
        }
        //5.Totul a mers bine
        Postare post = new Postare(newUser, extractedText);
        PostsArray.add(post);
        post.writePostToFile(post, "Posts.txt");
        System.out.println("{ 'status' : 'ok', 'message' : 'Post added successfully'}");
    }
    public static void deletePostById(java.lang.String[] args) {
        //"-u 'test'", "-p 'test'", "-id '1'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        // Id not provided
        if(args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No identifier was provided'}");
            return;
        }
        // Id not found
        int givenId = Integer.parseInt(args[2].substring(5, args[2].length() - 1));
        String foundPostLine;
        Postare foundPost = new Postare().getPostById(givenId);
        if (foundPost == null|| !(foundPost.PermissionToDelete(foundPost, newUser.getUsername()))) {
            System.out.println("{ 'status' : 'error', 'message' : 'The identifier was not valid'}");
            return;
        }
        foundPostLine = (foundPost.getPostLineById(givenId, "Posts.txt"));
        // Succes
        FileUtils.deleteLineFromFile(foundPostLine, "Posts.txt");
        String[] parts = foundPostLine.split(","); //textul propriu-zis
        Postare postare = new Postare(newUser,parts[2].substring(5));
        postare.deletePostFromArrayList(postare);
        System.out.println("{ 'status' : 'ok', 'message' : 'Post deleted successfully'}");
    }
    public boolean PermissionToDelete(Postare postare, String username) {
        //Verifica daca username-urile coincid
        return postare.getUsername().equals(username);
    }
    private void deletePostFromArrayList(Postare postare) {
        for(Postare aux : PostsArray)
            if(aux.getUsername().equals(postare.getUsername()) && aux.getText().equals(postare.getText())) {
                PostsArray.remove(aux);
                return;
            }
  }
    private void writePostToFile(Postare postare, String file) {
        //empty file - reset id
        idCounter++;
       if(FileUtils.isEmptyFile(file))
           idCounter = 1;
       postare.id = idCounter;
       try {
            BufferedWriter fileOut = new BufferedWriter(new FileWriter(file, true));
            fileOut.write("USER:" + postare.getUsername() + ",ID:" + idCounter + ",POST:" + postare.getText() + '\n');
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getPostLineById(int givenId, String file) {
        //Returneaza linia din fisier unde a fost gasita postarea cu id-ul specificat
        String emptyString = "";
        //empty file
        if(FileUtils.isEmptyFile(file))
            return emptyString;
        try {
            //"USER:username,ID:id,POST:postare
            BufferedReader fileIn = new BufferedReader(new FileReader("Posts.txt"));
            String line;
            while ((line = fileIn.readLine()) != null && givenId > 0) {
               String[] parts = line.split(",");
                if (Integer.parseInt(parts[1].substring(3)) == givenId)
                    return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emptyString;
    }
    public Postare getPostById(int givenId) {
        if(PostsArray == null){
            System.out.println("Eroare la accesarea PostsArray");
            return null;
        }
        for(Postare post : PostsArray)
            if(post.getId() == givenId)
                return post;
        return null;
    }
    @Override
    public void like(String[] args) {
        //"-u 'test'", "-p 'test'", "-post-id '1'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        // Post Id not provided
        if (args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to like was provided'}");
            return;
        }
        // Post Id not found
        String extractedId = args[2].substring(10, args[2].length() - 1);
        int givenId = Integer.parseInt(extractedId);
        String emptyString = "", foundPostLine;
        foundPostLine = (getPostLineById(givenId, "Posts.txt"));
        if (foundPostLine.equals(emptyString)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }
        //Postarea de apreciat nu este corectă (sau această postare este deja apreciată, sau este a utilizatorului curent)
        if(verifyUserLikesHisPost(newUser.getUsername(), givenId)){
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }
        if (verifyAlreadyLiked(newUser.getUsername(), givenId, "PostLikes.txt")) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to like was not valid'}");
            return;
        }
        //Totul a mers bine
        Postare foundPost = getPostById(givenId);
        if(foundPost == null) {
            System.out.println("Eroare la cautarea postarii in baza de date");
            System.exit(1);
        }
        foundPost.likes++;

        incrementUserLikes(foundPost);
        writePostLikeToFile(newUser.getUsername(), givenId);
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");

    }
    private void incrementUserLikes(Postare foundPost) {
        for(Utilizator utilizator : Utilizator.UsersArray)
            if(utilizator.getUsername().equals(foundPost.user.getUsername()) && utilizator.getParola().equals(foundPost.user.getParola()))
                utilizator.incrementLikes();
    }
    private void decrementUserLikes(Postare foundPost) {
        for(Utilizator utilizator : Utilizator.UsersArray)
            if(utilizator.getUsername().equals(foundPost.user.getUsername()) && utilizator.getParola().equals(foundPost.user.getParola()))
                utilizator.decrementLikes();
    }
    private boolean verifyUserLikesHisPost(String userLikes, int givenId) {
        if(FileUtils.isEmptyFile("Posts.txt"))
            return false;
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader("Posts.txt"));
            String line;
            while ((line = fileIn.readLine()) != null) {
                //"USER:user,ID:id,POST:post"
                String[] parts = line.split(",");
                String userLiked = parts[0].substring(5);
                String likedIdString = parts[1].substring(3);
                int likedId = Integer.parseInt(likedIdString);
                if (likedId == givenId && userLikes.equals(userLiked))
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void writePostLikeToFile(String userLikes, int givenId) {
        try {
            BufferedWriter fileOut = new BufferedWriter(new FileWriter("PostLikes.txt", true));
            fileOut.write(userLikes + "LIKES" + givenId + "\n");
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void unlike(String[] args) {
        //"-u 'test'", "-p 'test'", "-post-id '1'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        // Post Id not provided
        if (args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier to unlike was provided'}");
            return;
        }
        // Post Id not found
        String extractedId = args[2].substring(10, args[2].length() - 1);
        int givenId = Integer.parseInt(extractedId);
        String emptyString = "", foundPostLine;
        foundPostLine = (getPostLineById(givenId, "Posts.txt"));
        if (foundPostLine.equals(emptyString)) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }
        //Postarea pentru unlike nu este corecta (sau această postare este deja unliked)
        if (!verifyAlreadyLiked(newUser.getUsername(), givenId, "PostLikes.txt")) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier to unlike was not valid'}");
            return;
        }
        //Totul a mers bine
        Postare foundPost = getPostById(givenId);
        if(foundPost == null) {
            System.out.println("Eroare la cautarea postarii in baza de date");
            System.exit(1);
        }
        foundPost.likes--;
        decrementUserLikes(foundPost);
        unlikeFromFile(newUser.getUsername(), givenId,"PostLikes.txt");
        System.out.println("{ 'status' : 'ok', 'message' : 'Operation executed successfully'}");
    }
    public static void getUserPosts(java.lang.String[] args){
        //"-u 'test'", "-p 'test'", "-username 'test2'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        // Username-ul pentru listare postări nu a fost găsit
        if(args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No username to list posts was provided'}");
            return;
        }
        // Username-ul pentru listare postări nu este corect (sau acest username este deja unfollowed)
        String extractedUser = args[2].substring(11, args[2].length()-1);
        Utilizator followedUser = new Utilizator().getUserByUsernameARRAY(extractedUser);
        if(followedUser == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list posts was not valid'}");
            return;
        }
        if(!newUser.verifyAlreadyFollowed(newUser.getUsername(), followedUser.getUsername(), "Followers.txt")){
            System.out.println("{ 'status' : 'error', 'message' : 'The username to list posts was not valid'}");
            return;
        }
        // Totul a mers bine
        //Adaugam toate postarile intr-un alt ArrayList
        ArrayList<Postare> userPosts = new ArrayList<>();
        for(Postare post : PostsArray) {
            if(post.getUsername().equals(followedUser.getUsername()))
                userPosts.add(post);
        }
        //sortare
        userPosts.sort(Collections.reverseOrder(Comparator.comparing(Postare::getTimestamp)));
        //afisare
        System.out.print("{'status' : 'ok', 'message' :" + " [");
        FileUtils.printPostDetails(userPosts);
        System.out.print("]}");
    }
    public static void getPostDetails(java.lang.String[] args){
        //"-u 'test'", "-p 'test'", "-post-id '1'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        // Identificatorul pentru postare nu a fost găsit
        if(args.length < 3) {
            System.out.println("{ 'status' : 'error', 'message' : 'No post identifier was provided'}");
            return;
        }
        // Identificatorul pentru postare nu este corect (sau acest username este deja unfollowed)
        int givenId = Integer.parseInt(args[2].substring(10, args[2].length()-1));

        Postare post = new Postare().getPostById(givenId);
        if(post == null) {
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier was not valid'}");
            return;
        }
        if(!(newUser.getUsername().equals(post.getUsername())))
            if(!newUser.verifyAlreadyFollowed(newUser.getUsername(), post.getUsername(), "Followers.txt")){
            System.out.println("{ 'status' : 'error', 'message' : 'The post identifier was not valid'}");
            return;
        }
        // Totul a mers bine
        System.out.print("{'status' : 'ok', 'message' : [{'post_text' : '"+post.text+"', 'post_date' :'" + FileUtils.dateFormat.format(post.timestamp) + "', 'username' : '"+post.user+"', 'number_of_likes' :" +" '"+post.likes+"', ");
        //ne trebuie un ArrayList cu comentariile
        ArrayList <Comentariu> postComments = new Comentariu().getPostComments(post);
        //sortare
        postComments.sort(Collections.reverseOrder(Comparator.comparing(Comentariu::getTimestamp)));
        //afisare
        FileUtils.printPostComments(postComments);
        System.out.print("] }] }");
    }
    public static void getMostLikedPosts(java.lang.String[] args) {
        //"-u 'test'", "-p 'test'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        //3. Totul a mers bine
        ArrayList <Postare> mostLikedPosts = PostsArray;
        mostLikedPosts.sort(Comparator.comparingInt(Postare::getLikes).reversed());
        System.out.print("{ 'status' : 'ok', 'message' : [");
        FileUtils.printMostLikedPosts(mostLikedPosts);
        System.out.println(" ]}");
    }
    public static void getMostCommentedPosts(java.lang.String[] args) {
        //"-u 'test'", "-p 'test'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        //3. Totul a mers bine
        ArrayList <Postare> mostCommentedPosts = PostsArray;
        mostCommentedPosts.sort(Comparator.comparingInt(Postare::getCommentsCounter).reversed());
        System.out.print("{ 'status' : 'ok', 'message' : [");
        FileUtils.printMostCommentedPosts(mostCommentedPosts);
        System.out.println("]}");
    }
    public static void getFollowingsPosts(java.lang.String[] args){
        //"-u 'test'", "-p 'test'"
        if(!Utilizator.verifyAuthenticated(args))
            return;
        Utilizator newUser = new Utilizator().createUserFromInput(args);
        if(newUser == null)
            return;
        //3. Totul a mers bine
        //Adaugam toate postarile intr-un alt ArrayList
        ArrayList<Postare> followingsPosts = new ArrayList<>();
        for(Postare post : PostsArray) {
            if(newUser.verifyAlreadyFollowed(newUser.getUsername(), post.getUsername(), "Followers.txt"))
                followingsPosts.add(post);
        }
        //sortare
        followingsPosts.sort(Collections.reverseOrder(Comparator.comparing(Postare::getTimestamp)));

        //afisare
        System.out.print("{'status' : 'ok', 'message' :" + " [");
        FileUtils.printFollowingsPosts(followingsPosts);
        System.out.print("]}");
    }
}