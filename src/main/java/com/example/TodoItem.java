package com.example;

import org.apache.solr.client.solrj.beans.Field;

import java.time.LocalDateTime;

/**
 * Created by fadi on 5/13/15.
 */
public class TodoItem {

    @Field
    private String id;
    @Field
    private String description;
    @Field
    private String category;
    @Field
    private final LocalDateTime date = LocalDateTime.now();
    @Field
    private String location;



    public TodoItem(String id, String description, String category, String location) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.location = location;


    }

    public String getId(){
        return this.id;
    }

    public  String getDescription(){
        return this.description;
    }

    public String getCategory(){
        return this.category;
    }

//    @Field
//    public void setId(String id){
//        this.id = id;
//    }
//
//    @Field
//    public void setDescription(String description){
//        this.description = description;
//    }
//    @Field
//    public void setCategory(String category){
//        this.category = category;
//    }


    @Override
    public String toString() {
        return "Item's ID: " + this.id + "\nDescription: " + this.description + "\nCategory: " + this.category + "\nDate/Time: " + date;

    }
}




