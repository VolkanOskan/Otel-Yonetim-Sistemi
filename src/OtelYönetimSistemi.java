import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OtelYönetimSistemi extends JFrame {
    private JButton musteriEkleButton, rezervasyonEkleButton, rezervasyonGuncelleButton;

    public OtelYönetimSistemi() {
        setTitle("Otel Yönetim Sistemi");
        setLayout(new BorderLayout(20, 20)); 

        
        JLabel welcomeLabel = new JLabel("Otel Yönetim Sistemi'ne Hoşgeldiniz.", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

       
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20); 
        gbc.gridx = 0;
        gbc.gridy = 0;

        musteriEkleButton = new JButton("Müşteri Ekle");
        rezervasyonEkleButton = new JButton("Rezervasyon Ekle");
        rezervasyonGuncelleButton = new JButton("Rezervasyon Güncelle");

        
        musteriEkleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        rezervasyonEkleButton.setFont(new Font("Arial", Font.PLAIN, 14));
        rezervasyonGuncelleButton.setFont(new Font("Arial", Font.PLAIN, 14));

        musteriEkleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MusteriEklemeFormu musteriEklemeFormu = new MusteriEklemeFormu();
                musteriEklemeFormu.setVisible(true);
                dispose();
            }
        });

        rezervasyonEkleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RezervasyonEklemeFormu rezervasyonEklemeFormu = new RezervasyonEklemeFormu();
                rezervasyonEklemeFormu.yukleMusteriAdlari();
                rezervasyonEklemeFormu.setVisible(true);
                dispose();
            }
        });

        rezervasyonGuncelleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RezervasyonGuncellemeFormu rezervasyonGuncellemeFormu = new RezervasyonGuncellemeFormu();
                rezervasyonGuncellemeFormu.setVisible(true);
                dispose();
            }
        });

        buttonPanel.add(musteriEkleButton, gbc);
        gbc.gridx++;
        buttonPanel.add(rezervasyonEkleButton, gbc);
        gbc.gridx++;
        buttonPanel.add(rezervasyonGuncelleButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);

        setSize(600, 500); 
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OtelYönetimSistemi().setVisible(true);
            }
        });
    }
}
