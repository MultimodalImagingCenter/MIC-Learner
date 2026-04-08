package fr.curie.miclearner.panel;

import fr.curie.miclearner.structure.DisplayItem;
import fr.curie.miclearner.structure.StructureManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class DescriptionPanel extends JPanel {
    private JPanel rootPanel;
    private JLabel titleLabel;
    private JPanel scrollPanel;
    private JEditorPane descriptionArea;
    private JComboBox<DisplayItem> modelSelectionBox;
    private JButton selectButton;
    private JScrollPane scrollPane;
    private JPanel selectPanel;
    private JLabel modelSelectionLabel;

    private StructureManager uiStructure;
    private final String selectText;
    private final String notAvailableText;

    private final String SELECT_KEY = "select.button";
    private final String NOT_AVAILABLE_KEY = "unavailable.text";


    public DescriptionPanel(StructureManager uiStructure) {
        // fetch texts for button
        this.uiStructure = uiStructure;
        this.selectText = uiStructure.getString(SELECT_KEY, "Select");
        this.notAvailableText = uiStructure.getString(NOT_AVAILABLE_KEY, "Not Available");

        initUI();
    }

    private void initUI() {
        this.setLayout(new MigLayout("insets 15, fillx, wrap 1, hidemode 3"));

        // title
        titleLabel = new JLabel();
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
        this.add(titleLabel, "align center, gapbottom 10");

        // description area
        descriptionArea = new JEditorPane();
        descriptionArea.setContentType("text/html");
        descriptionArea.setEditable(false);

        // listener for the hyperlinks
        descriptionArea.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported()) {
                try {
                    // Open the link in the default system browser
                    Desktop.getDesktop().browse(e.getURL().toURI()); }
                catch (IOException | URISyntaxException ex) {
                    System.err.println("Unable to open link: " + e.getDescription());
                    ex.printStackTrace(); }
            }
        });

        // add description area to scrollPan, and scrollPane to Panel
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        this.add(scrollPane, "grow, pushy");

        // slection panel
        JPanel selectPanel = new JPanel(new MigLayout("insets 5, hidemode 3, fillx", "[center, min!, shrink 0][center grow]"));

        modelSelectionLabel = new JLabel();
        modelSelectionBox = new JComboBox<>();
        selectButton = new JButton(selectText);

        selectPanel.add(modelSelectionLabel, "cell 0 0, hidemode 3");
        selectPanel.add(modelSelectionBox, "cell 0 1, hidemode 3");
        selectPanel.add(selectButton, "cell 1 0 1 2, , growy ");

        // default settings
        modelSelectionBox.setVisible(false);
        modelSelectionLabel.setVisible(false);
        selectButton.setVisible(true);
        selectButton.setEnabled(false);

        this.add(selectPanel, "growx, align center");
    }

    /**
     * Updates the panel with new content and configures the select button. (no model selection list)
     *
     */
    public void updateContent(String title, String htmlContent, ActionListener selectAction) {
        titleLabel.setText(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        descriptionArea.setText(htmlContent);
        descriptionArea.setCaretPosition(0); // Scroll to the top

        // make model selection not visible
        modelSelectionBox.setVisible(false);
        modelSelectionLabel.setVisible(false);

        // Remove any previous action listeners to prevent duplicates
        for (ActionListener al : selectButton.getActionListeners()) {
            selectButton.removeActionListener(al);
        }
        selectButton.addActionListener(selectAction);
        selectButton.setText(selectText);
        selectButton.setEnabled(true);
    }

    /**
     * Updates the panel with new content and
     * update the model selection list
     * configures the select button.
     *
     */
    public void updateContentList(String title, String htmlContent, ActionListener selectAction, List<DisplayItem> exampleItemsList) {
        titleLabel.setText(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        descriptionArea.setText(htmlContent);
        descriptionArea.setCaretPosition(0); // Scroll to the top

        // make model selection visible
        modelSelectionBox.setModel(new DefaultComboBoxModel<>(exampleItemsList.toArray(new DisplayItem[0])));
        modelSelectionLabel.setText(uiStructure.getString("buttonListPage.askModel.text", "Choose a model"));
        modelSelectionBox.setVisible(true);
        modelSelectionLabel.setVisible(true);

        // Remove any previous action listeners to prevent duplicates
        for (ActionListener al : selectButton.getActionListeners()) {
            selectButton.removeActionListener(al);
        }

        selectButton.addActionListener(selectAction);
        selectButton.setText(selectText);
        selectButton.setEnabled(true);
    }

    /**
     * Updates the panel with content but keeps the select button disabled.
     * It also allows for a custom message on the button.
     */
    public void updateContentDisabled(String title, String htmlContent) {
        titleLabel.setText(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        descriptionArea.setText(htmlContent);
        descriptionArea.setCaretPosition(0);

        // Remove any previous action listeners
        for (ActionListener al : selectButton.getActionListeners()) {
            selectButton.removeActionListener(al);
        }

        // make model selection not visible
        modelSelectionBox.setVisible(false);
        modelSelectionLabel.setVisible(false);

        // Set the message but keep the button disabled
        selectButton.setText(notAvailableText);
        selectButton.setEnabled(false);
        selectButton.setVisible(true);
    }

    public String getSelectedExampleId() {
        DisplayItem selected = (DisplayItem) modelSelectionBox.getSelectedItem();
        if (selected != null) {
            return selected.getId();
        }
        return null;
    }

    /**
     * Resets the panel to its default "select an item" state.
     */
    public void reset() {
        // TODO : make those texts linked to bundle
        titleLabel.setText("Choose an item to see details : ");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        descriptionArea.setText("<html>\n" +
                "  <head>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <p>\n" +
                "    </p>\n" +
                "  </body>\n" +
                "</html>");

        selectButton.setEnabled(false);
        selectButton.setText(selectText);
        modelSelectionBox.setVisible(false);
        modelSelectionLabel.setVisible(false);
    }
}