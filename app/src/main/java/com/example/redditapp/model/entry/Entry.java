package com.example.redditapp.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

@Root(name = "entry", strict = false)
public class Entry implements Serializable {

    @Element(name = "updated")
    private String updated;

    @Element(required=false, name = "author")
    private String author;

    @Element(name = "id")
    private String id;

    @Element(name = "content")
    private String content;

    @Element(name = "title")
    private String title;

    public Entry() {
    }

    public Entry(String updated, String author, String content, String title) {
        this.updated = updated;
        this.author = author;
        this.content = content;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "\n\nEntry{" +
                "updated='" + updated + '\'' +
                ", author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                '}' + "\n" +
                "----------------------------------------------------------------\n";
    }
}
