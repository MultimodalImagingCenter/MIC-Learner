package fr.curie.miclearner.panel;

import fr.curie.miclearner.MainApplication_Frame;
import fr.curie.miclearner.structure.ContentLoader;
import fr.curie.miclearner.structure.StructureManager;
import fr.curie.miclearner.structure.UseCaseConfig;
import ij.IJ;
import ij.ImagePlus;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;


public class RunPanel extends JPanel{
    private JPanel runButtonsPanel;
    private JPanel descriptionPanel;
    private JButton exampleImageButton;
    private JButton runButton;
    private JLabel titleLabel;
    private JButton userImageButton;
    private JEditorPane descriptionArea;
    private JRadioButton defaultParamRButton;
    private JRadioButton userParamRButton;
    private JScrollPane scrollPane;

    private final MainApplication_Frame mainFrame;
    protected StructureManager uiStructure;
    private String modelPath;
    private UseCaseConfig currentUseCase;
    private boolean defaultParameters;


    public RunPanel(MainApplication_Frame mainFrame) {

        this.mainFrame = mainFrame;
        try {
            this.uiStructure = mainFrame.getUIStructure();
        } catch (Exception e) {
            System.err.println(getClass().getSimpleName() + ": Error loading resource bundle");
            this.uiStructure = null;
        }

        initUI();

        // Setup listeners
        setupListeners();
    }

    private void initUI() {
        this.setLayout(new MigLayout("insets 10, fill, wrap 1"));

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        this.add(titleLabel, "align center, gapbottom 10");

        descriptionArea = new JEditorPane();
        descriptionArea.setContentType("text/html");
        descriptionArea.setEditable(false);
        scrollPane = new JScrollPane(descriptionArea);
        this.add(scrollPane, "grow, pushy");

        // button panel
        JPanel runButtonsPanel = new JPanel(new MigLayout("insets 10, fillx", "[center, grow][center, grow]"));

        defaultParamRButton = new JRadioButton(uiStructure.getString("runPage.useDefaultParam.rButton"));
        userParamRButton = new JRadioButton(uiStructure.getString("runPage.useUserParam.rButton"));
        defaultParamRButton.setSelected(true);

        exampleImageButton = new JButton(uiStructure.getString("processPage.openExampleImage.button"));
        userImageButton = new JButton(uiStructure.getString("processPage.openUserImage.button"));
        runButton = new JButton(uiStructure.getString("processPage.run.button"));

        // left column
        runButtonsPanel.add(exampleImageButton, "cell 0 0, w 200!");
        runButtonsPanel.add(new JLabel(uiStructure.getString("or.text")), "cell 0 1, align center");
        runButtonsPanel.add(userImageButton, "cell 0 2, w 200!");

        // right column
        runButtonsPanel.add(defaultParamRButton, "cell 2 0");
        runButtonsPanel.add(userParamRButton, "cell 2 1");
        runButtonsPanel.add(runButton, "cell 2 2, w 150!");

        this.add(runButtonsPanel, "growx, align center");
    }

    /**
     * Configures the entire panel for a specific example model
     */
    public void configurePanel(String exampleId) {
        // load the configuration for this use case.
        this.currentUseCase = uiStructure.loadUseCase(mainFrame.getModelDirectoryPath(), exampleId);

        // handle the case where the configuration might fail to load.
        if (this.currentUseCase == null) {
            titleLabel.setText("Error");
            descriptionArea.setText("<html>Could not load the configuration for " + exampleId + ".<br>Check logs for details.</html>");
            runButton.setEnabled(false);
            exampleImageButton.setEnabled(false);
            userImageButton.setEnabled(false);
            return;
        }

        // populate the UI elements from the loaded UseCaseConfig object.
        titleLabel.setText(currentUseCase.getDescriptionTitle());

        String markdownFilePath = uiStructure.getExampleDescriptionPath(exampleId);
        descriptionArea.setText(ContentLoader.loadAndParseMarkdown(markdownFilePath));
        descriptionArea.setCaretPosition(0); // Scroll to top

        // reset UI state with default param
        defaultParameters = true;
        defaultParamRButton.setSelected(true);
        userParamRButton.setSelected(false);
        userImageButton.setEnabled(true);

        // only enable the run button if a valid model path is provided in the config
        runButton.setEnabled(true);
        // get model path
        this.modelPath = currentUseCase.getModelDirectoryPath();
        System.out.println("model path = " + modelPath);
        // check if path valid
        File modelDir = new File(modelPath);
        if (!modelDir.exists() || !modelDir.isDirectory()) {
            IJ.error("Model Not Found", "The required model directory does not exist at:\n" + modelPath);
            this.modelPath = null;
            runButton.setEnabled(false);
        }


        // only enable the example image button if a valid path is provided in the config
        // and path exist
        if (currentUseCase.getExampleImagePath() == null || currentUseCase.getExampleImagePath().isEmpty()) {
            exampleImageButton.setEnabled(false);
            IJ.error("Image Path Not Found", "No example image path was found for model " + exampleId );
        } else {
            File imageFile = new File(currentUseCase.getExampleImagePath());
            if (!imageFile.exists() ) {
                IJ.error("Image Not Found", "The required example image does not exist at:\n" + currentUseCase.getExampleImagePath());
                exampleImageButton.setEnabled(false);
            } else {
                exampleImageButton.setEnabled(true);
            }
        }
    }


    private void setupListeners() {
        exampleImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String exampleImagePath = currentUseCase.getExampleImagePath();
                IJ.open(exampleImagePath);
                // (no need to check for null, the button is disabled if the path is missing)
            }
        });

        userImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and start a new thread to handle the blocking operation
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // This code now runs on a background thread, NOT the EDT.
                        IJ.open();
                    }
                }).start();
            }
        });

        defaultParamRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultParameters=true;
                userParamRButton.setSelected(false);
            }
        });

        userParamRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                defaultParameters=false;
                defaultParamRButton.setSelected(false);
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the active image (necessary for some plugins)
                ImagePlus imp = IJ.getImage();
                String impTitle = (imp != null) ? imp.getTitle() : "";

                // Running the macro on a new thread to avoid freezing the GUI
                new Thread(() -> {
                    try {
                        // 1. Get the correct macro template
                        String macroTemplate = defaultParameters
                                ? currentUseCase.getDefaultMacro()
                                : currentUseCase.getOptionMacro();

                        if (macroTemplate != null && !macroTemplate.isEmpty()) {
                            // 2. Get the pre-resolved model path
                            String modelDir = modelPath;

                            // 3. Replace placeholders.
                            modelDir=modelDir.replace('\\','/');
                            String finalMacroScript = macroTemplate.replace("{MODEL_PATH}", modelDir)
                                    .replace("{IMP_TITLE}", impTitle);

                            // 4. Execute the script.
                            IJ.runMacro(finalMacroScript);
                        } else {
                            IJ.error("Configuration Error", "No macro definition found for this parameter choice.");
                        }

                    } catch (Exception ex) {
                        IJ.error("Macro Execution Failed", "Could not run the macro. Check usecase.properties and model configuration.\nError: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                }).start();
            }
        });

        descriptionArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        // Open the link in the default system browser
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException | NullPointerException ex) {
                        // Handle potential errors (e.g. malformed URLs)
                        System.err.println("Unable to open link: " + e.getDescription());
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}