package com.mohit.betterreadsdataloader.book;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Table("book_by_id")
public class Book {
    @Column("book_name")
    @CassandraType(type = Name.TEXT)
    private String name;

    
    @Id @PrimaryKeyColumn(name = "book_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @CassandraType(type = Name.TEXT)
    private String id;

    @Column("book_description")
    @CassandraType(type = Name.TEXT)
    private String description;

    @Column("publish_date")
    @CassandraType(type = Name.DATE)
    private LocalDate publishDate;

    @Column("author_names")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorNames;
    
    @Column("author_id")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> authorId;

    @Column("cover_id")
    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> coverId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public List<String> getAuthorNames() {
        return authorNames;
    }

    public void setAuthorNames(List<String> authorNames) {
        this.authorNames = authorNames;
    }

    public List<String> getAuthorIds() {
        return authorId;
    }

    public void setAuthorIds(List<String> authorId) {
        this.authorId = authorId;
    }

    public List<String> getCoverId() {
        return coverId;
    }

    public void setCoverId(List<String> coverId) {
        this.coverId = coverId;
    }

    
}
