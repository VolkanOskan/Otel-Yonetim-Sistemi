import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OdaEklemeFormu extends JFrame {
    private JTextField odaNoField, tipField, durumField;
    private JButton ekleButton;

    public OdaEklemeFormu() {
        setTitle("Oda Ekle");
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Oda No:"));
        odaNoField = new JTextField();
        add(odaNoField);

        add(new JLabel("Tip:"));
        tipField = new JTextField();
        add(tipField);

        add(new JLabel("Durum:"));
        durumField = new JTextField();
        add(durumField);

        ekleButton = new JButton("Ekle");
        add(ekleButton);

        ekleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int odaNo = Integer.parseInt(odaNoField.getText());
                String tip = tipField.getText();
                String durum = durumField.getText();

                try (Connection con = DBConnection.getConnection()) {
                    String sql = "INSERT INTO odalar (oda_no, tip, durum) VALUES (?, ?, ?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, odaNo);
                    ps.setString(2, tip);
                    ps.setString(3, durum);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Oda başarıyla eklendi");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OdaEklemeFormu().setVisible(true);
            }
        });
    }
}
