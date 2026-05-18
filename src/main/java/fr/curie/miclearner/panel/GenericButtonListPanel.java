

package fr.curie.miclearner.panel;

import ij.IJ;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import fr.curie.miclearner.MainApplication_Frame;
import fr.curie.miclearner.structure.StructureManager;


public class GenericButtonListPanel extends JPanel {

    private JSplitPane splitPane;
    private JPanel buttonsDisplayPanel;
    private JLabel questionLabel;
    private JScrollPane scrollPane;
    private JPanel buttonsPanel;

    protected MainApplication_Frame mainFrame;
    protected StructureManager uiStructure;
    protected String pageTitle;
    protected String propertyKey;
    // e.g., "task" or "model" : list of all available tasks or models
    // or "cnn.task", "detection.model" : list of sub-tasks for cnn, of sub-models for detection...
    protected BiConsumer<String, String> buttonActionHandler;

    /**
     * Constructor for the generic button list panel.
     *
     * @param mainFrame                 Reference to the main application frame.
     * @param pageTitle                 The title to display at the top of this page.
     * @param propertyKey The prefix for button name keys (e.g., "task" for "task.classification.name").
     *
     */
    public GenericButtonListPanel(MainApplication_Frame mainFrame, String pageTitle, String propertyKey) {
        this.mainFrame = mainFrame;
        this.pageTitle = pageTitle;
        this.propertyKey = propertyKey;
        try {
            this.uiStructure = mainFrame.getUIStructure();
        } catch (Exception e) {
            System.err.println(getClass().getSimpleName() + ": Error loading resource bundle");
            this.uiStructure = null;
        }
        initUI();

    }

    private void initUI() {
        this.setLayout(new BorderLayout());

        // top Label setup
        questionLabel = new JLabel();
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // button list container
        buttonsPanel = new JPanel(new MigLayout("wrap 1, fillx, align center"));

        // scrollPane setup
        scrollPane = new JScrollPane(buttonsPanel);
        scrollPane.setBorder(null);

        // left Panel container
        buttonsDisplayPanel = new JPanel(new MigLayout("fill, flowy"));
        buttonsDisplayPanel.add(questionLabel, "w 0:100%:100%, shrink"); // Take as little space as possible
        buttonsDisplayPanel.add(scrollPane, "push, grow"); // Take remaining space

        // splitPane setup
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonsDisplayPanel, new JPanel());
        splitPane.setResizeWeight(0.15);

        this.add(splitPane, BorderLayout.CENTER);
    }

    public void initializePanel() {
        populateButtons();
    }

    protected void populateButtons() {
        buttonsPanel.removeAll();
        if (uiStructure == null) {
            buttonsPanel.add(new JLabel("Error: button list not loaded."), "align center");
            return;
        }

        // fetch question text to display at the top of button list
        String question = uiStructure.getString(propertyKey + ".askChoice.text", "choose an option");
        String html = "<html><body style='width: 100%;'>" + question + "</body></html>";
        questionLabel.setText(html);

        List<String> itemIds = uiStructure.getIdsList(propertyKey);
        if (itemIds.isEmpty()) {
            buttonsPanel.add(new JLabel("No items defined."), "align center");
        } else {
            for (String id : itemIds) {
                String trimmedId = id.trim();
                if (trimmedId.isEmpty()) continue;

                // Fetch button name
                String buttonPropertyKey = propertyKey.contains(".") ? propertyKey.split("\\.")[1] : propertyKey;
                String buttonText = uiStructure.getString(buttonPropertyKey + "." + trimmedId + ".name", "Unnamed (" + trimmedId + ")");
                String htmlText = "<html><center>" + buttonText + "</center></html>";
                JButton button = new JButton(htmlText);
                button.setMargin(new Insets(10, 15, 10, 15));
                button.setActionCommand(trimmedId);
                button.addActionListener(e -> {
                    if (buttonActionHandler != null) buttonActionHandler.accept(e.getActionCommand(), buttonText);
                });

                buttonsPanel.add(button, "w 40:100:160, growx, growy, align center, gapy 5");
            }
        }
        buttonsPanel.revalidate();
        buttonsPanel.repaint();
    }

    public void setButtonActionHandler(BiConsumer<String, String> handler) { this.buttonActionHandler = handler; }
    public JSplitPane getSplitPane() { return splitPane; }
}