package ru.job4j.html;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime localDateTime;

    public Post() {
    }

    public Post(int id, String title, String link, String description, LocalDateTime localDateTime) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.localDateTime = localDateTime;
    }



    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", created=" + localDateTime
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id ==
                post.id
                && Objects.equals(title, post.title)
                && Objects.equals(link, post.link)
                && Objects.equals(localDateTime, post.localDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, localDateTime);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
