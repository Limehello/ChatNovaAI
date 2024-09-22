import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BotGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GitHubGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("GitHub Repository Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 2)); // 5 rows, 2 columns

        // Create components
        JLabel repoNameLabel = new JLabel("Repository Name:");
        JTextField repoNameField = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JButton createButton = new JButton("Create Repository");
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);

        // Add action listener to the button
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String repoName = repoNameField.getText();
                String description = descriptionField.getText();
                // Simulate creating a repository
                outputArea.append("Created repository: " + repoName + "\nDescription: " + description + "\n\n");
                repoNameField.setText(""); // Clear the input field
                descriptionField.setText(""); // Clear the input field
            }
        });

        // Add components to the frame
        frame.add(repoNameLabel);
        frame.add(repoNameField);
        frame.add(descriptionLabel);
        frame.add(descriptionField);
        frame.add(createButton);
        frame.add(new JLabel("Output:"));
        frame.add(outputArea);

        // Make the frame visible
        frame.setVisible(true);
    }
}
