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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.html.HTMLDocument.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class GUI extends JFrame implements ActionListener {

	//Define dimensions array
	static String [] dimensionsArray = {
			"8x8", "10x8", "10x10","12x8","12x9","12x12","14x11","14x12","16x8","16x12","16x16","18x12","20x10","20x16","20x20","24x12","24x16", "24x18","24x24",
			"28x20","28x28","30x15","30x20","30x30","32x16","32x24","32x32","36x24","36x36","40x20","40x26","40x28","40x30","40x40","48x24","48x32","48x36","50x40","50x50","60x20","60x30","60x40"
	};
	static String [] multiSizes = {"5XLP", "5MXLP", "5XLSP", "3XLP", "4XLP", "5LP", "5MLP", "5LSP", "3LP", "4LP", "5MP", "5MSP", "3MP", "4MP"};

	//Initialize buttons
	JButton multiButton;
	JButton countButton;
	JButton floatingCountButton;
	JButton mergeButton;

	//Initialize fileTree array
	static File[] fileTree;

	//Initialize HashMaps used for single panel count, multi count
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

	//Initialize floating frame counts
	static int blackFLCount;
	static int goldFLCount;
	static int whiteFLCount;
	static int extraLargeGoldFLCount;
	static int totalFLCount;

	//Initialize myFont
	static Font myFont = new Font("Calibiri", 0, 14);

	//Initialize variables needed for fileChooser function
	String filePath;
	String extraLargePath;
	String largePath;
	String mediumPath;
	static File chosenFolder;
	JFileChooser fileChooser;
	int option;
	File folderName;
	File tmpFile;

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


		this.setSize(500, 300);
		this.setVisible(true);

	}




	@Override
	public void actionPerformed(ActionEvent e) {

		/*
		if (e.getSource() == mergeButton) {


			fileChooser(fileChooser, option);
			fileTree = listFileTree(chosenFolder);
			for (File f: fileTree) {

				String filePath = f.toString();

				String [] parts = filePath.split("\\\\");

				String folderName = parts[parts.length - 2];


				if (folderName.contains("X")) {
					folderName = folderName.replace("X", "x");
				}
				for (String dimension: dimensionsArray) {
					if (!folderName.contains(dimension)) {
						System.out.println(folderName);
					}
				}
			}

		}



		*/
		if (e.getSource() == floatingCountButton) {

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

					blackFLCount = 0;
					goldFLCount = 0;
					whiteFLCount = 0;
					extraLargeGoldFLCount = 0;
					totalFLCount = 0;

					fileTree = listFileTree(multipleFolders[i]);

					//Loop through all sub folders in all selected folders
					for (File f: fileTree) {
						filePath = f.toString();

						//Get count for all FL, Gold FL, White FL, and Black FL
						if (filePath.contains("FL")) {
							totalFLCount++;
							if (filePath.contains("WFL")) {
								whiteFLCount++;
							} else if (filePath.contains("GFL")) {
								goldFLCount++;
							} else {
								blackFLCount++;
							}
						}
						//Specific count for XL GFL
						if (filePath.contains("GFL") && (Stream.of("1XLP", "48X36", "36X48", "48x36", "36x48").anyMatch(filePath::contains))) {
							extraLargeGoldFLCount++;
						}
					}
				}
				displayFLCount();
			}


			//If "Get Count" is selected
		} else if (e.getSource() == countButton) {

			fileChooser(fileChooser, option);
			fileTree = listFileTree(chosenFolder);
			for (File f: fileTree) {
				filePath = f.toString();

				//CONCEPT --> using singlePanelCount hash table, goal is to store a key = the dimension TO a value = of a array of ints (thickness)
				//{12x9}: {0.75, 1.5, FL}
				//{32x24}: {0.75, 1.5, FL}

				//Then use a getFileCount(String dimension) function to index into the hash table and display values

				//Example path
				//"D:\P5 03.11 Evening Fotoba (sunset)\12x9\AD1-WCM7744_12x9.jpg"
				//"C:\Users\Miles Davis\Desktop\testing\P5 03.11 Evening Fotoba (sunset)\12x9\AB1_164486_12x9.jpg"
				//"C:\Users\Miles Davis\Desktop\testing\P5 03.11 Evening Fotoba (sunset)\32x24\FL_HDF116840_24x32_0001.jpg"

				String [] parts = filePath.split("\\\\");
				String fileName = parts[parts.length-1];

				//Handle edge cases where fileName doesn't contain actual dimension
				if (Stream.of("1XLP1", "1LP1", "1SP1", "1MP1").anyMatch(filePath::contains)) {

					fileName = fileName.replace("1XLP1", "48x36");
					fileName = fileName.replace("1LP1", "32x24");
					fileName = fileName.replace("1SP1", "16x12");
					fileName = fileName.replace("1MP1", "24x18");

				}

				//Force all fileNames to have a small x (for example --> 30x40 vs 30X40) for simplicity
				if (fileName.contains("X")) {
					String tmpFileName = fileName.replace("X", "x");
					fileName = tmpFileName;
				}

				//Regex used to parse dimension from fileName
				String dimension = null;
				Pattern pattern = Pattern.compile("\\d+x\\d+");
				Matcher matcher = pattern.matcher(fileName);

				if (matcher.find()) {
					dimension = matcher.group(0);
				}
				

				//Loop through array of all dimensions, store the swapped dimensions in a variable (for example --> 12x9 vs 9x12)
				//if the dimension contains a swapped dimensions swap that specific dimension back to dimensionInArray and replace that fileName with that dimension
				if (dimension != null) {
					for (String size: dimensionsArray) {
						String [] dimensionParts = size.split("x");
						String swappedDimension = dimensionParts[1] + "x" + dimensionParts[0];

						if (dimension.equals(swappedDimension)) {
							String dimensionInArray = dimensionParts[0] + "x" + dimensionParts[1];
							dimension = dimension.replace(swappedDimension, dimensionInArray);
						}
					}
				}

				//Put sizes in hashMap with int array of thickness
				if (dimension != null) {
						if (!singlePanelCount.containsKey(dimension)) {
							singlePanelCount.put(dimension, new int[] {0,0,0});
						}
						//Logic to change the count for each individual thickness for each key in the hashMap
						int[] intTemp = singlePanelCount.get(dimension);
						if (fileName.contains("FL")) {
							intTemp[2]++;
						} else if (fileName.contains("1.5")) {
							intTemp[1]++;
						} else {
							intTemp[0]++;
						}
						singlePanelCount.put(dimension, intTemp);
					}
				getMultiCount(filePath);
			}
			displayCount();
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
			}

		} else {
			System.out.println("Open command canceled");
		}
	}

	//Choose directory through JFileChooser
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

	//Display each FL Count in the form of a JTable
	public static void displayFLCount() {
		JFrame frame = new JFrame("FL Count");

		DefaultTableModel tableFLModel = new DefaultTableModel();

		JTable tableFL = new JTable(tableFLModel);

		JTableHeader header1 = tableFL.getTableHeader();
		header1.setBackground(Color.yellow);

		tableFL.setRowHeight(70);

		tableFLModel.addColumn("Black FL Count");
		tableFLModel.addColumn("Gold FL Count");
		tableFLModel.addColumn("White FL Count");
		tableFLModel.addColumn("Gold FL - 48x36 Count");
		tableFLModel.addColumn("Total FL Count");

		tableFLModel.addRow(new Object[] { blackFLCount, goldFLCount, whiteFLCount, extraLargeGoldFLCount, totalFLCount});

		tableFL.setFont(myFont);
		frame.add(new JScrollPane(tableFL));
		frame.setDefaultCloseOperation(3);
		frame.setSize(800, 300);
		frame.pack();
		frame.setVisible(true);

	}

	//Displays count for multi-panels and single-panels
	public static void displayCount() {

		JFrame frame = new JFrame("Count");

		DefaultTableModel tableSingleModel = new DefaultTableModel();
		DefaultTableModel tableMultiModel = new DefaultTableModel();
		DefaultTableModel totalCountModel = new DefaultTableModel();

		JTable tableSingle = new JTable(tableSingleModel);
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
		tableMultiModel.addColumn("Sets");
		tableMultiModel.addColumn("Quantities Of Panels");

		tableSingleModel.addColumn("Dimension");
		tableSingleModel.addColumn("0.75");
		tableSingleModel.addColumn("1.5");
		tableSingleModel.addColumn("FL");

		totalCountModel.addColumn("0.75 Count");
		totalCountModel.addColumn("1.5 Count");
		totalCountModel.addColumn("FL Count");
		totalCountModel.addColumn("Total Multi Panels");
		totalCountModel.addColumn("Total Count");

		//Initialize counts for single panel thickness'
		int firstSizeCount = 0;
		int secondSizeCount = 0;
		int thirdSizeCount = 0;
		int totalMultiCount = 0;

		tableSingle.getTableHeader().setResizingAllowed(true);

		//Initialize count for total count of each thickness
		int totalSingleCount = 0;
		int zeroPointSevenFiveCount = 0;
		int onePointFiveCount = 0;
		int FLCount = 0;
		int totalCount;

		//Sort the keySet so the dimensions are in order
		List<String> sortedKeySet = new ArrayList<>(singlePanelCount.keySet());
		Collections.sort(sortedKeySet);

		//Loop through the sortedKeySet and add up all the panels with similar thickness then add rows for each dimension and thickness'
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

			tableSingleModel.addRow(new Object[] { key, firstSizeCount, secondSizeCount, thirdSizeCount});
		}

		//Loop through multiPanelCount and add row with each dimension and amount
		for (String key: multiPanelCount.keySet()) {
			int value  = multiPanelCount.get(key);
			char set = 0;
			for (int i = 0; i < key.length(); i++) {
				set = key.charAt(0);
			}
			int sets = value / Character.getNumericValue(set);
			totalMultiCount+=value;
			tableMultiModel.addRow(new Object[] { key, sets, value });
		}

		totalCount = totalSingleCount + totalMultiCount;
		totalCountModel.addRow(new Object[] { zeroPointSevenFiveCount, onePointFiveCount, FLCount, totalMultiCount, totalCount});

		//Makes the tables fit nicely in the frame
		//SplitPane can only handle two components so must use 2 splitPanes to fit three components in one frame
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableMulti, tableSingle);
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane, tableTotalCount);

		splitPane.add(new JScrollPane(tableSingle));
		splitPane.add(new JScrollPane(tableMulti));
		splitPane2.add(new JScrollPane(tableTotalCount));

		tableSingle.setFont(myFont);
		tableTotalCount.setFont(myFont);
		tableMulti.setFont(myFont);

		frame.setDefaultCloseOperation(3);
		frame.add(splitPane2);
		frame.setSize(800, 300);
		frame.pack();
		frame.setVisible(true);

	}

	public static void getMultiCount(String filePath) {
		//Loop through array of possible multi panel sizes
		//If the hashMap doesn't contain a size then put that size in hashMap with value of 0
		for (String size: multiSizes) {
			if (filePath.contains(size)) {
				if (!multiPanelCount.containsKey(size)) {
					multiPanelCount.put(size, 0);
				}
				int tmp = multiPanelCount.get(size);
				tmp++;
				multiPanelCount.put(size, tmp);
			}
		}
	}


	public static void sortFiles(File f, String filePath) {

		if (Stream.of("5LP3", "4LP2", "4LP3", "5LSP3", "5MLP1","5MLP2", "5MLP3", "5MLP4", "5MLP5", "5LP  (3)", "4LP  (2)", "4LP  (3)", "5LSP  (3)", "5MLP  (1)","5MLP  (2)", "5MLP  (3)", "5MLP  (4)", "5MLP  (5)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, largeFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (Stream.of("4LP1", "4LP4", "3LP1", "3LP2", "3LP3","5LSP1", "5LSP2", "5LSP4", "5LSP5", "5LP2", "5LP4", "4LP (1)", "4LP (4)", "3LP (1)", "3LP (2)", "3LP (3)","5LSP (1)", "5LSP (2)", "5LSP (4)", "5LSP (5)", "5LP (2)", "5LP (4)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, largeFolderTwoAndFour, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else if (Stream.of("5LP1", "5LP5", "5LP (1)", "5LP (5)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, largeFolderOneAndFive, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//Extra Large folder file to folders sorting logic

		} else if (Stream.of("5XLP3", "4XLP2", "4XLP3", "5XLSP3", "5MXLP1","5MXLP2", "5MXLP3", "5MXLP4", "5MXLP5", "5XLP (3)", "4XLP (2)", "4XLP (3)", "5XLSP (3)", "5MXLP (1)","5MXLP (2)", "5MXLP (3)", "5MXLP (4)", "5MXLP (5)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, extraLargeFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (Stream.of("4XLP1", "4XLP4", "3XLP1", "3XLP2", "3XLP3","5XLSP1", "5XLSP2", "5XLSP4", "5XLSP5", "5XLP2", "5XLP4", "4XLP (1)", "4XLP (4)", "3XLP (1)", "3XLP (2)", "3XLP (3)","5XLSP (1)", "5XLSP (2)", "5XLSP (4)", "5XLSP (5)", "5XLP (2)", "5XLP (4)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, extraLargeFolderTwoAndFour, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else if (Stream.of("5XLP1", "5XLP5", "5XLP (1)", "5XLP (5)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, extraLargeFolderOneAndFive, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			//Medium Folder file to folders sorting logic (no MEGA MEDIUM)

		} else if (Stream.of("5MP3", "4MP2", "4MP3", "5MSP3", "5MP (3)", "4MP (2)", "4MP (3)", "5MSP (3)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, mediumFolderThree, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else if (Stream.of("4MP1", "4MP4", "3MP1", "3MP2", "3MP3","5MSP1", "5MSP2", "5MSP4", "5MSP5", "5MP2", "5MP4", "4MP (1)", "4MP (4)", "3MP (1)", "3MP (2)", "3MP (3)", "5MSP (1)", "5MSP (2)", "5MSP (4)", "5MSP (5)", "5MP (2)", "5MP (4)").anyMatch(filePath::contains)) {
			try {
				FileUtils.moveFileToDirectory(f, mediumFolderTwoAndFour, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} else if (Stream.of("5MP1", "5MP5", "5MP (1)", "5MP (5)").anyMatch(filePath::contains)) {
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
				//Recursively search file tree until there are no more files left to search
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












