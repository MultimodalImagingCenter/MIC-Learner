package fr.curie.miclearner.panel;

import fr.curie.miclearner.MainApplication_Frame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

public class HomePanel extends JPanel {
    private JButton taskButton;
    private JButton modelButton;
    private JLabel taskDescriptionLabel;
    private JLabel modelDescriptionLabel;
    private JLabel askChoiceLabel;
    private JLabel titleLabel;
    private JPanel rootPanel;

    private MainApplication_Frame mainFrame;

    public HomePanel(MainApplication_Frame mainFrame) {
        this.mainFrame = mainFrame;

        initUI();
        initComponents();
    }

    private void initUI() {
        ResourceBundle bundle = ResourceBundle.getBundle("UIstrings");
        setLayout(new BorderLayout());
        rootPanel = new JPanel(new MigLayout("insets 30, fillx, gapy 20", "[50%, center][50%, center]"));

        // Components
        // Title and subtitle
        titleLabel = new JLabel(bundle.getString("app.title"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        askChoiceLabel = new JLabel(bundle.getString("homePage.askChoice.text"));
        askChoiceLabel.setFont(new Font("Serif", Font.ITALIC, 17));
        askChoiceLabel.setForeground(new Color(0x160330));

        // Buttons
        taskButton = new JButton(bundle.getString("homePage.taskChoice.button"));
        modelButton = new JButton(bundle.getString("homePage.modelChoice.button"));

        // Descriptions
        taskDescriptionLabel = new JLabel("<html><center>" + bundle.getString("homePage.taskButtonDescription.text") + "</center></html>");
        taskDescriptionLabel.setFont(new Font("Serif", Font.ITALIC, 14));

        modelDescriptionLabel = new JLabel("<html><center>" + bundle.getString("homePage.modelButtonDescription.text") + "</center></html>");
        modelDescriptionLabel.setFont(new Font("Serif", Font.ITALIC, 14));

        // Add to layout
        rootPanel.add(titleLabel, "span 2, wrap, center, gapafter 20");
        rootPanel.add(askChoiceLabel, "span 2, wrap, center, gapy 40");

        rootPanel.add(taskButton, "w 250!, h 40!");
        rootPanel.add(modelButton, "w 250!, h 40!, wrap");

        rootPanel.add(taskDescriptionLabel, "w 300!, align center");
        rootPanel.add(modelDescriptionLabel, "w 300!, align center");

        add(rootPanel, BorderLayout.CENTER);
    }

    private void initComponents() {
        taskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.navigateToTasksList();
                }
            }
        });

        modelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainFrame != null) {
                    mainFrame.navigateToModelsList();
                }
            }
        });
    }
}