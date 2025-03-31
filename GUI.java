package zad1;

import javax.swing.*;
import javax.swing.border.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Map;

public class GUI {
    private JTextField searchField1,searchField2,searchField3;

    private JFXPanel fxPanel;
    private JFrame frame;
    private Map<String, String> weather;
    private Double rate1;
    private Double rate2;
    private JLabel fromCurr1;
    private JLabel toCurr1;
    private JLabel fromVal1;
    private JLabel toVal1;
    private JLabel fromCurr2;
    private JLabel toCurr2;
    private JLabel fromVal2;
    private JLabel toVal2;
    private String[] serviceData;
    private Service service;
    private String defaultCity;
    private static String[] weatherLabels = {
            "Lokalizacja",
            "Temperatura",
            "Odczuwalna temperatura",
            "Minimalna temperatura",
            "Maksymalna temperatura",
            "Ciśnienie",
            "Wilgotność",
            "Widoczność",
            "Wiatr",
            "Zachmurzenie",
            "Pogoda",
            "Wschód słońca",
            "Zachód słońca"
    };

    public GUI(Map<String, String> parsedData, Double r1, Double r2, Service service) {
        this.weather = parsedData;
        this.rate1 = r1;
        this.rate2 = r2;
        this.service = service;

    }

    public void launchGui() {
        frame = new JFrame("GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout(10, 10));
        ((JComponent) frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        mainSplitPane.setResizeWeight(0.5);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel textFieldsPanel = new JPanel();
        textFieldsPanel.setLayout(new BoxLayout(textFieldsPanel, BoxLayout.X_AXIS));
        textFieldsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        searchField1 = new JTextField("miasto...");
        searchField1.setBorder(new RoundBorder(15, Color.GRAY));
        searchField1.setForeground(Color.GRAY);

        searchField2 = new JTextField("kraj...");
        searchField2.setBorder(new RoundBorder(15, Color.GRAY));
        searchField2.setForeground(Color.GRAY);

        searchField3 = new JTextField("waluta...");
        searchField3.setBorder(new RoundBorder(15, Color.GRAY));
        searchField3.setForeground(Color.GRAY);

        addFocusListeners();

        textFieldsPanel.add(searchField1);
        textFieldsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        textFieldsPanel.add(searchField2);
        textFieldsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        textFieldsPanel.add(searchField3);


        JButton searchButton = new JButton("Szukaj");
        searchButton.setBorder(new RoundBorder(15, Color.GRAY));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalGlue());

        leftPanel.add(textFieldsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(buttonPanel);

        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        topPanel.setBorder(BorderFactory.createTitledBorder("Pogoda"));
        fillTopPanel(topPanel);
        leftPanel.add(topPanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Kurs walut"));
        makeBottomPanel(bottomPanel);

        leftPanel.add(bottomPanel);

        searchButton.addActionListener(e -> {
            String text1 = searchField1.getText().trim().equals("miasto...")?"":searchField1.getText().trim();
            String text2 = searchField2.getText().trim().equals("kraj...")?"":searchField2.getText().trim();
            String text3 = searchField3.getText().trim().equals("waluta...")?"":searchField3.getText().trim();

            if(!text1.isEmpty()){loadWebPage( searchField1.getText().trim()); }

            if((!text1.isEmpty() && !text2.isEmpty())){ updateTopPanel(topPanel,searchField2.getText().trim(),searchField1.getText().trim());}

            if(!text2.isEmpty() && !text3.isEmpty()){updateCurrenciesRate(bottomPanel,searchField2.getText().trim(),searchField3.getText().trim());}



        });


        fxPanel = new JFXPanel();
        fxPanel.setBorder(new CompoundBorder(new TitledBorder(new LineBorder(Color.DARK_GRAY), "Wikipedia"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(fxPanel);
        frame.add(mainSplitPane, BorderLayout.CENTER);
        loadWebPage(weather.get("Lokalizacja").substring(0, weather.get("Lokalizacja").indexOf(",")));
        frame.setVisible(true);
    }
    private void updateCurrenciesRate(JPanel bottomPanel,String country,String Currency){
        service.setCountryInfo(country);
        service.setRateFor(Currency);
        toCurr1.setText(service.getCurrency());
        fromCurr1.setText(service.getRate());
        toVal1.setText(Double.toString(service.getRateFor(Currency)));

        toCurr2.setText(service.getCurrency());
        toVal2.setText(Double.toString(service.getNBPRate()));

        bottomPanel.revalidate();
        bottomPanel.repaint();


    }

    private void addFocusListeners() {
        searchField1.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField1.getText().equals("miasto...")) {
                    searchField1.setText("");
                    searchField1.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField1.getText().isEmpty()) {
                    searchField1.setForeground(Color.GRAY);
                    searchField1.setText("miasto...");
                }
            }
        });

        searchField2.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField2.getText().equals("kraj...")) {
                    searchField2.setText("");
                    searchField2.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField2.getText().isEmpty()) {
                    searchField2.setForeground(Color.GRAY);
                    searchField2.setText("kraj...");
                }
            }

        });
        searchField3.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField3.getText().equals("waluta...")) {
                    searchField3.setText("");
                    searchField3.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField3.getText().isEmpty()) {
                    searchField3.setForeground(Color.GRAY);
                    searchField3.setText("waluta...");
                }
            }

        });
    }

    private void loadWebPage(String kraj) {
        String baseUrl = "https://pl.wikipedia.org/wiki/";

        if (!kraj.isEmpty()) {
            createJFXContent(baseUrl + kraj);
        } else {
            JOptionPane.showMessageDialog(frame, "Proszę podać kraj", "Błąd", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void makeBottomPanel(JPanel panel) {
        panel.removeAll();

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Marginesy

        fromCurr1 = new JLabel(service.getRate());
        toCurr1 = new JLabel(service.getCurrency());
        fromVal1 = new JLabel("1.0");
        toVal1 = new JLabel(Double.toString(rate1));

        fromCurr2 = new JLabel("PLN");
        toCurr2 = new JLabel(service.getCurrency());
        fromVal2 = new JLabel("1.0");
        toVal2 = new JLabel(Double.toString(rate2));

        setComponentStyle(fromCurr1, toCurr1, fromVal1, toVal1,
                fromCurr2, toCurr2, fromVal2, toVal2);

        addCurrencyRow(panel, gbc, 0, fromCurr1, toCurr1, fromVal1, toVal1);

        addCurrencyRow(panel, gbc, 10, fromCurr2, toCurr2, fromVal2, toVal2);

        panel.revalidate();
        panel.repaint();
    }

    private void setComponentStyle(JComponent... components) {
        for (JComponent comp : components) {
            comp.setFont(new Font("Arial", Font.BOLD,
                    comp instanceof JLabel && ((JLabel)comp).getText().equals("za") ? 16 : 20));
        }
    }

    private void addCurrencyRow(JPanel panel, GridBagConstraints gbc, int yPos,
                                JLabel fromCurr, JLabel toCurr, JLabel fromVal, JLabel toVal) {
        gbc.gridy = yPos;
        gbc.gridx = 0;
        panel.add(fromCurr, gbc);

        gbc.gridx = 1;
        JLabel coupler = new JLabel("za");
        coupler.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(coupler, gbc);

        gbc.gridx = 2;
        panel.add(toCurr, gbc);

        gbc.gridy = yPos+1;
        gbc.gridx = 0;
        panel.add(fromVal, gbc);

        gbc.gridx = 2;
        panel.add(toVal, gbc);
    }

    private void createJFXContent(String url) {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().load(url);
            fxPanel.setScene(new Scene(webView));
        });
    }

    private void fillTopPanel(JPanel topPanel) {
        for(String element: weatherLabels){
            topPanel.add(new JLabel(element + ": " +weather.get(element) ));
        }

    }
    private void updateTopPanel(JPanel topPanel,String country, String city){
        topPanel.removeAll();
        service.setCountryInfo(country);
        weather = service.parseWeatherData(service.getWeather(city));
        for(String element: weatherLabels){
            topPanel.add(new JLabel(element + ": " +weather.get(element) ));
        }
        topPanel.revalidate();
        topPanel.repaint();
    }







static class RoundBorder extends AbstractBorder {
    private int radius;
    private Color color;

    public RoundBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius/2, radius/2, radius/2, radius/2);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = radius/2;
        insets.top = insets.bottom = radius/2;
        return insets;
    }
}}