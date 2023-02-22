package com.merative.publisher.hibernate.entities;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.*;

@Entity
@Table(name = "articles")
public class ArticleEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public long author_id;
    public String title;
    public String synopsis;
    public String fulltext;
    public String status;
}
