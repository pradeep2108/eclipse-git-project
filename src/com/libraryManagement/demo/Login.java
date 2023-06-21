package com.libraryManagement.demo;
//super bro

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.dbutils.DbUtils;

import com.libraryManagement.demo.Main.ex;



public class Login implements LibraryService {
		
	public void login() {
		
		JFrame frame = new JFrame("Login");
        JLabel l1, l2;
        JTextField user_key;
        JPasswordField user_pass;
        JButton login_but;
		
		l1 = new JLabel("Username");  //create lable username
		l1.setBounds(30, 15, 100, 30); // x axis, y axis, width, height
		
		l2 = new JLabel("Password"); //create label password
		l2.setBounds(30, 50, 100, 30);
		
		user_key = new JTextField(); //Create text field for username
	    user_key.setBounds(110, 15, 200, 30);
	         
	    user_pass=new JPasswordField(); //Create text field for password
	    user_pass.setBounds(110, 50, 200, 30);
		
		login_but =  new JButton("Login");
		login_but.setBounds(130,90,80,25);
		login_but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String userName = user_key.getText();
				String password = String.valueOf(user_pass.getPassword());
				
				if (userName.isEmpty()) {
					JOptionPane.showMessageDialog(null, "please enter username");
					
				}else if (password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "please enter password");
					
				}else {					     	 
						
						try {
							Connection con = DAO.getConnection();
							String st = "SELECT * FROM users WHERE username=? AND password=?";
							PreparedStatement pstmt = con.prepareStatement(st, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
							pstmt.setString(1, userName);
			                pstmt.setString(2, password);
							ResultSet rs = pstmt.executeQuery();
							if (!rs.next()) {
								System.out.println("No User");
								JOptionPane.showMessageDialog(null, "Wrong username or password!");
							}else {
								frame.dispose();
								rs.beforeFirst();
								while (rs.next()) {
									boolean admin= rs.getBoolean("admin");
									String UID = rs.getString("uid");
									if (admin) {
										admin_menu(); //redirect to admin menu
										return;
									}else {
									user_menu(UID);//redirect to user menu for that user ID
									return;
									}
									
								}
							}
							rs.close();
							pstmt.close();
							con.close();
							
						} catch (Exception e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
				}
			}
		});

		frame.add(user_pass); //add password
		frame.add(login_but); //adding button in JFrame 
		frame.add(user_key);  //add user
		frame.add(l1);        // add label1 i.e. for username
		frame.add(l2);        // add label2 i.e. for password
		
		frame.setSize(400, 180);   //400 width and 500 height
		frame.setLayout(null);    //using no layout managers
		frame.setVisible(true);    //making the frame visible 
		frame.setLocationRelativeTo(null);;
		
		
		
	}

	@Override
	public void create() throws SQLException, ClassNotFoundException {
		Connection con=null;
		try {
			con = DAO.getConnection();
			ResultSet rs = con.getMetaData().getCatalogs();
			//iterate each catalog in the ResultSet
			while (rs.next()) {
				// Get the database name, which is at position 1
				String databaseName = rs.getString(1);
				if (databaseName.equals("LibrarySystem")) {
					//System.out.print("yes");
					Statement statement = con.createStatement();
					String sql = "DROP DATABASE library";
					//Drop database if it pre-exists to reset the complete database
					statement.executeUpdate(sql);
				}
			}
			     Statement statement = con.createStatement();
			     String sql = "CREATE DATABASE library";
			     statement.executeUpdate(sql);
			     
			     String sql1 = "USE LibrarySystem";
			     statement.executeUpdate(sql1);
			     
			     statement.executeUpdate("CREATE TABLE users (uid SERIAL PRIMARY KEY, username VARCHAR(20), password VARCHAR(50), admin BOOLEAN)");
			     
			     statement.executeUpdate("CREATE TABLE books(bid SERIAL PRIMARY KEY, book_name VARCHAR(50),price NUMERIC(5,2), genre VARCHAR(30))");
			    
			     statement.executeUpdate("CREATE TABLE issue(iid SERIAL PRIMARY KEY, u_id SERIAL REFERENCES users(uid), b_id SERIAL REFERENCES books(bid), issueDate DATE, period INT, returnDate DATE, fine NUMERIC(5,2))");
			     
			     statement.executeUpdate("INSERT INTO books(book_name, price, genre) VALUES ('War and Peace', 'Mystery', 200),  ('The Guest Book', 'Fiction', 300), ('The Perfect Murder','Mystery', 150), ('Accidental Presidents', 'Biography', 250), ('The Wicked King','Fiction', 350)");
		
			     statement.close();
			     rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
 
	public void user_menu(String UID) {
		JFrame frame = new JFrame("User Login");  //Give dialog box name as User functions
	    //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit user menu on closing the dialog box
		JButton view_but = new JButton("View Books");
		view_but.setBounds(20, 20, 125, 25);
		view_but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				JFrame frame = new JFrame("Books Available");//View books stored in database
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				try {
					Connection con = DAO.getConnection();
				   String sql = "SELECT * FROM books";
				   Statement statement = con.createStatement();
				   ResultSet rs = statement.executeQuery(sql);
				   
				   List<Object[]> data = new ArrayList<>();
				   ResultSetMetaData rsMetaData = rs.getMetaData();
				   int columnCount = rs.getMetaData().getColumnCount();
				   
				  while (rs.next()) {
					  Object[] rowData = new Object[columnCount];
					  for (int i = 0; i < columnCount; i++) {
						  rowData[i] = rs.getObject(i +1);
					}
					  data.add(rowData);
					
				}
				  
				  TableModel tableModel = new AbstractTableModel() {

					@Override
					public int getColumnCount() {
						// TODO Auto-generated method stub
						return columnCount;
					}

					@Override
					public int getRowCount() {
						// TODO Auto-generated method stub
						return data.size();
					}

					@Override
					public Object getValueAt(int rowIndex, int columnIndex) {
						// TODO Auto-generated method stub
						return data.get(rowIndex)[columnIndex];
					}
					
					 @Override
				        public String getColumnName(int column) {
				            try {
				                return rsMetaData.getColumnName(column + 1);
				            } catch (SQLException e) {
				                e.printStackTrace();
				            }
				            return super.getColumnName(column);
				        }
					  
				  };
				  
				  JTable book_list = new JTable(tableModel);
				  
				  JScrollPane scroll = new JScrollPane(book_list);
				  frame.add(scroll);
				  frame.setSize(800, 400);
				  frame.setVisible(true);
				  frame.setLocationRelativeTo(null);
				   
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				} 
			}
		});
		
		JButton my_book = new JButton("My Books");
		my_book.setBounds(150,20,120,25);
		my_book.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("My Books");
				//View books issued by user
	            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				int UID_int =  Integer.parseInt(UID); //pass user ID
				
				try {
					Connection connection = DAO.getConnection();
                    String sql = "SELECT DISTINCT issue.*, books.book_name, books.genre, books.price " +
                            "FROM issue " +
                            "JOIN books ON books.bid = issue.b_id " +
                            "WHERE issue.u_id = " + UID_int + " " +
                            "  AND issue.b_id IN (SELECT bid FROM issue WHERE issue.u_id = " + UID_int + ") " +
                            "GROUP BY issue.iid, books.book_name, books.genre, books.price";
                    String sql1 = "SELECT b_id FROM issue where u_id="+UID_int;
					
                    Statement statment = connection.createStatement();
                    statment = connection.createStatement();
					 
                    ResultSet rs = statment.executeQuery(sql);
                   
                    DefaultTableModel model = new DefaultTableModel();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    for (int column = 1; column <= columnCount; column++) {
                        model.addColumn(metaData.getColumnName(column));
                    }
                    
                    while (rs.next()) {
                    	Object[] row = new Object[columnCount];
                    	for (int column = 1; column <= columnCount; column++) {
                    		row[column -1] = rs.getObject(column);	
						}
						model.addRow(row);
					}
                    JTable bookList = new JTable(model);
                    JScrollPane scrollPane = new JScrollPane(bookList);

                    frame.add(scrollPane);
                    frame.setSize(800,400);
                    frame.setVisible(true);
                    frame.setLocationRelativeTo(null);
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				} 
			}
		});
		
		
		frame.add(my_book);//add my books
		frame.add(view_but);
		frame.setSize(400, 200);//400 width and 500 height 
		frame.setLayout(null);//using no layout managers  
		frame.setVisible(true);//making the frame visible
		frame.setLocationRelativeTo(null);
	}
	
	
	public void admin_menu() {
		
		JFrame admin_frame = new JFrame("Admin Function");//Give dialog box name as admin functions
	    admin_frame.setSize(900, 600);
	    admin_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonPanel.setPreferredSize(new Dimension(admin_frame.getWidth(), admin_frame.getHeight() - 100));
		
//		 JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
//	        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//	        JPanel topRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
//	        JPanel bottomRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

	
		JButton create_but =  new JButton("Create/Reset");//creating instance of JButton to create or reset database
		create_but.setBounds(460,60,120,25);
		create_but.addActionListener(new ActionListener() {//Perform action
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					create();
					JOptionPane.showMessageDialog(null, "Database Created/Reset");
				} catch (ClassNotFoundException |SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
			}
		});
		
		JButton view_but =  new JButton("View book");
		view_but.setBounds(20,20,125,25);
		view_but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFrame frame =  new JFrame("Books Available");
				//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Connection connection;
				String sql = "SELECT * FROM books";
				try {
					 connection =  DAO.getConnection();
					Statement statement = connection.createStatement();
					statement = connection.createStatement();
					ResultSet rs = statement.executeQuery(sql);
					
					
					DefaultTableModel model = new DefaultTableModel();
					ResultSetMetaData metaData =  rs.getMetaData();
					int columnCount =  metaData.getColumnCount();
					for (int column = 1; column <= columnCount; column++) {
						
						model.addColumn(metaData.getColumnName(column));	
					}
					 
					while (rs.next()) {
						Object[] row = new Object[columnCount];
						for (int column = 1; column <= columnCount; column++) {
							row[column -1] = rs.getObject(column);
						}
						
						model.addRow(row);;
					}
					
					JTable book_list =  new JTable(model);
					
					//mention scroll bar
					JScrollPane scrollPane = new JScrollPane(book_list);

					frame.add(scrollPane);
					frame.setSize(800,400);
					frame.setVisible(true);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					
					    rs.close();
				        statement.close();
				        connection.close();
					
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					JOptionPane.showInternalMessageDialog(null, e1);
				}
				
			}
		});
		
		JButton user_but = new JButton("View Users");
		user_but.setBounds(160,20,125,25);
		user_but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("User List");
				//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
				String sql = "SELECT * FROM users";
				try {
					Connection connection = DAO.getConnection();
					Statement statment = connection.createStatement();
					ResultSet rs = statment.executeQuery(sql);			
					DefaultTableModel model =  new DefaultTableModel();
					ResultSetMetaData metaData = rs.getMetaData();
					int columnCount = metaData.getColumnCount();
				    for (int column = 1; column <= columnCount; column++) {
				    	model.addColumn(metaData.getColumnName(column));
						
					}
				    
				    while (rs.next()) {
				    	Object[] row = new Object[columnCount];
				    	for (int column = 1; column <= columnCount; column++) {
				    		
				    		row[column -1] = rs.getObject(column);
							
						}
				    	model.addRow(row);
					}
				    
				    JTable userList = new JTable(model);
				    JScrollPane scrollPane =  new JScrollPane(userList);
				    frame.add(scrollPane);
			    	frame.setSize(800, 400);
			    	frame.setVisible(true);
			    	frame.setLocationRelativeTo(null);
			    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			    	
					rs.close();
					statment.close();
					connection.close();
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e1);
				} 
			
			}
		});
		
	       JButton issued_but = new JButton("View Issued Books");
	       issued_but.setBounds(300,20,160,25);
	       issued_but.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFrame userList_frame = new JFrame("Users List");
				//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
				
				String sql="select * from issue";
				try {
					Connection connection = DAO.getConnection();
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery(sql);
					
					DefaultTableModel model =  new DefaultTableModel();
					ResultSetMetaData metaData = rs.getMetaData();
					int columnCount = metaData.getColumnCount();
				    for (int column = 1; column <= columnCount; column++) {
				    	model.addColumn(metaData.getColumnName(column));
						
					}
				    
				    while (rs.next()) {
				    	
				    	Object[] row = new Object[columnCount];
				    	for (int column = 1; column <= columnCount; column++) {
				    		row[column -1] = rs.getObject(column);
	
						}
				    	
				    	model.addRow(row);
						
					}
				    
					JTable issued_list = new JTable(model);
					JScrollPane scrollPane = new JScrollPane(issued_list);
					
					userList_frame.add(scrollPane);
					userList_frame.setSize(800,400);
					userList_frame.setVisible(true);
					userList_frame.setLocationRelativeTo(null);
					userList_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					
					rs.close();
					statement.close();
					connection.close();
					
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, e);
				}
				
				
			}
		});
	       
	      JButton add_user = new JButton("Add User"); 
	      add_user.setBounds(20,60,120,25);
	      add_user.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFrame user_Details = new  JFrame("Enter User Details");
				//g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JLabel l1,l2;
				
				l1 = new JLabel("Username");
				l1.setBounds(30,15, 100,30);
				
				l2 = new JLabel("Password");
				l2.setBounds(30,50, 100,30);
				
				JTextField userText  = new JTextField();
				userText.setBounds(110, 15, 200, 30);
				
				JPasswordField userPass = new JPasswordField();
				userPass.setBounds(110, 50, 200, 30);
				
				
				JRadioButton b1 = new JRadioButton("Admin");
				b1.setBounds(55, 80, 200,30);
				
				JRadioButton b2 = new JRadioButton("User");
				b2.setBounds(130, 80, 200,30);
				
				ButtonGroup bg = new ButtonGroup();
				bg.add(b1);bg.add(b2);
				
				JButton create_button = new JButton("Create");
				create_button.setBounds(130,130,80,25);
				create_button.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						String userName = userText.getText();
						String password = String.valueOf(userPass.getPassword());						
						boolean admin = false;
						
						if (b1.isSelected()) {
							admin = true;
						}else if (b2.isSelected()) {
							admin =false;
						}
						
						String sql = "INSERT INTO users(username,password,admin) VALUES(?,?,?)";
						try {
							Connection con = DAO.getConnection();
							PreparedStatement statement = con.prepareStatement(sql);
							statement.setString(1, userName);
							statement.setString(2, password);
							statement.setBoolean(3, admin);
							statement.executeUpdate();
							
						    JOptionPane.showMessageDialog(null, "User added!");
						    user_Details.dispose();
						    statement.close();
						    con.close();
						} catch (ClassNotFoundException | SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						} 
					}
				});
				
				
				user_Details.add(create_button);
				user_Details.add(b1);
				user_Details.add(b2);
				user_Details.add(l1);
				user_Details.add(l2);
				user_Details.add(userText);
				user_Details.add(userPass);
				user_Details.setSize(350,200);
				user_Details.setLayout(null);
				user_Details.setVisible(true);
				user_Details.setLocationRelativeTo(null);
				
				
			}
		});
	      
	      JButton add_book = new JButton("Add Book");
	      add_book.setBounds(160,60,120,25);
	      
	      add_book.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame new_book = new JFrame("Enter Book Details");
				
				JLabel l1, l2, l3;
				
				l1 = new JLabel("Book Name");
				l1.setBounds(30,15, 100,30);
				
				l2 = new JLabel("Genre");
				l2.setBounds(30,53, 100,30);
				
				l3 = new JLabel("Price");
				l3.setBounds(30,90, 100,30);
				
				JTextField add_book = new JTextField();
				add_book.setBounds(110, 15, 200, 30);
				
				JTextField add_genre = new JTextField();
				add_genre.setBounds(110, 53, 200, 30);
				
				JTextField add_price = new JTextField();
				add_price.setBounds(110, 90, 200, 30);
				
				JButton book_submit = new JButton("Submit");
				book_submit.setBounds(130,130,80,25);
				book_submit.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String bookName = add_book.getText();
						String genre = add_genre.getText();
						String price = add_price.getText();
						//converting price of integer to int
						int priceInt = Integer.parseInt(price);
						
						Connection connection;
						
						try {
							connection = DAO.getConnection();
							Statement statment = connection.createStatement();
							statment.executeUpdate("INSERT INTO books(book_name, price, genre) VALUES('"+bookName+"','"+genre+"',"+priceInt+")");
							JOptionPane.showMessageDialog(null, "Book added!");
							new_book.dispose();
							
							statment.close();
							connection.close();
							
						} catch (ClassNotFoundException | SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);

						}

					}
				});
				
				new_book.add(l1);
				new_book.add(l2);
				new_book.add(l3);
				new_book.add(add_book);
				new_book.add(add_genre);
				new_book.add(add_price);
				new_book.add(book_submit);
				new_book.setSize(350,200);
				new_book.setLayout(null);
				new_book.setVisible(true);
				new_book.setLocationRelativeTo(null);
			}
		});
	      
	      JButton issue_book = new JButton("Issue Book");
	      issue_book.setBounds(460,20,120,25);
	      issue_book.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame details_frame= new JFrame("Enter Details");
				JLabel l1,l2,l3,l4;
				
				l1 = new JLabel("Book Id");
				l1.setBounds(30,20, 100,30);
				
				l2 = new JLabel("User Id");
				l2.setBounds(30,58, 100,30);
				
				l3 = new JLabel("Period(days)");
				l3.setBounds(30,96, 100,30);
				
				l4 = new JLabel("Issued Date (yy-mm-dd)");
				l4.setBounds(30,134, 150,30);
				
				JTextField l_bid = new JTextField();
				l_bid.setBounds(140, 20, 200, 30);
				
				JTextField l_uid=new JTextField();
                l_uid.setBounds(140, 58, 200, 30);
                 
                JTextField l_period=new JTextField();
                l_period.setBounds(140, 96, 200, 30);
                 
                JTextField l_issue=new JTextField();
                l_issue.setBounds(190, 134, 130, 30);
                
                JButton issue_but = new JButton("Issue");
                issue_but.setBounds(150,180,80,25);
                issue_but.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						String uid = l_uid.getText();
						String bid = l_bid.getText();
						String period = l_period.getText();
						String issue = l_issue.getText();
						
						int period_int = Integer.parseInt(period);
						
						DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
						java.util.Date parsedDate; 
						java.sql.Date sqlDate = null;
						
						try {
							parsedDate = dateFormat.parse(issue);
							sqlDate = new java.sql.Date(parsedDate.getTime());
							
						} catch (Exception e2) {
							// TODO: handle exception
						}
						
						
						try {
							Connection connection = DAO.getConnection();
							Statement statement = connection.createStatement();
							statement.executeUpdate("INSERT INTO issue(u_id,b_id,issuedate,period) VALUES ('"+uid+"','"+bid+"','"+issue+"',"+period_int+")");
						    JOptionPane.showMessageDialog(null, "Book issued!");
						    details_frame.dispose();
						    
						    statement.close();
						    connection.close();
						} catch (ClassNotFoundException | SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}
						
					}
				});
                
                details_frame.add(l1);
                details_frame.add(l2);
                details_frame.add(l3);
                details_frame.add(l4);
                details_frame.add(issue_but);
                details_frame.add(l_uid);
                details_frame.add(l_bid);
                details_frame.add(l_period);
                details_frame.add(l_issue);
                
                details_frame.setLayout(null);
                details_frame.setSize(400,300);
                details_frame.setVisible(true);
                details_frame.setLocationRelativeTo(null);
				
			}
		});
		
	      JButton return_book = new JButton("Return Book");
	      return_book.setBounds(300,60,160,25);	
	      return_book.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFrame enterDetails = new JFrame("Enter Details");
				JLabel l1,l2;
				
				l1 = new JLabel("Issue ID(IID)");
				l1.setBounds(30,15, 100,30);
				
				l2 = new JLabel("Return Date(yy-MM-dd)");
				l2.setBounds(30,50, 150,30);
				
				JTextField l_iid = new JTextField();
                l_iid.setBounds(180, 15, 200, 30);
                 
                JTextField l_return=new JTextField();
                l_return.setBounds(180, 50, 200, 30);
                
                JButton create_but = new JButton("Return");
                create_but.setBounds(180,100, 100,30);
                create_but.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						String iid    = l_iid.getText();
						String return_date = l_return.getText();
						
						Connection connection;
						
						try {
							connection = DAO.getConnection();
							Statement statement = connection.createStatement();
							
							String date1=null;
							String date2=return_date;
							ResultSet rs = statement.executeQuery("SELECT issuedate FROM issue WHERE iid="+iid);
							
							while (rs.next()) {
								date1=rs.getString(1);
								
							}
							try {
								Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(date1);
	                            Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(date2);
	                            //subtract the dates and store in diff
	                            long diff = date_2.getTime() - date_1.getTime();
	                             //Convert diff from milliseconds to days
	                            ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
							} catch (ParseException e2) {
								e2.printStackTrace();
							}
							//update return date
							statement.executeUpdate("UPDATE issue SET returndate='"+return_date+"' WHERE iid="+iid);
							enterDetails.dispose();
							
							Connection con;
							con = DAO.getConnection();
							Statement stat = con.createStatement();
							//stat.executeUpdate("Use LibrarySystem");
							ResultSet rs1 = stat.executeQuery("SELECT period FROM issue WHERE iid="+iid); 
							String diff =null;
							
							while (rs1.next()) {
								diff = rs1.getString(1);
							}
							
							int diff_int = Integer.parseInt(diff);
							if (ex.days > diff_int ) {//If number of days are more than the period then calculcate fine
								//System.out.println(ex.days);
								int fine = (ex.days - diff_int)*10;
								stat.executeUpdate("Update issue SET fine="+fine+" WHERE iid="+iid);
								String fine_str = ("Fine: Rs. "+fine);
								JOptionPane.showMessageDialog(null, fine_str);
							}
							
							JOptionPane.showMessageDialog(null, "Book Returned!");
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, e1);
						}
						
					}
				});
                
                enterDetails.add(l2);
                enterDetails.add(create_but);
                enterDetails.add(l1);
                enterDetails.add(l_iid);
                enterDetails.add(l_return);
                
                enterDetails.setLayout(null);
                enterDetails.setSize(500,310);
                enterDetails.setVisible(true);
                enterDetails.setLocationRelativeTo(null);

			}
		});
	      
	      	     
	       buttonPanel.add(view_but);
	        buttonPanel.add(user_but);
	        buttonPanel.add(issued_but);
	        buttonPanel.add(issue_book);
	        buttonPanel.add(add_user);
	        buttonPanel.add(add_book);
	        buttonPanel.add(return_book);
	        buttonPanel.add(create_but);
	      
//	        buttonPanel.add(topRowPanel);
//	        buttonPanel.add(bottomRowPanel);
	        
	      admin_frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);   
	      admin_frame.setVisible(true);
	      admin_frame.setLayout(new BorderLayout());
    //      admin_frame.setLayout(new FlowLayout());
	      admin_frame.setLocationRelativeTo(null);
	      
	}
  
	
}





/*DefaultTableModel tableModel = new DefaultTableModel();
tableModel.setColumnIdentifiers(new String[]{"bid", "book name", "price", "genre"});

// Populate the table model with data
while (rs.next()) {
    String bid = rs.getString("bid");
    String bookName = rs.getString("book name");
    String price = rs.getString("price");
    String genre = rs.getString("genre");
    tableModel.addRow(new Object[]{bid, bookName, price, genre});
}
// Create the JTable with the table model
JTable book_list = new JTable(tableModel);*/
