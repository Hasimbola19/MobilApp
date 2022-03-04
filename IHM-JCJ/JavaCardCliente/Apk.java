package JavaCardCliente;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Color;
import javax.swing.JButton;
import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.sun.javacard.apduio.Apdu;

import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class Apk {

	private JFrame frame;
	private JTextField textField;
	JFrame f = new JFrame("Historique de tout les actions");
    JTable table = new JTable();
    String data[][] = new String[30][3];
    String columns[] = { "Date", "Action", "Montant" };
    byte[] datas;
    Apdu apdu;

    JPanel panel = new JPanel();
    JScrollPane pane;
    static byte counter;
    int montant;
    static int i = 0;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    String date,action;


    static Card card = null;
    boolean end = false;
    ResponseAPDU response;
    int count; 

	final static byte Wallet_CLA =(byte)0xB0;
	// codes of INS byte in the command APDU header
	final static byte INIT = (byte) 0x10;
	final static byte VERIFY = (byte) 0x20;
	final static byte CREDIT = (byte) 0x30;
	final static byte DEBIT = (byte) 0x40;
	final static byte GET_BALANCE = (byte) 0x50;
	final static byte UNBLOCK = (byte) 0x60;
	final static byte CHANGE_PIN = (byte) 0x70;
	final static byte SET_DATE = (byte)0x80;
	final static byte GET_DATE = (byte)0x90;
	final static byte SET_NUM = (byte)0x11;
	final static byte GET_NUM = (byte)0x12;
	
    // Signal that there is no error
    public final static String SW_SUCCESS_RESPONSE = "36864";

	private static byte[] APPLET_AID = {0x7C,0x0F,0x1B,0x01,(byte)0x88,0x01}; 
	/**
	 * Launch the Apk.
	 */
	public static void main(String[] args) {
		connecter();
		 try {
	            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        } catch (ClassNotFoundException ex) {
	            java.util.logging.Logger.getLogger(Apk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (InstantiationException ex) {
	            java.util.logging.Logger.getLogger(Apk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (IllegalAccessException ex) {
	            java.util.logging.Logger.getLogger(Apk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	            java.util.logging.Logger.getLogger(Apk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Apk window = new Apk();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the Apk.
	 */
	public Apk() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 1165, 737);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 153, 153));
		panel.setBounds(0, 0, 317, 706);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(0, 153, 153));
		panel_1.setBounds(0, 189, 317, 373);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBounds(307, 0, 851, 706);
		frame.getContentPane().add(panel_6);
		panel_6.setLayout(null);
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(10, 65, 841, 641);
		panel_6.add(panel_4);
		panel_4.setBackground(new Color(255, 255, 255));
		panel_4.setLayout(null);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBackground(new Color(255, 255, 255));
		panel_7.setBounds(12, 29, 817, 248);
		panel_4.add(panel_7);
		panel_7.setLayout(null);
		
		final JLabel lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setBounds(106, 136, 599, 112);
		panel_7.add(lblNewLabel_2);
		lblNewLabel_2.setFont(new Font("Inter", Font.BOLD, 35));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBounds(0, 0, 817, 53);
		panel_7.add(panel_5);
		panel_5.setBackground(new Color(255, 255, 255));
		
		JLabel lblNewLabel_1 = new JLabel("MONTANT");
		lblNewLabel_1.setForeground(new Color(0, 51, 153));
		lblNewLabel_1.setFont(new Font("Inter", Font.BOLD, 38));
		panel_5.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setBounds(82, 66, 645, 63);
		panel_7.add(textField);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("Inter", Font.BOLD, 30));
		textField.setColumns(10);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBackground(new Color(255, 255, 255));
		panel_8.setBounds(59, 290, 735, 277);
		panel_4.add(panel_8);
		panel_8.setLayout(null);
		
		JButton btnNewButton_1 = new JButton("CREDIT");
		btnNewButton_1.setBounds(0, 0, 152, 114);
		panel_8.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.equals("") || !textField.equals(null)) {
					String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
					datas = Helpers.numberStringToByteArray(pin);
					try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA,VERIFY,0x00,0x00,datas));
						if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                   System.err.println("Success!!!");
		                   response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, GET_BALANCE, 0x00, 0x00));
		                    if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                        String sol =  Helpers.byteArrayToString(response.getData());
		                        int sold = Integer.parseInt(sol);
		                        String vab = textField.getText();
					            int valu = Integer.parseInt(vab);
					            int valeur = sold+valu;
					            String fin = String.valueOf(valeur);
					            byte[] fina = fin.getBytes();
					            response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, DEBIT, 0x00, 0x00, fina));
					            setDataInTable(date, "CREDIT", valu);
		                    } else {
		                        System.out.println("An error occurred with status: " + response.getSW());
		                    }
		                } else {
		                   System.out.println("Authentication failed : Invalid PIN Code");
		                }
					} catch (CardException e1) {
						e1.printStackTrace();
					}
				}else {
					JOptionPane.showMessageDialog(frame,"Veuillez entrer une valeur valide");
				}
			}
		});
		
		btnNewButton_1.setFont(new Font("Inter", Font.BOLD, 15));
		btnNewButton_1.setBackground(new Color(102, 51, 0));
		btnNewButton_1.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\creditor.png"));
		
		JButton btnDebit = new JButton("DEBIT");
		btnDebit.setBounds(195, 0, 152, 114);
		panel_8.add(btnDebit);
		btnDebit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.equals("") || !textField.equals(null)) {
					String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
					datas = Helpers.numberStringToByteArray(pin);
					try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA,VERIFY,0x00,0x00,datas));
						if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                   System.err.println("Success!!!");
		                   response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, GET_BALANCE, 0x00, 0x00));
		                    if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                        String sol =  Helpers.byteArrayToString(response.getData());
		                        int sold = Integer.parseInt(sol);
		                        String vab = textField.getText();
					            int valu = Integer.parseInt(vab);
					            int valeur = sold-valu;
					            String fin = String.valueOf(valeur);
					            if(sold < valeur) {
					            	JOptionPane.showMessageDialog(frame, "Solde insuffisant pour la transaction");
					            }else {
					            	byte[] fina = fin.getBytes();
					                response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, DEBIT, 0x00, 0x00, fina));
					                setDataInTable(date, "DEBIT", valu);
					            }
		                    } else {
		                        System.out.println("An error occurred with status: " + response.getSW());
		                    }
		                } else {
		                   System.out.println("Authentication failed : Invalid PIN Code");
		                }
					} catch (CardException e1) {
						e1.printStackTrace();
					}
				}else {
					JOptionPane.showMessageDialog(frame,"Veuillez entrer une valeur valide");
				}
			}
		});
		
		btnDebit.setFont(new Font("Inter", Font.BOLD, 15));
		btnDebit.setBackground(new Color(102, 51, 51));
		btnDebit.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\debt.png"));
		
		JButton btnBalance = new JButton("BALANCE");
		btnBalance.setBounds(392, 0, 152, 114);
		panel_8.add(btnBalance);
		btnBalance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
                System.out.println(pin);
                datas = Helpers.numberStringToByteArray(pin);
                try {
					response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, VERIFY, 0x00, 0x00, datas));
				} catch (CardException e1) {
					e1.printStackTrace();
				}
                if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    System.out.println("Success!!!");
                    try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, GET_BALANCE, 0x00, 0x00));
					} catch (CardException e1) {
						e1.printStackTrace();
					}
                    if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                        System.out.print("\nData : " + Helpers.byteArrayToString(response.getData()));
                    } else {
                        System.out.println("An error occurred with status: " + response.getSW());
                    }
                } else {
                    System.out.println("Authentication failed : Invalid PIN Code");
                }
			}
		});
		
		btnBalance.setFont(new Font("Inter", Font.BOLD, 13));
		btnBalance.setBackground(new Color(102, 51, 102));
		btnBalance.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\wallet.png"));
		
		JButton btnInit = new JButton("INIT");
		btnInit.setBounds(583, 0, 152, 114);
		panel_8.add(btnInit);
		btnInit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.equals("") || !textField.equals(null)) {
					String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
					datas = Helpers.numberStringToByteArray(pin);
					try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA,VERIFY,0x00,0x00,datas));
						if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                   System.err.println("Success!!!");
		                   String name = textField.getText();
		                   int valu = Integer.parseInt(name);
		                   byte[] nameData = name.getBytes();
		                   response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, INIT, 0x00, 0x00, nameData));
		                   setDataInTable(date, "INIT", valu);
						} else {
		                   System.out.println("Authentication failed : Invalid PIN Code");
		                }
					} catch (CardException e1) {
						e1.printStackTrace();
					}
				}else {
					JOptionPane.showMessageDialog(frame,"Veuillez entrer une valeur valide");
				}
			}
		});
		
		btnInit.setFont(new Font("Inter", Font.BOLD, 15));
		btnInit.setBackground(new Color(102, 51, 153));
		btnInit.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\compte-bancaire.png"));
		
		JButton btnSetDate = new JButton("DATE");
		btnSetDate.setBounds(0, 163, 152, 114);
		panel_8.add(btnSetDate);
		btnSetDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, GET_DATE, 0x00, 0x00));
				} catch (CardException e1) {
					e1.printStackTrace();
				}
                if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    System.out.print("\nData : " + Helpers.byteArrayToString(response.getData()));
                } else {
                    System.out.println("An error occurred with status: " + response.getSW());
                }
			}
		});
		
		btnSetDate.setFont(new Font("Inter", Font.BOLD, 15));
		btnSetDate.setBackground(new Color(51, 51, 153));
		btnSetDate.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\date-limite.png"));
		
		JButton btnSetDate_1 = new JButton("SET DATE");
		btnSetDate_1.setBounds(195, 163, 152, 114);
		panel_8.add(btnSetDate_1);
		btnSetDate_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.equals("") || !textField.equals(null)) {
					String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
					datas = Helpers.numberStringToByteArray(pin);
					try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA,VERIFY,0x00,0x00,datas));
						if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                   System.err.println("Success!!!");
		                   String name = dtf.format(LocalDateTime.now());
		                   byte[] nameData = name.getBytes();
		                   response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, SET_DATE, 0x00, 0x00, nameData));
		                } else {
		                   System.out.println("Authentication failed : Invalid PIN Code");
		                }
					} catch (CardException e1) {
						e1.printStackTrace();
					}
				}else {
					JOptionPane.showMessageDialog(frame,"Veuillez entrer une valeur valide");
				}
			}
		});
		
		btnSetDate_1.setBackground(new Color(51, 51, 102));
		btnSetDate_1.setFont(new Font("Inter", Font.BOLD, 15));
		btnSetDate_1.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Downloads\\icons8-planner-40.png"));
		
		JButton btnGetNum = new JButton("NUMBER");
		btnGetNum.setBounds(392, 163, 152, 114);
		panel_8.add(btnGetNum);
		btnGetNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, GET_NUM, 0x00, 0x00));
				} catch (CardException e1) {
					e1.printStackTrace();
				}
                if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    System.out.print("\nData : " + Helpers.byteArrayToString(response.getData()));
                } else {
                    System.out.println("An error occurred with status: " + response.getSW());
                }
			}
		});
		
		btnGetNum.setFont(new Font("Inter", Font.BOLD, 15));
		btnGetNum.setBackground(new Color(51, 51, 51));
		btnGetNum.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\barcode.png"));
		
		JButton btnSetNum = new JButton("SET NUM");
		btnSetNum.setBounds(583, 163, 152, 114);
		panel_8.add(btnSetNum);
		btnSetNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textField.equals("") || !textField.equals(null)) {
					String pin = JOptionPane.showInputDialog(frame, "Enter PIN");
					datas = Helpers.numberStringToByteArray(pin);
					try {
						response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA,VERIFY,0x00,0x00,datas));
						if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
		                   System.err.println("Success!!!");
		                   String name = "1254879652";
		                   byte[] nameData = name.getBytes();
		                   response = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, SET_NUM, 0x00, 0x00, nameData));
		                } else {
		                   System.out.println("Authentication failed : Invalid PIN Code");
		                }
					} catch (CardException e1) {
						e1.printStackTrace();
					}
				}else {
					JOptionPane.showMessageDialog(frame,"Veuillez entrer une valeur valide");
				}
			}
		});
		
		btnSetNum.setBackground(new Color(51, 51, 0));
		btnSetNum.setFont(new Font("Inter", Font.BOLD, 15));
		btnSetNum.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Downloads\\icons8-refresh-barcode-40.png"));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 0, 851, 65);
		panel_6.add(panel_2);
		panel_2.setBackground(new Color(0, 153, 153));
		
		JButton btnNewButton = new JButton("Change PIN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		btnNewButton.setForeground(new Color(255, 255, 204));
		btnNewButton.setFont(new Font("Inter", Font.BOLD, 15));
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\password.png"));
		btnNewButton.setBackground(new Color(0, 153, 204));
		btnNewButton.setBounds(0, 38, 317, 55);
		panel_1.add(btnNewButton);
		
		JButton btnDebloquer = new JButton("Debloquer");
		btnDebloquer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
		btnDebloquer.setForeground(new Color(255, 255, 204));
		btnDebloquer.setFont(new Font("Inter", Font.BOLD, 15));
		btnDebloquer.setHorizontalAlignment(SwingConstants.LEFT);
		btnDebloquer.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\unlocked.png"));
		btnDebloquer.setBackground(new Color(0, 153, 204));
		btnDebloquer.setBounds(0, 116, 317, 55);
		panel_1.add(btnDebloquer);
		
		JButton btnHistorique = new JButton("Historique");
		btnHistorique.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 try
		    	    {
		    	      // Le fichier d'entrée
		    	      File file = new File("file.txt");    
		    	      // Créer l'objet File Reader
		    	      FileReader fr = new FileReader(file);  
		    	      // Créer l'objet BufferedReader        
		    	      BufferedReader br = new BufferedReader(fr);  
		    	      StringBuffer sb = new StringBuffer();    
		    	      String line;
		    	      while((line = br.readLine()) != null)
		    	      {
		    	        // ajoute la ligne au buffer
		    	        sb.append(line);  
		    	        sb.append("\n");  
		    	        String [] tab=line.split("-");
		    	        data[i][0] = tab[0];
		    	        data[i][1] = tab[1];
		    	        data[i][2] = tab[2];
		    	        DefaultTableModel model = new DefaultTableModel(data, columns);
		   	         	table.setModel(model);
		   	         	table.setShowVerticalLines(true);
		   	         	i++;
		   		      
		   	         	model.setRowCount(i);
		   	         	pane = new JScrollPane(table);
		   	         	f.getContentPane().add(pane);
		    	      }
		    	      
		    	      fr.close();    
		    	      br.close();
		    	      System.out.println("Contenu du fichier: ");
		    	      System.out.println(sb.toString());  
		    	}catch(IOException eO)
		    	{
		    	   eO.printStackTrace();
		    	}
				
				     // table.setShowGrid(true);
			    f.setSize(500, 250);
			    f.setResizable(false);
			    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    	f.setVisible(true);
			}
		});
		
		btnHistorique.setForeground(new Color(255, 255, 204));
		btnHistorique.setFont(new Font("Inter", Font.BOLD, 15));
		btnHistorique.setHorizontalAlignment(SwingConstants.LEFT);
		btnHistorique.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\history.png"));
		btnHistorique.setBackground(new Color(0, 153, 204));
		btnHistorique.setBounds(0, 192, 317, 55);
		panel_1.add(btnHistorique);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(0, 153, 153));
		panel_3.setBounds(0, 28, 305, 132);
		panel.add(panel_3);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Hasimbola RAKOTOSON\\Pictures\\1542.png"));
		panel_3.add(lblNewLabel);
	}
	  private void setDataInTable(String date,String action,int montant) {
	    	try
	    	{
	    	 String filename="file.txt";
	    	 FileWriter fw = new FileWriter(filename,true);
	    	 fw.write(date +"-"+ action +"-"+ montant);
	    	 fw.append("\n");
	    	 fw.close();
	    	}
	    	catch(IOException ioe)
	    	{
	    	 System.err.println(ioe.getMessage());
	    	}
	    }
	      
	      public static void handleResponse(Apdu apdu) {
	        if (apdu.getStatus() != 0x9000) {
	            System.out.println("An error occurred with status: " + apdu.getStatus());
	        } else {
	            System.out.println("OK");
	        }
	    }
	    
	    @SuppressWarnings("resource")
		private static byte[] fileToByteArray(String filePath) {
	        FileInputStream fis = null;
	        try {
	            fis = new FileInputStream(filePath);

	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int readCount = 0;

	            while ((readCount = fis.read(buffer)) != -1){
	                baos.write(buffer, 0, readCount);
	            }

	            return baos.toByteArray();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return new byte[] { };
	    }
	    
	    private static void connecter() {
	        TerminalFactory terminalFactory = TerminalFactory.getDefault();
	        List<CardTerminal> cardTerminals = null;
	        try {
	            cardTerminals = terminalFactory.terminals().list();

	            if (cardTerminals.isEmpty()) {
	                System.out.println("Skipping the test: no card terminals available");
	                return;
	            }

	            // System.out.println("Terminals: " + cardTerminals);
	            CardTerminal cardTerminal = cardTerminals.get(0);

	            if (cardTerminal.isCardPresent()) {
	                card = cardTerminal.connect("T=1");
	                System.out.println("Connected to the card!");
	            }
	        } catch (CardException e) {
	            e.printStackTrace();
	        }

	        try {
	            ResponseAPDU reponse = card.getBasicChannel().transmit(new CommandAPDU(Wallet_CLA, 0xA4, 0x04, 0x00, APPLET_AID));
	            if (!String.valueOf(reponse.getSW()).equals(SW_SUCCESS_RESPONSE)) {
	                System.out.println("Error while selecting the applet: " + reponse.getSW());
	                System.exit(1);
	            }
	            
	        } catch (CardException e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
	    }
	
}
