import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

public class GUI extends JFrame implements ActionListener {
	
	//Define dimensions array
	static String [] dimensionsArray = {
			"8x8", "10x8", "10x10", "12x8", "12x9", "12x12", "14x11", "14x12", "16x8", "16x12", "16x16", "18x12", "20x10", "20x16", "20x20", "24x12", "24x16",  "24x18",  "24x24",  
			"28x20",  "28x28",  "30x15",  "30x20",  "30x30",  "32x16",  "32x24",  "32x32",  "36x24",  "36x36",  "40x20", "40x26", "40x28", "40x30", "40x40", "48x24", "48x32", "48x36", "50x40", "50x50", "60x20", "60x30", "60x40"
			};
	static String [] multiSizes = {"5XLP", "5MXLP", "5XLSP", "3XLP", "4XLP", "5LP", "5MLP", "5LSP", "3LP", "4LP", "5MP", "5MSP", "3MP", "4MP"};
	
	//Initialize buttons
	JButton multiButton;
	JButton countButton;
	JButton floatingCountButton;
	JButton mergeButton;
	
	//Initialize fileTree array
	static File[] fileTree;
	
	//Initialize HashMap used for single panel count
	private static HashMap<String, int[]> singlePanelCount = new HashMap<String, int[]>();
	private static HashMap<String, Integer> multiPanelCount = new HashMap<String, Integer>();
	
	//Initialize global sub-folders to be null
	static File largeFolderThree = null;
	static File largeFolderTwoAndFour = null;
	static File largeFolderOneAndFive = null;
	static File mediumFolderTwoAndFour = null;
	static File mediumFolderOneAndFive = null;
	static File mediumFolderThree = null;
	static File extraLargeFolderThree = null;
	static File extraLargeFolderOneAndFive = null;
	static File extraLargeFolderTwoAndFour = null;
	
	//Initialize floating frame count
	int FLCount;
	
	//Initialize variables needed for fileChooser function
	String filePath;
	String extraLargePath;
	String largePath;
	String mediumPath;
	static File chosenFolder;
	JFileChooser fileChooser;
	int option;
	File folderName;
	
	
	GUI() {
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		this.setTitle("Automation Application");
		
		multiButton = new JButton("Organize Multi Files");
		multiButton.setBounds(200, 100, 100, 50);
		
		countButton = new JButton("Get Count");
		countButton.setBounds(200, 100, 100, 50);
		
		floatingCountButton = new JButton("Get FL Count From Multiple Folders");
		floatingCountButton.setBounds(200, 100, 100, 50);
		
		mergeButton = new JButton("Merge Folders With Same Dimensions");
		mergeButton.setBounds(200, 100, 100, 50);
		
		multiButton.addActionListener(this);
		countButton.addActionListener(this);
		floatingCountButton.addActionListener(this);
		mergeButton.addActionListener(this);

		this.add(countButton);
		this.add(multiButton);
		this.add(floatingCountButton);
		this.add(mergeButton);
		this.setSize(500, 300);
		this.setVisible(true);
		
	}
	
	

	//Ideas: create a button to merge 30x40 and 40x30 and alike situations....
	
	//Concept --> parse filePath, isolate folderName, split at the X in the dimensions (for example(30x40) --> part[0] = 30 and part[1] = 40)
	//Check fileTree[i + 1] and get all the same information
	//if part[0] of fileTree[i] = part[1] of fileTree[i + 1] and part[1] of fileTree[i] = part[0] of fileTree[i + 1]
	//then create new folder with folderName of fileTree[i] and move files from fileTree[i + 1] into that folder (Automatic windows merge should happen)
	
	//Concept --> HashMap that stores {string: string}
	//(30x40) {30:40} 
	//(40x30) {40:30}
	//looping through HashMap if any {key:value} is equal to a {value:key}
	//create new folder with name of {key:value} and move files.contains({value:key} into that folder (Automatic windows merge should happen)
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == mergeButton) {
			/*
			 * 
			fileChooser(fileChooser, option);
		
			for (int i = 0; i < fileTree.length; i++) {
				
				String firstFilePath = fileTree[i].toString();
				String nextFilePath = fileTree[i+1].toString();
				
				String [] partsFirstPath = firstFilePath.split("\\\\");
				String [] partsSecondPath = nextFilePath.split("\\\\");
			
                String fileNameFirstPath = partsFirstPath[partsFirstPath.length-1];
                String folderNameFirstPath = partsFirstPath[partsFirstPath.length-2];
                String [] dimensionPartsFirstPath = folderNameFirstPath.split("x");
                String firstPartFirstPath = dimensionPartsFirstPath[0];
                String secondPartFirstPath = dimensionPartsFirstPath[1];
                
                String fileNameSecondPath = partsSecondPath[partsSecondPath.length-1];
                String folderNameSecondPath = partsSecondPath[partsSecondPath.length-2];
                String [] dimensionPartsSecondPath = folderNameSecondPath.split("x");
                String firstPartSecondPath = dimensionPartsSecondPath[0];
                String secondPartSecondPath = dimensionPartsSecondPath[1];
                //(30x40) and (40x30)
                if (firstPartFirstPath == secondPartSecondPath && secondPartFirstPath == firstPartSecondPath) {
                	excCommand("cd " + chosenFolder.toString() + "\n" + "mkdir " + folderNameFirstPath);
                	folderName = new File(folderNameFirstPath);
                	if (fileNameSecondPath.contains(folderNameSecondPath)) {
                		try {
							FileUtils.moveFileToDirectory(fileTree[i+1], folderName, false);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}
                }
			}
			*/
	
			
		}	else if (e.getSource() == floatingCountButton) {
				
				try {
				    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} 
				catch (Exception e1) {
					e1.printStackTrace();
				}
	            JFileChooser multiFileChooser = new JFileChooser();
	            multiFileChooser.setMultiSelectionEnabled(true);
	            multiFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int multiFileOption = multiFileChooser.showOpenDialog(null);
	            if (multiFileOption == JFileChooser.APPROVE_OPTION){
	            	File [] multipleFolders = multiFileChooser.getSelectedFiles();
	            	//Loop through all folders selected
	            	for (int i = 0; i < multipleFolders.length; i++) {
	            		FLCount = 0;
	            		fileTree = listFileTree(multipleFolders[i]);
	            		//Loop through all sub folders in all selected folders
	            		for (File f: fileTree) {
	            			
	            			filePath = f.toString();
	             		   if (filePath.contains("FL") && (filePath.contains("jpg") || filePath.contains("pdf"))) {
	             			   FLCount++;
	             		   }
	             	   }
            		  }
	            	
	            	JOptionPane.showMessageDialog(this, "FL COUNT: " + String.valueOf(FLCount));
	            	
	            	System.out.println(FLCount);
	            }
	            
			
		//If "Get Count" is selected
		//As of now for this to work every file has to be in a folder with a dimension name
		} else if (e.getSource() == countButton) {
				
				fileChooser(fileChooser, option);
				fileTree = listFileTree(chosenFolder);
				for (File f: fileTree) {
					filePath = f.toString();
					
					//CONCEPT --> using singlePanelCount hash table, goal is to store a key = the dimension TO a value = of a array of ints (thickness) 
					//{12x9}: {0.75, 1.5, FL}
                    //{32x24}: {0.75, 1.5, FL}
					
					//Then use a getFileCount(String size) function to index into the hash table and display values in the ArrayList
					
					//Example path
					//"D:\P5 03.11 Evening Fotoba (sunset)\12x9\AD1-WCM7744_12x9.jpg"
				
					String [] parts = filePath.split("\\\\");
	                String fileName = parts[parts.length-1];
	                String folderName = parts[parts.length-2];
	                if (!filePath.contains("Multi")) {
		                if (!singlePanelCount.containsKey(folderName)) {
		                    //If it doesn't contain the key already, make a new array of size 3 for that key
		                    singlePanelCount.put(folderName, new int[]{0,0,0});
		                    
		                } if (Character.isDigit(folderName.charAt(0))) {
			                //filter logic to add counts
		                	int[] intTemp = singlePanelCount.get(folderName);
			               	if (fileName.contains("FL")) {
			               		intTemp[2]++;
			                } else if (fileName.contains("1.5")) {
			                    intTemp[1]++;
			               	} else {
			               		intTemp[0]++;
			                }
			               	singlePanelCount.put(folderName, intTemp);
		                }
	                } else {
	                	getMultiCount(filePath);
	                }
				}
				
				displayCount(chosenFolder);
				
			}
			
		//If "Organize Multi Files" is selected	
			
		 else if (e.getSource() == multiButton) {
				
				fileChooser(fileChooser, option);
				fileTree = listFileTree(chosenFolder);
				for (File f: fileTree) {
					
					filePath = f.toString();
					extraLargePath = (filePath.substring(0,filePath.indexOf("Extra Large") + 11));	
					largePath = (filePath.substring(0,filePath.indexOf("Large") + 5));
					mediumPath = (filePath.substring(0,filePath.indexOf("Medium") + 6));
					
					createFolders(filePath, extraLargePath, largePath, mediumPath);
						
					sortFiles(f, filePath);
					
					getMultiCount(filePath);
					
					//Update count for each size and set
					//private HashTable<String, 
					//Extra Large count for each set
					
					
				}
					//Create count.txt and display count for each size and set
				
					 
			} else {
				System.out.println("Open command canceled");
			}
	}
	
	
	public static void fileChooser(JFileChooser fileChooser, int option) {
		try {
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		option = fileChooser.showOpenDialog(null);
		if(option == JFileChooser.APPROVE_OPTION) {
			chosenFolder = fileChooser.getSelectedFile();
			
		}
	}
	
	 public static void displayCount(File folder) {
		 	//Create frame and models for table
		 
		    JFrame frame = new JFrame("Count");
		    
		    DefaultTableModel tableModel = new DefaultTableModel();
		    DefaultTableModel tableMultiModel = new DefaultTableModel();
		    DefaultTableModel totalCountModel = new DefaultTableModel();
		    
		    JTable tableSingle = new JTable(tableModel);
		    JTable tableMulti = new JTable(tableMultiModel);
		    JTable tableTotalCount = new JTable(totalCountModel);
		    
		    JTableHeader header1 = tableSingle.getTableHeader();
		    header1.setBackground(Color.yellow);
		    JTableHeader header2 = tableTotalCount.getTableHeader();
		    header2.setBackground(Color.yellow);
		    JTableHeader header3 = tableMulti.getTableHeader();
		    header3.setBackground(Color.yellow);
		    
		    tableMulti.setRowHeight(70);
		    tableTotalCount.setRowHeight(70);
		    tableSingle.setRowHeight(70);
		    
		    tableMultiModel.addColumn("Multi Panel Sizes");
		    tableMultiModel.addColumn("Quantities Of Panels");
		    
		    
		    tableModel.addColumn("Dimension");
		    tableModel.addColumn("0.75");
		    tableModel.addColumn("1.5");
		    tableModel.addColumn("FL");
		    
		    totalCountModel.addColumn("0.75 Count");
		    totalCountModel.addColumn("1.5 Count");
		    totalCountModel.addColumn("FL Count");
		    totalCountModel.addColumn("Total Count");
		    
		    int firstSizeCount = 0;
		    int secondSizeCount = 0;
		    int thirdSizeCount = 0;
		    int totalMultiCount = 0;
		    
		    tableSingle.getTableHeader().setResizingAllowed(true);
		    
		    int totalSingleCount = 0;
		    int zeroPointSevenFiveCount = 0;
		    int onePointFiveCount = 0;
		    int FLCount = 0;
		    int totalCount; 
		    
		    
		    //Sort the keySet so the sizes are in order
		    List<String> sortedKeySet = new ArrayList<>(singlePanelCount.keySet());
		    Collections.sort(sortedKeySet);
		    
		    //Loop through the sortedKeySet and add up all the panels with similar thickness then add rows for each size and thickness'
		    for (String key : sortedKeySet) {
			      int[] temp = singlePanelCount.get(key);
			      int i;
			      for (i = 0; i < temp.length; i++) {
			        firstSizeCount = temp[0];
			        secondSizeCount = temp[1];
			        thirdSizeCount = temp[2];
			        totalSingleCount += temp[i];
			      } 
			      for (i = 0; i < temp.length; i += 3)
			        zeroPointSevenFiveCount += temp[i]; 
			      for (i = 1; i < temp.length; i += 2)
			        onePointFiveCount += temp[i]; 
			      for (i = 2; i < temp.length; i += 3)
			        FLCount += temp[i]; 
			      
			      tableModel.addRow(new Object[] { key, firstSizeCount, secondSizeCount, thirdSizeCount});
			    } 
		    
		    //Loop through multiPanelCOunt and add row with each size and amount
		    for (String key: multiPanelCount.keySet()) {
				   int tmp = multiPanelCount.get(key);
				   totalMultiCount+=tmp;
				   tableMultiModel.addRow(new Object[] { key, tmp });
				   
			   }
		    
		    totalCount = totalSingleCount + totalMultiCount;
		    totalCountModel.addRow(new Object[] { zeroPointSevenFiveCount, onePointFiveCount, FLCount, totalCount});
		    
		    Font myFont = new Font("Calibiri", 0, 14);
		    
		    //Makes the tables fit nicely in the frame
		    
		    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableMulti, tableSingle);
		    JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, tableTotalCount);
		    
		    splitPane.add(new JScrollPane(tableSingle));
		    splitPane.add(new JScrollPane(tableMulti));
		    splitPane2.add(new JScrollPane(tableTotalCount));
		    
		    
		    tableSingle.setFont(myFont);
		    tableTotalCount.setFont(myFont);
		    
		    frame.setDefaultCloseOperation(3);
		    frame.add(splitPane2);
		    frame.setSize(800, 300);
		    frame.pack();
		    frame.setVisible(true);
		    
		  }
	
	public static void getMultiCount(String filePath) {
			//Loop through array of possible multi panel sizes, if the hashMap doesn't contain a size then put that size in hashMap with value of 0.
			//Then if a file contains a size in the multiSizes array (key) then add 1 to the amount of files (value)
            if (filePath.contains("Multi")) {
            	for (String size: multiSizes) {
            		if (!multiPanelCount.containsKey(size)) {
            			multiPanelCount.put(size, 0);
            		}
            		int tmp = multiPanelCount.get(size);
                 	if (filePath.contains(size)) {
                 		tmp++;
	            	}
                 	multiPanelCount.put(size, tmp);
                }
            	
            }
		}

	public static void sortFiles(File f, String filePath) {
		
		if (Stream.of("5LP3", "4LP2", "4LP3", "5LSP3", "5MLP1","5MLP2", "5MLP3", "5MLP4", "5MLP5", "5LP(3)", "4LP(2)", "4LP(3)", "5LSP(3)", "5MLP(1)","5MLP(2)", "5MLP(3)", "5MLP(4)", "5MLP(5").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, largeFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (Stream.of("4LP1", "4LP4", "3LP1", "3LP2", "3LP3","5LSP1", "5LSP2", "5LSP4", "5LSP5", "5LP2", "5LP4", "4LP(1)", "4LP(4)", "3LP(1)", "3LP(2)", "3LP(3)","5LSP(1)", "5LSP(2)", "5LSP(4)", "5LSP(5)", "5LP(2)", "5LP(4)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, largeFolderTwoAndFour, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
		} else if (Stream.of("5LP1", "5LP5", "5LP(1)", "5LP(5)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, largeFolderOneAndFive, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
		//Extra Large folder file to folders sorting logic
				
		} else if (Stream.of("5XLP3", "4XLP2", "4XLP3", "5XLSP3", "5MXLP1","5MXLP2", "5MXLP3", "5MXLP4", "5MXLP5", "5XLP(3)", "4XLP(2)", "4XLP(3)", "5XLSP(3)", "5MXLP(1)","5MXLP(2)", "5MXLP(3)", "5MXLP(4)", "5MXLP(5)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, extraLargeFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (Stream.of("4XLP1", "4XLP4", "3XLP1", "3XLP2", "3XLP3","5XLSP1", "5XLSP2", "5XLSP4", "5XLSP5", "5XLP2", "5XLP4", "4XLP(1)", "4XLP(4)", "3XLP(1)", "3XLP(2)", "3XLP(3)","5XLSP(1)", "5XLSP(2)", "5XLSP(4)", "5XLSP(5)", "5XLP(2)", "5XLP(4)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, extraLargeFolderTwoAndFour, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
		} else if (Stream.of("5XLP1", "5XLP5", "5XLP(1)", "5XLP(5)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, extraLargeFolderOneAndFive, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	
		//Medium Folder file to folders sorting logic (no MEGA MEDIUM)
				
		} else if (Stream.of("5MP3", "4MP2", "4MP3", "5MSP3", "5MP(3)", "4MP(2)", "4MP(3)", "5MSP(3)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, mediumFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		} else if (Stream.of("4MP1", "4MP4", "3MP1", "3MP2", "3MP3","5MSP1", "5MSP2", "5MSP4", "5MSP5", "5MP2", "5MP4", "4MP(1)", "4MP(4)", "3MP(1)", "3MP(2)", "3MP(3)", "5MSP(1)", "5MSP(2)", "5MSP(4)", "5MSP(5)", "5MP(2)", "5MP(4)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, mediumFolderTwoAndFour, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
		} else if (Stream.of("5MP1", "5MP5", "5MP(1)", "5MP(5)").anyMatch(filePath::contains)) {
				try {
					FileUtils.moveFileToDirectory(f, mediumFolderOneAndFive, false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}
	
	//File Tree
	public static File[] listFileTree(File directory) {
		
	    if(directory==null||directory.listFiles()==null){
	        return fileTree;
	    }
	    
	    for (File f : directory.listFiles()) {
	        if (f.isFile()) { 
	        	fileTree = ArrayUtils.add(fileTree, f);
	        }
	        else {
	        	listFileTree(f);
	        }
	    }
	    return fileTree;
	}
	
	//Function to execute a command in command prompt
	public static void excCommand(String cmd){
			String[] command = {"cmd",};
			Process p;
			try {
				p = Runtime.getRuntime().exec(command);
				new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
				new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
				PrintWriter stdin = new PrintWriter(p.getOutputStream());
				stdin.println(cmd);
	            stdin.close();
	            p.waitFor();
			} catch (Exception e) {
		 		e.printStackTrace();
			}
		}
	
	
	//If any file/directory contains Extra Large, Large, or Medium and the sub-folders don't exist, then store the directories of the sub-folders and create the sub-folders with command prompt
	public static void createFolders(String filePath, String extraLargePath, String largePath, String mediumPath) {
		
		if (filePath.contains("Extra Large") && extraLargeFolderThree == null && extraLargeFolderOneAndFive == null && extraLargeFolderTwoAndFour == null) {
			extraLargeFolderThree = new File(extraLargePath + "\\3");
			extraLargeFolderTwoAndFour = new File(extraLargePath + "\\2 and 4");
			extraLargeFolderOneAndFive = new File(extraLargePath + "\\1 and 5");
			excCommand("cd " + extraLargePath + "\n" + "mkdir 3");
			excCommand("cd " + extraLargePath + "\n" + "mkdir \"2 and 4\"");
			excCommand("cd " + extraLargePath + "\n" + "mkdir \"1 and 5\"");
			excCommand("exit");	
		}
		if (filePath.contains("Large") && !filePath.contains("Extra Large") && largeFolderThree == null && largeFolderTwoAndFour == null && largeFolderOneAndFive == null) {
			largeFolderThree = new File(largePath + "\\3"); 
			largeFolderTwoAndFour = new File(largePath + "\\2 and 4");
			largeFolderOneAndFive = new File(largePath + "\\1 and 5");
			excCommand("cd " + largePath + "\n" + "mkdir 3");
			excCommand("cd " + largePath + "\n" + "mkdir \"2 and 4\"");
			excCommand("cd " + largePath + "\n" + "mkdir \"1 and 5\"");
			excCommand("exit");
		}
		else if (filePath.contains("Medium") && mediumFolderThree == null && mediumFolderTwoAndFour == null && mediumFolderOneAndFive == null) {
			mediumFolderThree = new File(mediumPath + "\\3");
			mediumFolderTwoAndFour = new File(mediumPath + "\\2 and 4");
			mediumFolderOneAndFive = new File(mediumPath + "\\1 and 5");
			excCommand("cd " + mediumPath + "\n" + "mkdir 3");	
			excCommand("cd " + mediumPath + "\n" + "mkdir \"2 and 4\"");
			excCommand("cd " + mediumPath + "\n" + "mkdir \"1 and 5\"");
			excCommand("exit");
		}
	}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	