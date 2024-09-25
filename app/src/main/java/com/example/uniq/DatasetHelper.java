package com.example.uniq;

public class DatasetHelper
{
    String Document, Content;

    public String getDocument() {
        return Document;
    }

    public String getContent() {
        return Content;
    }

    public DatasetHelper(String document, String content) {
        Document = document;
        Content = content;
    }
}
