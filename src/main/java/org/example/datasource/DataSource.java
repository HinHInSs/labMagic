package org.example.datasource;

public interface DataSource {
    String read() throws Exception;
    String getIdentifier();
}