package com.hzc.common.environment;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: hzc
 * @Date: 2020/04/12  20:23
 * @Description:
 */
public class CommonEnvironment implements Environment {
    private Resource resource;

    private Map<String, String> properties = new HashMap<>(32);

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String getPropertyValue(String key) {
        return properties.get(key);
    }

    @Override
    public InputStream getResourceAsInputStream() {
        return resource.getResourceAsInputStream();
    }

    @Override
    public void loadProperties() throws IOException {
        InputStream input = getResourceAsInputStream();
        int available = input.available();
        byte[] inputBytes = new byte[available];
        for (int i = 0; i < inputBytes.length; ) {
            int read = input.read(inputBytes, i, inputBytes.length - i);
            i = i + read;
        }
        String propertyString = new String(inputBytes, StandardCharsets.UTF_8);
        String[] propertyLines = propertyString.split("\n");
        for (String propertyLine : propertyLines) {
            String[] kv = propertyLine.split("=");
            if (kv.length<2){
                throw new RuntimeException("this property: "+kv[0]+"must not empty");
            }
            properties.put(kv[0], kv[1]);
        }

    }
}
