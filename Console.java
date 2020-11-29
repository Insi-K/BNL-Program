package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console 
{
	
	
	public static void main (String[] args) throws IOException//For the CreateReadFile method. See method below.
	{
		App();
	}
	
	//Here are the variables used in the program.
	/*PD: a "Finch" instance for the robot was made here in the main project:
	public Finch robot; //In order for this to work, a specific library for the finch must be imported to the project.[NOT in use in this version]*/
	
	public static char cmd = 0;//Single character variable used to trigger a specific command/task.
	
	public static boolean syn = false;//If true, it triggers the RunCommand method. See method below.
	public static boolean retrace = false;//Prevents the variable n from changing while Retrace method is running. See method below.
	public static boolean synMsg = true;//Avoids showing a syntax error message when error is found within input. See InCommand method below.
	
	public static ArrayList <String> history = new ArrayList <String> ();//ArrayList used to store all the commands run by the user during the session.
	
	public static int t = 0;//Stores the time given by user (seconds).
	public static int tms = 0;//Stores the time given by user (milliseconds). This is the variable that would be used in the Finch methods (robot).
	public static int v = 0;//Stores the speed given by user. Used for the robot's movement;
	public static int n = 0;//Stores the number of moves that should be scanned from history. Input given by the user. See Retrace method below.
	
	public static String logFile = "LogFile.txt";//Here you can specify a location where you want to create the file. Add the path: "\\path\\<name>.txt".
	public static String readFile = "ReadFile.txt";//Here you can specify the file's location. Add the path: "\\path\\<name>.txt".
	
	public static String commandLine = null;//Variable used to scan commands from readFile (ReadFile.txt or any text file you stored the commands). See Execute method below.
	
	public static JFrame frame;//New java window.
	public static JTextPane console;//New console (User output display).
	public static JTextField input;//New input (text bar).
	public static JScrollPane scroll;//Camera used for scrolling function.
	public static StyledDocument document;//Document used to display the output to user in the console window.
	
	public static void App() throws IOException//For the CreateReadFile method. See method below.
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//Makes the app look like a Windows app and not like java's.
		}
		catch(Exception ex) {}
		
		frame = new JFrame();//Creates the console window.
		frame.setTitle("Navigation Console [1.2]");//Sets the title of the application.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//This makes sure the user can exit the program.
		
		console = new JTextPane();//This component will display the program output for the user.
		console.setEditable(false);//The panel will only show output. User will not be able to write on it.
		console.setFont(new Font("Consolas",Font.PLAIN,12));//Sets the font for the console.
		console.setOpaque(false);//Makes the background transparent. So only the frame color is shown.
		
		document = console.getStyledDocument();//this object will display the text output to the console.
		
		input = new JTextField();//Small bar situated at the bottom where the user will type the commands.
		input.setEditable(true);//user should be able to write on it.
		input.setFont(new Font("Consolas",Font.PLAIN,12));//Sets the font for the input.
		input.setForeground(Color.WHITE);//Sets text color.
		input.setCaretColor(Color.WHITE);//Sets cursor color(This "|" thing that appears when writing text.).
		input.setOpaque(false);
		
		scroll = new JScrollPane(console);//Creates a scroll panel and includes the console as it is the component where the text will be displayed.
		scroll.setBorder(null);//Erases any borders of the panel.
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);//The viewport is the "Camera" of the console.
		
		frame.add(input, BorderLayout.SOUTH);//Add input to the bottom of window.
		frame.add(scroll, BorderLayout.CENTER);//Add console and camera to center of window.
		
		frame.getContentPane().setBackground(new Color(50,50,50));//Sets the console color.
		
		frame.setSize(850, 500);//sets window size.
		frame.setLocationRelativeTo(null);//window will show in center.
		
		frame.setResizable(false);//Prevents the user from changing the window size.
		frame.setVisible(true);//opens the app.
		Println("Navigation Console [Version 1.2]");
		Println("(c) 2019 Paul Flores[1914366] - Brunel University London: Department of Computer Science. All rights reserved.");
		//Create a new instance of the robot object before the program starts. This prevents the app from running without a robot connected [NOT in use in this version].
		CreateReadFile();
		Print("\nUser:/>");//Line where user input will be displayed.
		
		input.addActionListener(new ActionListener() {//an action is performed when enter key is pressed. This will have the same effect as a loop.
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String command = input.getText();
				
				if(command.length() > 0)
				{
					DoCommand(command);//Command that will say if the input is processed, or the program terminates. See method.
					ScrollBottom();
					input.setText("");
					Print("\nUser:/>");
				}
			}
		});
	}
	
	public static void ScrollBottom()
	{
		console.setCaretPosition(console.getDocument().getLength());//the program will go to last line displayed when text output is bigger than frame size.
	}
	
	public static void Print(String text, Color c) //Main console text output method.
	{
		javax.swing.text.Style style = console.addStyle("Style", null);//creates a plain style.
		StyleConstants.setForeground(style, c);//changes the text color on console.
		try
		{
			document.insertString(document.getLength(), text, style);//Prints a new string in the last line in the style defined(color).
		}
		catch(Exception ex) {}
	}
	
	public static void Print(String text)//Default print method.
	{
		Print(text, new Color(255,255,255));
	}
	
	public static void Println(String text)//Default println method.
	{
		Print(text+"\n", new Color(255,255,255));
	}
	
	public static void Println(String text, Color c)//println method + text color. Used in error and command outputs
	{
		Print(text+"\n", c);
	}
	
	public static void Clear()//This method cleans the document where the output is displayed.
	{
		try
		{
			document.remove(0, document.getLength());//Removes everything in the console.
			Println("Navigation Console [Version 1.2]");//Prints again the starting lines to keep style.
			Println("(c) 2019 Paul Flores[1914366] - Brunel University London: Department of Computer Science. All rights reserved.");
		}
		catch(Exception ex) {}
	}
	
	public static void CreateReadFile() throws IOException //Creates an empty text file if the user wants to write commands on it for execution. 
	{
		File file = new File(readFile);
		file.createNewFile();
	}
	
	public static void DoCommand(String cmd)//This method performs a specific command based on the user's input.
	{
		final String[] inCmd = cmd.split(" ");//Splits the input when a space is found. Helps run different methods depending on each part of the main String (cmd variable).
		
		try 
		{
			if(inCmd[0].equals("clear"))//if statements ONLY check on the first part of the input. Example: cmd = "F 2 200" (The program checks only the letter).
			{
				Clear();
			}
			else if(inCmd[0].equals("Q"))
			{
				Println("");
				Println("\nProgram Terminates");
				//Terminate the robot's instance [NOT in use in this version]
				System.exit(1);//Closes the java window.
			}
			else if(inCmd[0].length() == 1)
			{
				Println(cmd+"\n", new Color(155, 255, 255));//Prints user input to console
				InCommand(cmd);//Sets the correct input variables.
				CheckCommand();//Checks the entire input.
				if(syn == true)
				{
					RunCommand(commandLine);//See method below.
					Reset();//resets variables to avoid a bug found after running 1 command and having a SYN error.
				}
			}
			else 
			{
				Println(cmd+"\n", new Color(155, 255, 255));//Prints user input to console
				syn = false;
				Println("Syntax error. Invalid input.", new Color(255,51,51));//SYN errors will display in red.
				Println("For help, please enter H to see available commands", new Color(255,51,51));
			}
		}
		catch(Exception ex) {}
	}
	
	public static void InCommand(String input)//Method used to set the correct input. Receives user input from the console window.
	{
		try //Allows the program to run smoothly even if there is an input error.
		{
			commandLine = input;//Sets the command equal to the input from history/file.
			Scanner cmdlnScan = new Scanner(commandLine);//Scan last input stored in variable.
			
			cmd = cmdlnScan.next().charAt(0);//Stores the first parameter from the input.
			if(cmd == 'F' || cmd == 'B' || cmd == 'L' || cmd == 'R')
			{
				t = cmdlnScan.nextInt();//Second parameter from input. This parameter will be used as reference for input and screen output.
				tms = t*1000; //converts the time in seconds to milliseconds. This parameter will be used for the robot methods. [NOT in use in this version]
				v = cmdlnScan.nextInt();//Third parameter from input.
			}
			else if(cmd == 'T' && retrace == false) // only triggered if Retrace is not active.
			{
				n = cmdlnScan.nextInt();//Second parameter from input.
			}
			synMsg = true;//If true an input error must be displayed. See CheckCommand method below.
		}
		catch(Exception e)//Used to avoid any errors from input. Error messages will display in red.
		{
			synMsg = false;//Prevents printing the SYN error message as another error type is found.
			Println("\nInvalid input. The value entered may not be entered in the correct order or is not allowed.", new Color(255,51,51));
			Println("For more help on how to input a command just enter \'H\'",new Color(255,51,51));
		}
		
	}
	
	public static void CheckCommand() //Checks if the input is correct. Else it will ask again for a valid command.
	{
		if (cmd == 'F' || cmd == 'B' || cmd == 'L' || cmd == 'R')
		{
			if(0<t && t<=6 && 40<=v && v<=200)// 40 is the minimum speed so that the robot runs with no issues. Lower numbers won't work with the robot or it will not respond well.
			{
				syn = true;
			}
			else if(synMsg == true)
			{
				syn = false;
				Println("Syntax error. Invalid input.", new Color(255,51,51));//SYN errors will display in red.
				Println("For help, please enter H to see available commands", new Color(255,51,51));
			}
		}
		else if(cmd == 'T')
		{
			if(0<n && n<=history.size())
			{
				syn = true;
			}
			else if(synMsg == true)
			{
				syn = false;
				Println("Syntax error. Invalid input.", new Color(255,51,51));
				Println("For help, please enter H to see available commands", new Color(255,51,51));
			}
		}
		else if(cmd == 'W' || cmd == 'X' || cmd == 'H')
		{
			syn = true;
		}
		else if(cmd != 'Q') // Q is also a valid command, but it will not trigger the RunCommnand method.
		{
			syn = false;
			Println("Syntax error. Invalid input.",new Color(255,51,51));
			Println("For help, please enter H to see available commands.",new Color(255,51,51));
		}
	}
	
	public static void RunCommand(String command) throws IOException //The parameter <<command>> lets me reuse the method with the other ones without errors (retrace and execute).
	{
		switch (cmd)//Processes the command character assigned on cmd. See InCommand method.
		{
			case 'F':
				Println("Command \'Forward\' received.");//Each command will display details of the action performed.
				//robot.setLED(0, 100, 0);//The robot will light a single color based on the command run. [NOT in use in this version]
				//robot.setWheelVelocities(v, v, tms);//The robot moves at a certain speed on each wheel during a certain amount of time in milliseconds. [NOT in use in this version]
				Println(" Duration: "+t+"s");
				Println(" Speed: "+v);
				history.add(command);//adds the command to history
				break;
			case 'B':
				Println("Command \'Backward\' received.");
				//robot.setLED(100, 0, 0); [NOT in use in this version]
				//robot.setWheelVelocities(-v, -v, tms); [NOT in use in this version]
				Println(" Duration: "+t+"s");
				Println(" Speed: "+v);
				history.add(command);
				break;
			case 'L':
				Println("Command \'Left\' received.");//For orthogonal turn Use parameters t=1 and v=75.
				//robot.setLED(100, 100, 0); [NOT in use in this version]
				//robot.setWheelVelocities(-v, v, tms); [NOT in use in this version]
				Println(" Duration: "+t+"s");
				Println(" Speed: "+v);
				history.add(command);
				break;
			case 'R':
				Println("Command \'Right\' received.");//For orthogonal turn Use parameters t=1 and v=75.
				//robot.setLED(100, 100, 0); [NOT in use in this version]
				//robot.setWheelVelocities(v, -v, tms); [NOT in use in this version]
				Println(" Duration: "+t+"s");
				Println(" Speed: "+v);
				history.add(command);
				break;
			case 'T':
				Println("--Scanning last "+n+" commands from history--");
				history.add(command);//Goes before as during the next method, more commands will be added to history.
				Retrace();
				Println("\n--Scanning complete--");
				break;
			case 'W': 
				//robot.setLED(100, 0, 100); [NOT in use in this version]
				Println("Exporting current session to \"LogFile\"");
				Write();
				Println("Session has been successfully saved into \"LogFile.txt\".");//Probably you should add more details on the file location
				history.add(command);
				break;
			case 'X':
				history.add(command);//Goes before as during the next method, more commands will be added to history.
				Execute();
				break;
			case 'H': //Help command will never be added to history. This command will only be used by the user for reference.
				//robot.setLED(0, 100, 100); [NOT in use in this version]
				Help();
				break;
		}
	}
	
	public static void Retrace() throws IOException //This method reads previous commands stored in the history variable during the current session.
	{
		retrace = true; // Avoids changing the variable n while retracing. So whenever T is found in history it will not scan [n].
		String cmdLn = null;
		int hElm = history.size()-2;// Variable used to access a certain element from history.
		for(int i=0; i<n; ++i)
		{
			cmdLn = history.get(hElm);//Temporarily stores X element called from history.
			InCommand(cmdLn);// Sets the correct input from the element.
			Println("");
			if (cmd != 'T' && cmd != 'W' && cmd != 'X' && cmd != 'H')//Only movement commands will be retraced.
			{
				RunCommand(cmdLn);
			}
			else
			{
				Println("A non-movement command has been detected. The command will not be re-traced.");//Non-movement commands will be scanned but not re-traced.
			}
			--hElm;
		}
		retrace = false; //Lets modify N in another method. N will not be modified only during Re-tracing. Reason: The program will still read the input (T [n]), but it will be skipped.
	}
	
	public static void Write() throws IOException //Exports the current session into a text file
	{
		String time = SessionTime(); //Stores the date created by the method. See SessionTime method below.
		FileWriter scanFile = new FileWriter(logFile); //Opens file.
		BufferedWriter bw = new BufferedWriter(scanFile); //Set up file writer.
		bw.write("Navigation Console [Ver. 1.2] - Output");
		bw.newLine();//goes to next line.
		bw.write("Time exported: " + time);
		bw.newLine();
		bw.newLine();
		bw.write("Command history log:");
		bw.newLine();
		for(int i = 0; i<history.size(); ++i )// Exports all the commands stored in history from current session.
		{
			String scanLine = history.get(i);
			bw.write(scanLine);
			bw.newLine();
		}
		bw.close(); //It's important to close always the file reader first.
		scanFile.close(); //It's important to close always the file last.
	}
	
	public static void Execute() throws IOException //This method reads and executes commands from a text file. Works fine. cannot skip lines though.
	{
		try
		{
			FileReader executer = new FileReader(readFile);//Opens file.
			BufferedReader br = new BufferedReader(executer);//Set up file reader.
			Path path = Paths.get(readFile);//Get's access to file info [Class In-built Method].
			long lineCount = Files.lines(path).count();//Counts the number of lines in the file.
			String scanLine = null;
			if(lineCount>=3)
			{
				Println("--Executing commands from \"ReadFile\"--\n");
				for(int i=1; i<=lineCount; ++i)
				{
					scanLine = br.readLine();//Reads the next line.
					InCommand(scanLine);//Sets the correct input from the scanned line.
					CheckCommand();//Checks if the command is valid.
					if(syn = true)
					{
						RunCommand(scanLine);
					}
				}
				Println("\n--Execution complete--");
			}
			else
			{
				Println("File has to contain at least 3 commands.",new Color(255,51,51));
			}
			br.close();// always close first the file reader.
			executer.close();// always close the file opener last.
		}
		catch(Exception e)
		{
			Println("Program Error. \"ReadFile.txt\" could not be found.");//Make sure you create ReadFile.txt before running X.
		}
	}
	
	public static void Help()//Only displays all available commands.
	{
		String tab = "\t";
		Println("Command:"+tab+tab+tab+"Description:");
		Println("----------"+tab+tab+tab+"-------------");
		Println("-F [t] [v]"+tab+tab+tab+"Finch moves forward."+tab+tab+"Max time: 6(s). Speed: [MIN] 40 - [MAX] 200.");
		Println("-B [t] [v]"+tab+tab+tab+"Finch moves Backward."+tab+"Max time: 6(s). Speed: [MIN] 40 - [MAX] 200.");
		Println("-L [t] [v]"+tab+tab+tab+"Finch turns left*."+tab+tab+"Max time: 6(s). Speed: [MIN] 40 - [MAX] 200.");
		Println("-R [t] [v]"+tab+tab+tab+"Finch turns right*."+tab+tab+"Max time: 6(s). Speed: [MIN] 40 - [MAX] 200.");
		Println("-T [N]"+tab+tab+tab+"Re-traces the last [N] commands from \'History\' whithin the current session.");
		Println("-W"+tab+tab+tab+"Exports a summary of the current session into a text file.");
		Println("-X"+tab+tab+tab+"Executes a list of commands from \"RunFile.txt\".");
		Println("-Q"+tab+tab+tab+"Terminates the program.");
		Println("-clear"+tab+tab+tab+"Erases all output displayed to the user in order to improve performance.\n");
		Println("Please note: The program is case sensitive. Make sure you enter the commands in capital letters.");
		Println("*For an orthogonal turn (Right/Left), parameters suggested: time - 1s ; speed - 75.");
	}
	
	public static void Reset() //This method avoids a bug found when entering a void input after the program has run 1 command successfully.
	{
		syn = false;
		t = 0;
		tms = 0;
		n = 0;
		v = 0;
	}
	
	public static String SessionTime()//Returns the current date and time from the System.
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//Sets the time format.
		Calendar cal = Calendar.getInstance();//Generates the current date and time from System.
		return dateFormat.format(cal.getTime());//Returns the date and time in the specified format
	}
}

