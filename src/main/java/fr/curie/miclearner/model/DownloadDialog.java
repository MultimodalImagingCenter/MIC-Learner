package fr.curie.miclearner.model;

import ij.IJ;
import ij.gui.GenericDialog;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadDialog {

    private final ModelManager modelManager;

    public DownloadDialog(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public void show() throws IOException {
        Map<String, ModelDefinition> models = modelManager.getRegistry().getAvailableModels();
        IJ.log("\navailable models: " + models.keySet());
        String[] modelIds = models.keySet().toArray(new String[0]);

        // 1. Setup Dialog
        GenericDialog gd = new GenericDialog("Download Models");
        gd.addMessage("Select models to download:");

        for (int i = 0; i < modelIds.length; i++) {
            ModelDefinition def = models.get(modelIds[i]);

            boolean isComplete = modelManager.isModelComplete(modelIds[i]);

            String label = def.getDisplayName();
            if (isComplete) {
                label += " (Installed)";
            }

            gd.addCheckbox(label, false);
        }

        gd.showDialog();

        if (gd.wasCanceled()) return;

        // 2. Identify selection
        List<String> toDownload = new ArrayList<>();
        for (int i = 0; i < modelIds.length; i++) {
            if (gd.getNextBoolean()) {
                toDownload.add(modelIds[i]);
            }
        }

        // 3. Process Download in a separate thread
        if (!toDownload.isEmpty()) {
            executeDownloadTask(toDownload);
        } else {
            IJ.log("no models selected");
        }
    }

    private void executeDownloadTask(List<String> modelIds) {
        IJ.log("Downloading models...");
        new Thread(() -> {
            for (String id : modelIds) {
                IJ.log("   Downloading model: " + id);
                try {
                    modelManager.installModel(id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (modelManager.isModelComplete(id)) {
                    ij.IJ.log("   Model " + id + " installed successfully.");
                } else {
                    ij.IJ.log("   Model " + id + " is incomplete. Check logs for specific file errors.");
                }
            }
            ij.IJ.showMessage("Download Process Complete");
        }).start();
    }
}