import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.Dimension;
import java.util.Stack;
import java.util.Random;

public class Maze extends JFrame implements ActionListener{

    //Labels for the Frame
    private JLabel TimeLabel;
    private JLabel TxtTime;
    private JLabel VisitedLabel;
    private JLabel TxtVisited;
    private JLabel TxtSpeed;
    private JLabel TxtRows;
    private JLabel TxtColumns;

    //Buttons for the Frame
    private JButton GenerateMaze;
    private JButton NewGrid;
    private JButton SolveMaze;
    private JButton Pause;
    private JButton Resume;

    //Sliders for the Frame
    private JSlider Speed;
    private JSlider Rows;
    private JSlider Columns;

    //Check mark box for show/hide
    private JCheckBox ShowGeneration;
    private JCheckBox ShowSolver;

    // we create the timer
    Timer timer;

    /*---Variables---*/

    //assign numbers for colors
    final static int black = 1;
    final static int path_color = 9;

    //initial state (i,j)
    final static int START_I = 1, START_J = 1;

    //goal (i,j)
    final static int END_I = 2, END_J = 9;

    //random array
    int [][] grid;

    boolean repaint = true;
    private int time = 0;
    private int rows = 10;
    private int delay;
    private int columns = 10;

    int width = 400;
    int height = 400;
    int cellWidth = width/columns;
    int cellHeight = height/rows;

    //the 2d maze
    int maze[][] = new int[rows][columns];

    public Maze(){

        setLayout(null);

        //start application in center of screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        //set title of app
        this.setTitle("Maze Solver");

        //set up the buttons
        GenerateMaze =  new JButton("Generate");
        GenerateMaze.setToolTipText("Creates a random maze");
        NewGrid = new JButton("New Grid");
        NewGrid.setToolTipText("Creates a new Grid based on Rows/Columns");
        SolveMaze = new JButton("Solve");
        SolveMaze.setToolTipText("Solves the Maze without animation");
        Resume = new JButton("Resume");
        Resume.setToolTipText("Resumes the maze solution");
        Pause = new JButton("Pause");
        Pause.setToolTipText("Pauses the maze solution");

        //set up the checkboxes
        ShowGeneration = new JCheckBox("Show Generation");
        ShowSolver = new JCheckBox("Show Solver");

        //set up the labels
        TimeLabel = new JLabel("Time: ");
        TxtTime = new JLabel("" + time + "s");
        VisitedLabel = new JLabel("Visited: ");
        TxtVisited = new JLabel("0%");
        TxtSpeed = new JLabel("Speed");
        TxtRows = new JLabel("Rows: " + rows);
        TxtColumns = new JLabel("Columns: " + columns);

        //set up the sliders
        Speed = new JSlider(0,1000,500); // initial value of delay 500 ms
        Speed.setToolTipText("Regulates the delay for each step (0 to 1 sec)");
        Rows = new JSlider(0,50,10);
        Rows.setToolTipText("Sets the number of Rows to be generated");
        Columns = new JSlider(0, 50, 10);
        Columns.setToolTipText("Sets the number of Columns to be generated");

        //add the contents of the panel
        add(GenerateMaze);
        add(ShowGeneration);
        add(ShowSolver);
        add(SolveMaze);
        add(Resume);
        add(Pause);
        add(TimeLabel);
        add(TxtTime);
        add(VisitedLabel);
        add(TxtVisited);
        add(Speed);
        add(Rows);
        add(Columns);
        add(TxtSpeed);
        add(TxtColumns);
        add(TxtRows);
       // add(NewGrid);

        //add action listeners
        NewGrid.addActionListener(this);
        Pause.addActionListener(this);
        ShowSolver.addActionListener(this);
        ShowGeneration.addActionListener(this);
        Resume.addActionListener(this);
        GenerateMaze.addActionListener(this);
        SolveMaze.addActionListener(this);

        delay = 1000-Speed.getValue();
        Speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    delay = 1000 - source.getValue();
                }
            }
        });

        Rows.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    rows = (int) source.getValue();
                    TxtRows.setText("Rows: " + rows);

                }
            }
        });

        Columns.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int  columns = (int) source.getValue();
                    TxtColumns.setText("Columns: " + columns);
                }
            }
        });


        //set bounds
        TimeLabel.setBounds(50, 425, 75, 25);
        TxtTime.setBounds(90, 425, 75, 25);
        VisitedLabel.setBounds(200, 425, 75, 25);
        TxtVisited.setBounds(250, 425, 75, 25);
        GenerateMaze.setBounds(440, 25, 100, 25);
        ShowGeneration.setBounds(560,25,150,25);
        NewGrid.setBounds(440, 75, 100, 25);
        SolveMaze.setBounds(440, 125, 100, 25);
        ShowSolver.setBounds(560, 125, 100, 25);
        TxtSpeed.setBounds(560, 170, 75, 25);
        Speed.setBounds(500, 190, 175, 25);
        TxtRows.setBounds(560, 230, 75, 25);
        Rows.setBounds(500, 250, 175, 25);
        TxtColumns.setBounds(560, 305, 75, 25);
        Columns.setBounds(500, 325, 175, 25);
        Pause.setBounds(500, 400, 150, 50);
        Resume.setBounds(500, 400, 150, 50);


        //hide resume initially
        Resume.setVisible(false);

        //create timer object to measure time
        timer = new javax.swing.Timer(delay, this);

    }

    // to mark the node in the array with a certain value
    public int mark(int i, int j, int value) {
        assert (InsideTheMaze(i, j));
        int temp = maze[i][j];
        maze[i][j] = value;       // put the value from the parameter in maze cell with corresponding i,j
        return temp;              // return original value
    }

    public int mark(MazePos pos, int value) {
        return mark(pos.i(), pos.j(), value);
    }



    public void actionPerformed( ActionEvent e ) {

        Object UsrSource = e.getSource();

        if (UsrSource == GenerateMaze){

            if (ShowGeneration.isSelected()){
              //show the animation of the maze being created
                timer.start();

            }//else generate the maze without the animation

            TxtTime.setText("" + time + "s");
            int x[][] = GenerateArray(); // generate random array and store in x
            ChangeToInitial(x);
            repaint(); // repaint the maze on the JFrame
        }

        else if(UsrSource == NewGrid){

        }

        else if (UsrSource == SolveMaze){
            if(ShowSolver.isSelected()){
                //show animation of the maze being solved
                timer.start();

            }//else solve the maze without animation
            ChangeToInitial(grid);
            repaint = false;
            SolveDFS();
            repaint();
        }

         else if( UsrSource == Pause ){
             Pause.setVisible(false);
             Resume.setVisible(true);
         }

         else if( UsrSource == Resume ){
             Pause.setVisible(true);
             Resume.setVisible(false);
         }

        else if( UsrSource == timer )
        {
            time++;
            TxtTime.setText( "" + time + "s" );

        }

    }

    public void SolveDFS() {

        Stack<MazePos> stack = new Stack<MazePos>();


        stack.push(new MazePos(START_I, START_J));

        MazePos crt;
        MazePos next;

        while (!stack.empty()) {

            crt = stack.pop();

            if (isFinal(crt)) {

                break;
            }

            mark(crt, path_color);

            next = crt.north();
            if (InsideTheMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.east();
            if (InsideTheMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.west();
            if (InsideTheMaze(next) && isClear(next)) {
                stack.push(next);
            }
            next = crt.south();
            if (InsideTheMaze(next) && isClear(next)) {
                stack.push(next);
            }



        }
    }

    //return true if cell is within maze
    public boolean InsideTheMaze(int i, int j) {

        if (i >= 0 && i < Size() && j >= 0 && j < Size()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean InsideTheMaze(MazePos pos) {
        return InsideTheMaze(pos.i(), pos.j());
    }


    public boolean isClear(int i, int j) {
        assert (InsideTheMaze(i, j));
        return (maze[i][j] != black && maze[i][j] != path_color);

    }

    public boolean isClear(MazePos pos) {
        return isClear(pos.i(), pos.j());
    }


    public boolean isFinal(int i, int j) {

        return (i == Maze.END_I && j == Maze.END_J);
    }

    public boolean isFinal(MazePos pos) {
        return isFinal(pos.i(), pos.j());
    }

    //draw the maze on the JFrame
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.translate(10, 50);      //move the maze to begin at 70 from x and 70 from y
        float CountTotal = 0;

        // draw the maze
        if (repaint == true) {  // what to do if the repaint was set to true (draw the maze as a problem without the solution)
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.DARK_GRAY;       // block (black)
                            break;
                        case 8:
                            color = Color.RED;          // goal (red)
                            break;
                        case 2:
                            color = Color.green;      //initial state   (yellow)
                            break;
                        default:
                            color = Color.WHITE;
                    }
                    g.setColor(color);
                    g.fillRect(cellWidth * col, cellHeight * row, cellWidth, cellHeight);  // fill rectangular with color
                    g.setColor(Color.BLACK);
                    g.drawRect(cellWidth * col, cellHeight  * row, cellWidth, cellHeight);  //draw rectangular with color

                }
            }
        }

        if (repaint == false) {   // what to do if the repaint was set to false (draw the solution for the maze)
            for (int row = 0; row < maze.length; row++) {
                for (int col = 0; col < maze[0].length; col++) {
                    Color color;
                    switch (maze[row][col]) {
                        case 1:
                            color = Color.DARK_GRAY;     // block (black)
                            break;
                        case 8:
                            color = Color.RED;         // goal  (red)
                            break;
                        case 2:
                            color = Color.blue;      //initial state   (yellow)
                            break;
                        case 9:
                            color = Color.CYAN;   // the path from the initial state to the goal
                            CountTotal ++;
                            break;
                        default:
                            color = Color.WHITE;   // white free space 0  (white)
                    }
                    g.setColor(color);
                    g.fillRect(cellWidth * col, cellHeight * row, cellWidth, cellHeight);  // fill rectangular with color
                    g.setColor(Color.BLACK);                  //the border rectangle color
                    g.drawRect(cellWidth * col, cellHeight  * row, cellWidth, cellHeight);  //draw rectangular with color

                }

            }


        }

        TxtVisited.setText("" + (CountTotal/(rows * columns)) * 100 + "%");
        timer.stop();
        time = 0;

    }

    // get size of the maze
    public int Size() {
        return maze.length;
    }

    // ChangeToInitial the maze to the initial state
    public void ChangeToInitial(int[][] savedMazed) {

        for (int i = 0; i < Size(); i++) {
            for (int j = 0; j < Size(); j++) {
                maze[i][j] = savedMazed[i][j];
            }
        }

        maze[0][0] = 2;  // the start point
        maze[rows - 1][columns -1] = 8;  // the goal
    }

    //generate random maze whith values 0 and 1 (black and white blocks)
    public int[][] GenerateArray() {

        grid = new int[rows][columns];

        Random rnd = new Random();
        int min = 0;
        int high = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int n = rnd.nextInt((high - min) + 1) + min;
                grid[i][j] = n;

            }
        }

        //make sure all paths from initial state are legal moves (white block)
        grid[0][1] = 0;
        grid[1][0] = 0;

        //make sure all paths to goal are legal moves (white block)
        grid[rows - 2][columns - 1] = 0;
        grid[rows - 1][columns - 2] = 0;

        return grid;

    }

    public static void main(String args[]) {

        Maze DarkMaze = new Maze();
        DarkMaze.setSize(700, 500);
        DarkMaze.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        DarkMaze.setVisible(true);
        DarkMaze.setResizable(false);

    }
}