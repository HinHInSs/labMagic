package org.example;

import org.example.model.Mission;
import org.example.parser.Parser;
import org.example.parser.ParserFabric;
import org.example.service.ReportService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GUI extends JFrame {

    private JTextArea textArea;
    private JButton openButton;
    private JLabel statusLabel;

    public GUI() {
        setTitle("Анализатор миссий магов");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openButton = new JButton("Выбрать файл");
        statusLabel = new JLabel("Файл не выбран");
        topPanel.add(openButton);
        topPanel.add(statusLabel);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Отчет по миссии"));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        openButton.addActionListener(e -> openFile());
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл миссии");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Path filePath = fileChooser.getSelectedFile().toPath();
            statusLabel.setText("Файл: " + filePath.getFileName());

            try {
                String content = Files.readString(filePath);
                Parser parser = ParserFabric.getParser(filePath);
                Mission mission = parser.parse(content);

                ReportService reportService = new ReportService();
                String report = reportService.getReport(mission);

                textArea.setText(report);

            } catch (IOException e) {
                textArea.setText("Ошибка чтения файла: " + e.getMessage());
                statusLabel.setText("Ошибка");
            } catch (Exception e) {
                textArea.setText("Ошибка парсинга: " + e.getMessage());
                statusLabel.setText("Ошибка");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}