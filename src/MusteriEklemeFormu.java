import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MusteriEklemeFormu extends JFrame {
    private JTextField adField, soyadField, emailField, telefonField;
    private JButton ekleButton;

    public MusteriEklemeFormu() {
        setTitle("Müşteri Ekle");
        setLayout(new GridLayout(10, 9, 10,10));

        add(new JLabel("Ad:"));
        adField = new JTextField();
        add(adField);

        add(new JLabel("Soyad:"));
        soyadField = new JTextField();
        add(soyadField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Telefon:"));
        telefonField = new JTextField();
        add(telefonField);

        ekleButton = new JButton("Ekle");
        add(ekleButton);

        ekleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ad = adField.getText().trim();
                String soyad = soyadField.getText().trim();
                String email = emailField.getText().trim();
                String telefon = telefonField.getText().trim();

                if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || telefon.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun!", "Hata", JOptionPane.ERROR_MESSAGE);
                } else {
                    try (Connection con = DBConnection.getConnection()) {
                        String sql = "INSERT INTO Musteriler (ad, soyad, email, telefon) VALUES (?, ?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, ad);
                        ps.setString(2, soyad);
                        ps.setString(3, email);
                        ps.setString(4, telefon);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Müşteri başarıyla eklendi.");

                        
                        RezervasyonEklemeFormu rezervasyonEklemeFormu = new RezervasyonEklemeFormu();
                        rezervasyonEklemeFormu.yukleMusteriAdlari();
                        rezervasyonEklemeFormu.setVisible(true);
                        MusteriEklemeFormu.this.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusteriEklemeFormu().setVisible(true);
            }
        });
    }
}
