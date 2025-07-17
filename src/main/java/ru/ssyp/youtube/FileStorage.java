package ru.ssyp.youtube;

import java.io.*;
import java.util.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
    Abandon all hope ye, who import this package
 */

public class FileStorage implements Storage {

    @Override
    public void upload(String name, InputStream stream) throws FileNotFoundException {
        int length = 0;
        try {
            length = stream.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (length > 4096){
            throw new UnsupportedOperationException("Too heavy file");
        }
        byte[] buffer = new byte[length];
        for(int i = 0; i < length; i++){
            int test = -1;
            try {
                test = stream.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(test == -1){
                throw new RuntimeException("Oh man, some sketchy business!");
            }
            buffer[i] = (byte) test;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Oh man, hashy thingy refuses to work");
        }
        byte[] result = md.digest(buffer);
        String signature = Base64.getEncoder().encodeToString(result);

        File newFile = new File("C://SsypYoutubeBaisicStorage//" + name + signature.substring(0, 5) + ".txt");
        try
        {
            boolean created = newFile.createNewFile();
            if(created){
                // System.out.println("File has been created");
            }
            else{
                throw new RuntimeException("Oh man, fily thingy wasn't created!");
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            // Write the bytes to the file
            fos.write(buffer);
            // System.out.println("Data successfully written to the file.");
        }
        catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        return;
    }

    @Override
    public InputStream download(String name) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'download'");
        // First, let's locate all files in our glorious directory:
        File startPath = new File("C://SsypYoutubeBaisicStorage");
        File[] AllFiles = startPath.listFiles();
        File FoundFile = null;
        // I am a useless piece of meat:
        for (int i = 0; i < AllFiles.length; i++){
            String CurrentName = AllFiles[i].getName();
            CurrentName = CurrentName.substring(0, CurrentName.length() - 9);
            // System.out.println("Looking at : " + CurrentName);
            if (CurrentName.equals(name)){
                FoundFile = AllFiles[i];
                break;
            }
        }
        if (FoundFile != null){
            try {
                return new FileInputStream(FoundFile);
            } catch (FileNotFoundException e) {
                System.out.println("No luck today :(");
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        new FileStorage().upload("FileHeavyOkay8.txt", new ByteArrayInputStream( "Just a iVBORw0KGgoAAAANSUhE/+qZyC4mpkewgAAAABJjgh,,,,,/jghjghjghjRU5ErkJggg== file occupying the HDD. Im file 4 btw. And I am very heavy. Take a look: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec ante enim, cursus quis sapien quis, dictum lobortis quam. Sed dapibus magna quis ex maximus dapibus. Fusce ipsum nulla, vehicula ac tempus at, scelerisque eget orci. Aliquam accumsan porta consequat. Nunc vestibulum a leo ac ultricies. Duis pharetra maximus volutpat. Nulla ac interdum ex. In porta sem vitae nulla placerat pulvinar. Vestibulum euismod molestie nibh vitae condimentum. Etiam fermentum, augue quis accumsan tristique, mauris risus suscipit justo, at pellentesque ante nisl sed elit. Curabitur egestas lobortis augue, finibus placerat augue ullamcorper sed. Proin a turpis nunc. Curabitur pulvinar dolor gravida mi sagittis sagittis quis congue leo. Mauris aliquam tempor malesuada. Pellentesque vulputate pellentesque quam et iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC.".getBytes()));
        InputStream io = new FileStorage().download("mleow.mp3");
        String read = "Read: ";
        while(io.available() != 0){
            read += (char) io.read();
        }
        System.out.println(read);
    }
}
