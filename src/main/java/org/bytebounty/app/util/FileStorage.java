package org.bytebounty.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.RandomStringUtils;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.validators.FileValidator;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



public class FileStorage {


    public static String save(MultipartFile file) {
        return save(file, "thumbnails");
    }


    public static String save(MultipartFile file, String folder) {
        @SuppressWarnings("null")
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = FileValidator.sanitizeFileName(fileName);
        String relativeFileLocation = "";
        try {
                



                int lenght = 10;
                boolean useLetters = true;
                boolean useNumbers = true;
                String generatedString = RandomStringUtils.random(lenght, useLetters, useNumbers);
                String finalPhotoName = generatedString + fileName;
                String absoluteFileLocation = new File("static/img/"
                + folder + "/").getAbsolutePath() +  "/" + finalPhotoName;
                Path path = Paths.get(absoluteFileLocation);
                relativeFileLocation = "/img/" + folder + "/" + finalPhotoName;

                // added code start

                FileValidator.resolvePath("/home/", path.toString());

                // added code end


                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);


                
                
    
                    
            } catch (Exception e) {
                    // TODO: handle exception
            }
            
            return relativeFileLocation;


    }
    // /home/yoda/spring-boot-projects/ByteBounty/app_16_Account_Crud/
    public static String delete(String fileRelativePath) {
        String result = "";        
        try {
            Path path = Paths.get(
                "static" 
                + fileRelativePath
                );
            if (Files.deleteIfExists(path) ) {
                result = "The file " + path + " has been deleted!";
                System.out.println("*******************************************");
                System.out.println("*******************************************");
                System.out.println(result);
                System.out.println("*******************************************");
                System.out.println("*******************************************");

            } else {                
                result = "Unable to delete the file " + path;
                System.out.println("*******************************************");
                System.out.println("*******************************************");
                System.out.println(result);
                System.out.println("*******************************************");
                System.out.println("*******************************************");
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static List<String> imageTagExtractor(String textWithImages) {
        Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = pattern.matcher(textWithImages);
        List<String> imageUrlList = new ArrayList<>();
        while (matcher.find()) {
            //String imgTag = matcher.group(0);
            String imageUrl = matcher.group(1);
            imageUrlList.add(imageUrl);
        }
        return imageUrlList;
    }

    public static List<String> listFilesInFolder(String dir){
        List<String> files = new ArrayList<>();
        //List<String> folders = new ArrayList<>();
        File folder = new File("static/img/"
                + dir + "/");
        File[] listOFiles = folder.listFiles();
        if (listOFiles != null) {
            for (int i = 0; i < listOFiles.length; i++) {
                if (listOFiles[i].isFile()) {
                    files.add("/img/" + dir + "/" +listOFiles[i].getName());
                } else if (listOFiles[i].isDirectory()) {
                    //folders.add(listOFiles[i].getName());
                }
            }
        }
        return files;
    }

    // returns the name of the file being moved
    public static String move(String fileFrom) {
        File oldFile = new File("static" + fileFrom);
        String fileName = oldFile.getName();
        File newFile = new File("static/img/posts/" + fileName);

        System.out.println("old and new file:");
        System.out.println(oldFile);
        System.out.println(newFile);
        oldFile.renameTo(newFile);

        return fileName;
    }

    // empty tmp folder
    public static Post emptyTmp(Post post, List<String> newPostImagePaths) {
        // list images that are in tmp folder
        List<String> tmpImgs = FileStorage.listFilesInFolder("tmp");

        // images that are in tmp folder and not in post.content field are eliminated
        Iterator<String> itr =  tmpImgs.iterator();
        while (itr.hasNext()) {
            String tmpImg = itr.next();
            if (!newPostImagePaths.contains(tmpImg)){
                // eliminate file from strorage
                FileStorage.delete(tmpImg);
                // eliminate file from list
                itr.remove();
            }
        }
        
        // per each file in the tmp folder:
        for (String tmpImg : tmpImgs) {

            // move images from tmp to final destination            
            String imgName = FileStorage.move(tmpImg);

            // change the image path in content field to the final destination              
            post.setContent(
                post.getContent().replaceAll( "src=\"/img/tmp/" + imgName, "src=\"/img/posts/" + imgName)
            );
        }
        // delay rendering of the page after have moved tmp files to fial location
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        return post;
    }

    // emptyTmp method override with only one parameter
    public static Post emptyTmp(Post post) {
        // Get a list of image paths in new post.content
        List<String> newPostImagePaths = FileStorage.imageTagExtractor(post.getContent());
        return emptyTmp(post, newPostImagePaths); 

    }

    // clean statis/img/posts folder
    public static List<String> cleanPostsImg(List<String> oldPostImagePaths, Post post) {
        // Get a list of image paths in edited post.content
        List<String> newPostImagePaths = FileStorage.imageTagExtractor(post.getContent());    
        // images that are not in edited post.content are here eliminated from the server:
        for (String oldFile : oldPostImagePaths) {
            oldFile = oldFile.replace("[","").replace("]", "");
            if(!newPostImagePaths.contains(oldFile)) {
                // delete old file                  
                FileStorage.delete(oldFile);
            }
        }
        return newPostImagePaths;
    }

    public static void waitt(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
    public static void waitt() {
        waitt(1);
    }


}
