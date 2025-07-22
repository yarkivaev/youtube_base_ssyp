package ru.ssyp.youtube;

import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Quality;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.*;
import java.io.InputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientYoutube implements Youtube {


    private final Socket clientSocket;

    private final Videos videos;

    public ClientYoutube(Socket clientSocket, Videos videos) throws IOException {
        this.clientSocket = clientSocket;
        this.videos = videos;
    }

    @Override
    public Video videoInfo(int videoId) {

        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x00});
            clientSocketStream.write(IntCodec.intToByte(videoId));

            int id;
            String title;
            String description;
            int segmentAmount;
            short segmentLength;
            int priority;
            String author;

            byte[] intBuffer = new byte[4];
            byte[] shortBuffer = new byte[1];

            InputStream input = clientSocket.getInputStream();
            input.read(intBuffer);
            segmentAmount = IntCodec.byteToInt(intBuffer);
            input.read(shortBuffer);
            segmentLength = IntCodec.byteToInt_1(shortBuffer);
            title = StringCodec.streamToString(input);
            description = StringCodec.streamToString(input);
            author = StringCodec.streamToString(input);
            input.read(intBuffer);
            priority = IntCodec.byteToInt(intBuffer);
            input.read(intBuffer);
            id = IntCodec.byteToInt(intBuffer);


            return new Video(
                    videoId,
                    new VideoMetadata(
                            title,
                            description
                    ),
                    segmentAmount,
                    segmentLength,
                    Quality.fromPriority(priority),
                    author
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video[] videos() {
        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x02});

            int id;
            String title;
            String description;
            int segmentAmount;
            short segmentLength;
            int priority;
            String author;
            short videoCount;
            List<Video> videosList = new ArrayList<>();

            byte[] intBuffer = new byte[4];
            byte[] shortBuffer = new byte[1];
            InputStream input = clientSocket.getInputStream();
            input.read(shortBuffer);
            videoCount = IntCodec.byteToInt_1(shortBuffer);

            for (int i = 0; i<videoCount; i++) {

                input.read(intBuffer);
                segmentAmount = IntCodec.byteToInt(intBuffer);
                input.read(shortBuffer);
                segmentLength = IntCodec.byteToInt_1(shortBuffer);
                title = StringCodec.streamToString(input);
                description = StringCodec.streamToString(input);
                author = StringCodec.streamToString(input);
                input.read(intBuffer);
                priority = IntCodec.byteToInt(intBuffer);
                input.read(intBuffer);
                id = IntCodec.byteToInt(intBuffer);
                input.read(shortBuffer);
                videoCount = IntCodec.byteToInt_1(shortBuffer);
                videosList.add(new Video(
                        id,
                        new VideoMetadata(
                                title,
                                description
                        ),
                        segmentAmount,
                        segmentLength,
                        Quality.fromPriority(priority),
                        author
                ));
            }
            return videosList.toArray(new Video[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            OutputStream clientSocketStream = clientSocket.getOutputStream();
//            clientSocketStream.flush();
//            InputStream clientSocketInStream = clientSocket.getInputStream();
//            System.out.println(clientSocketInStream);
//        } catch (IOException e) {
//            System.out.println("Капец, у тебя ошибка");
//        }
//        return new clientSocketInStream[0];
    }

    @Override
    public void upload(Session user, VideoMetadata str, InputStream stream) throws IOException, InterruptedException {
        try {
            String title = str.title;
            String description = str.description;
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            byte[] part = stream.readNBytes(1024 ^ 2);
            while (part.toString().isEmpty()) {
                dOut.writeUTF(user + title + description + new String(part) + part.length);
                dOut.flush();
                part = stream.readNBytes(1024 ^ 2);
            }
        } catch (java.io.IOException e) {
            String i = "да как так то :(";
            System.out.println(i);
        }
    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x02});
            clientSocketStream.write(new byte[videoId]);
            clientSocketStream.write(new byte[startSegment]);
            clientSocketStream.write(new byte[resolution]);

            InputStream clientSocketInStream = clientSocket.getInputStream();
            return clientSocketInStream;
        } catch (IOException e){
            System.out.println("OKAK");
        }
        return null;
    }
//
//    @Override
//    public InputStream load(int videoId, int startSegment, int resolution) {
////        try {
////            String title = str.title;
////            String description = str.discreaption;
////            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
////            byte[] part = stream.readNBytes(1024^2);
////            while (part.toString().isEmpty()) {
////                dOut.writeUTF(user + title + description + new String(part) + part.length);
////                dOut.flush();
////                part = stream.readNBytes(1024^2);
////            }
////        } catch (java.io.IOException e) {
////            String i = "да как так то :(";
////            System.out.println(i);
//        return null;
////        }
//    }
////    public void main(String[] args) {
////        try {
////            while(true) {
////                String a = System.console().readLine();
////                byte[] bcnt = a.getBytes();
////                if (bcnt[0] == (byte) 0x00) ;
////                {
////                    new ClientYoutube(new Socket("localhost", 8080)).getVideoInfo(a);
////                }
////                if (bcnt[0] == (byte) 0x01 || bcnt[0] == (byte) 0x02 || bcnt[0] == (byte) 0x03 || bcnt[0] == (byte) 0x04);
////                {
////                    new ClientYoutube(new Socket("localhost", 8080)).videoList(a);
////                }
////                if (bcnt[0] == (byte) 0x05) ;
////                {
////                    new ClientYoutube(new Socket("localhost", 8080)).uploadVideo(a);
////                }
////            }
////        } catch (IOException e) {
////            System.out.println("Бывает");
////        }
//        //public void uploadVideo(String a) {
//        //    try {
//        //        String[] x = a.split(" ");
//        //        User user = new User(x[0]);
//        //        UploadSignature str = new UploadSignature(x[1], x[2], x[3]);
//        //        String title = str.title;
//        //        String description = str.discreaption;
//        //        String name = str.name;
//        //        new ClientYoutube(new Socket("localhost", 8080)).upload(user, new UploadSignature(title, description, name), new ByteArrayInputStream(x[5].getBytes()));
//        //        InputStream clientSocketInStream = clientSocket.getInputStream();
//        //        System.out.println(clientSocketInStream);
//        //        while (clientSocketInStream.readAllBytes()[0] != 0x01 &&
//        //                clientSocketInStream.readAllBytes()[0] != 0x02) {
//        //            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocketInStream));
//        //            reader.readLine();
//        //            String b = reader.toString();
//        //            System.out.println(b.getBytes());
//        //        }
//        //    } catch (IOException e) {
//        //        System.out.println("Капец, у тебя ошибка");
//        //    }
//        //}
//

}
