package www.mutou.com.model;

import android.content.Intent;

import java.io.Serializable;

/**
 * Created by 在旭 on 2018/2/10.
 */

public class Mp3Info implements Serializable{

    private Integer id;
    private String fileName;
    private String title;
    private int duration;
    private String singer;
    private String album;
    private String year;
    private String type;
    private String size;
    private String fileUrl;
    private String who;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Mp3Info() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Mp3Info(int id, String fileName, String title, int duration, String singer,
                   String album, String year, String type, String size, String fileUrl
                    ,String who) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.year = year;
        this.type = type;
        this.size = size;
        this.fileUrl = fileUrl;
        this.who = who;
    }

    @Override
    public String toString() {
        return "Song [id="+id+", fileName=" + fileName + ", title=" + title
                + ", duration=" + duration + ", singer=" + singer + ", album="
                + album + ", year=" + year + ", type=" + type + ", size="
                + size + ", fileUrl=" + fileUrl + "]";
    }

}