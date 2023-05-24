package version2;

import javax.swing.*;
import java.awt.*;

public class ApplicationGUI {
    private static JFrame frame;
    private static JPanel panel;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    // 其他GUI元素

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createGUI();
            addComponents();
            frame.setVisible(true);
        });
    }

    private static void createGUI() {
        frame = new JFrame("Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        frame.add(panel, BorderLayout.CENTER);
    }

    private static void addComponents() {
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        panel.add(registerButton);

        // 添加其他GUI元素和事件监听器
    }

    private static void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // 执行登录操作
        // ...

        // 更新GUI或执行其他操作
        // ...
    }

    private static void performRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // 执行注册操作
        // ...

        // 更新GUI或执行其他操作
        // ...
    }
}
