package com.microsoft.graphdataconnect.selenium.api.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class JsonStructure<T> {
    private static ObjectMapper mapper = new ObjectMapper();

    public JsonNode getJsonRoot(File file) throws IOException {
        JsonNode rootNode = mapper.readTree(file);
        return rootNode;
    }

    public ObjectNode editJsonStructure(File file, String field, T value) {
        JsonNode rootNode = null;
        try {
            rootNode = getJsonRoot(file);
            ((ObjectNode) rootNode).set(field, (JsonNode) value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (ObjectNode) rootNode;
    }

    public JsonNode fromStringToJsonNode(String string) throws IOException {

        JsonNode rootNode = mapper.readTree(string);
        return rootNode;
    }

}