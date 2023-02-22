package com.merative.publisher.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Article {

    public Article(long id, Author author, String title, String synopsis, String fulltext, String status) {
        this.identifier = id;
        this.author = author;
        this.title = title;
        this.synopsis = synopsis;
        this.fulltext = fulltext;
        this.status = ArticleStatus.valueOf(status);
    }

    private long identifier;
    private Author author;
    private String title;
    private String synopsis;
    private String fulltext;
    private ArticleStatus status;
}
