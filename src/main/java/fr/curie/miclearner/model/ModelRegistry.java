package fr.curie.miclearner.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ij.IJ;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelRegistry {

    private final Map<String, ModelDefinition> availableModels;
    private final ObjectMapper mapper; // The Jackson engine

    public ModelRegistry() {
        this.availableModels = new HashMap<>();
        this.mapper = new ObjectMapper();
    }

    public void loadDefinitions(String source) throws Exception {
        List<ModelDefinition> definitions;

        if (source.toLowerCase().startsWith("http")) {
            try (InputStream in = new URL(source).openStream()) {
                definitions = mapper.readValue(in, new TypeReference<List<ModelDefinition>>(){});
            } catch (IOException e) {
                IJ.log("Could not load definitions from: " + source + ":  " + e.getMessage());
                return;
            }
        } else {
            File localFile = new File(source);
            definitions = mapper.readValue(localFile, new TypeReference<List<ModelDefinition>>(){});
        }


        // Populate the registry
        for (ModelDefinition def : definitions) {
            availableModels.put(def.getId(), def);
        }
    }

    public Map<String, ModelDefinition> getAvailableModels() {
        return Collections.unmodifiableMap(availableModels);
    }

    public ModelDefinition getModel(String modelId) {
        return  availableModels.get(modelId);
    }
}
