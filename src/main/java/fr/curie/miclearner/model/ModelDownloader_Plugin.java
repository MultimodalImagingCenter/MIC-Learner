package fr.curie.miclearner.model;

import ij.plugin.PlugIn;
import ij.IJ;

public class ModelDownloader_Plugin implements PlugIn {

    @Override
    public void run(String arg) {
        try {

            ModelRegistry registry = new ModelRegistry();

            String modelListUrl = "https://zenodo.org/records/20138094/files/models.json?download=1";
            registry.loadDefinitions(modelListUrl);
            if (registry.getAvailableModels().isEmpty()) {
                IJ.error("The models list could not be downloaded");
                return;
            }

            String IJDir = IJ.getDirectory("imagej");
            FileSystemManager fs = new FileSystemManager(IJDir);
            ModelDownloader downloader = new ModelDownloader();


            ModelManager manager = new ModelManager(registry, fs, downloader);


            DownloadDialog dialog = new DownloadDialog(manager);
            dialog.show();

        } catch (Exception e) {
            IJ.error("Test Plugin Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
