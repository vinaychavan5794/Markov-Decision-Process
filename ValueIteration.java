import org.ini4j.*;
import java.io.File;
public class ValueIteration 
{
	
    private static double reward ;        
    private static double gama ;         
    private static double pExecution;     
    private static double pNotExecution; 
    private static int N = 10000;           
    private static double deltaMin;    
    private static double epsilon; 
    private static double U[][];  
    private static double Uprime[][]; 
    private static double Reward[][];  
    private static char  policy[][];  
    
     
    private static int rowCount, columnCount,wallRow1,wallRow2,wallColumn1,wallColumn2,rewardRow1,
    rewardRow2,rewardRow3,rewardColumn1,rewardColumn2,rewardColumn3,reward1,reward2,reward3;
     
    public static void main(String[] args)
    {
    	try {
    		Wini ini = new Wini(new File("C:\\Users\\vinay\\Desktop\\study\\AI\\mdp_input.ini"));
            rowCount = ini.get("size", "Row", int.class);
            columnCount = ini.get("size", "Column", int.class);
            reward = ini.get("NonterminalstatesReward", "Reward", double.class);
            gama = ini.get("TransitionProbabilitiesandRates", "discountrate", double.class);
            pExecution = ini.get("TransitionProbabilitiesandRates", "Up", double.class);
            pNotExecution = ini.get("TransitionProbabilitiesandRates", "Left", double.class);
            wallRow1 = ini.get("Walls", "WallRow", int.class);
            wallRow2 = ini.get("Walls", "WallRow1", int.class);
            wallColumn1 = ini.get("Walls", "WallColumn", int.class);
            wallColumn2 = ini.get("Walls", "WallColumn1", int.class);
            rewardRow1 = ini.get("Terminalstates", "Row1", int.class);
            rewardRow2 = ini.get("Terminalstates", "Row2", int.class);
            rewardRow3 = ini.get("Terminalstates", "Row3", int.class);
            rewardColumn1 = ini.get("Terminalstates", "Column1", int.class);
            rewardColumn2 = ini.get("Terminalstates", "Column2", int.class);
            rewardColumn3 = ini.get("Terminalstates", "Column3", int.class);
            reward1 = ini.get("Terminalstates", "Reward1", int.class);
            reward2 = ini.get("Terminalstates", "Reward2", int.class);
            reward3 = ini.get("Terminalstates", "Reward3", int.class);
            epsilon = ini.get("TransitionProbabilitiesandRates", "epsilon", double.class);
            deltaMin=epsilon*(1-gama)/gama;
			
		} catch (Exception e) {
			System.out.println("There was an Exception while reading the ini file");
			e.printStackTrace();
		}
        double delta = 0;
    
        policy = new char[rowCount][columnCount]; 
        Uprime = new double[rowCount][columnCount]; 
        for (int row=0; row<rowCount; row++) {
            for (int col=0; col<columnCount; col++) {
            	Uprime[row][col] = 0;
            }
        }
        
        U = new double[rowCount][columnCount];
         
        
        Reward = new double[rowCount][columnCount];
        for (int row=0; row <rowCount; row++) {
            for (int col=0; col<columnCount; col++) {
            	Reward[row][col] = reward;
            }
            
        }
		
        Reward[rewardRow1][rewardColumn1] =  reward1; 
        Reward[rewardRow2][rewardColumn2] = reward2;
        Reward[rewardRow3][rewardColumn3] = reward3;  
        Reward[wallRow1][wallColumn1] =    0; 
        Reward[wallRow2][wallColumn2] =    0; 
               
        int n = 0;
        do
        {
          
            clone(Uprime, U);
            n++;
            delta = 0;
            for (int row=0; row<rowCount; row++) {
                for (int col=0; col<columnCount; col++) {
                    updateUPrime(row, col);
                    double difference = Math.abs(Uprime[row][col] - U[row][col]);
                    if (difference > delta)
                        delta = difference;
                }
            }
            System.out.println("After " + n+"th" + " iteration:\n");
            for (int row=0; row<rowCount; row++) {
                for (int col=0; col<columnCount; col++) {
                    System.out.printf("% 9.2f\t", Uprime[row][col]);
                }
                System.out.print("\n");
            }
        } while (delta > deltaMin && n < N);

        policy[rewardRow1][rewardColumn1] = '-'; policy[rewardRow2][rewardColumn2] = '+';policy[rewardRow3][rewardColumn3]='+';
        policy[wallRow1][wallColumn1] = '#';policy[wallRow2][wallColumn2] = '#';
         
        System.out.println("\nBest policy:\n");
        for (int row=0; row<rowCount; row++) {
            for (int col=0; col<columnCount; col++) {
                System.out.print(policy[row][col] + "   ");
            }
            System.out.print("\n");
        }
    }
     
    public static void updateUPrime(int row, int col)
    {
        
        double moves[] = new double[4];
     
        if ((row==1 && col==1) || (row==1 && col==2) || (row==4 && col==2)|| (row==4 && col==3)|| (row==3 && col==1)) {
        	Uprime[row][col] = Reward[row][col];
        }
        else {
        	moves[0] = moveUp(row,col)*pExecution + moveLeft(row,col)*pNotExecution + moveRight(row,col)*pNotExecution;
        	moves[1] = moveDown(row,col)*pExecution + moveLeft(row,col)*pNotExecution + moveRight(row,col)*pNotExecution;
        	moves[2] = moveLeft(row,col)*pExecution + moveDown(row,col)*pNotExecution + moveUp(row,col)*pNotExecution;
        	moves[3] = moveRight(row,col)*pExecution + moveDown(row,col)*pNotExecution + moveUp(row,col)*pNotExecution;
             
            int maxIndex = maxindex(moves);
             
            Uprime[row][col] = Reward[row][col] + gama * moves[maxIndex];
 
            policy[row][col] = (maxIndex==0 ? 'U' : (maxIndex==1 ? 'D' : (maxIndex==2 ? 'L': 'R')));
        }
    }
     
    
    public static void clone(double[][]source, double[][]destination)
    {
        int length=source.length;
        for (int i=0; i<length; i++) {
			
        	System.arraycopy(source[i], 0, destination[i], 0, source[i].length);
        }
    }
     
    public static double moveDown(int row, int col)
    {
        
        if ((row==rowCount-1) || (row==0 && col==1)|| (row==0 && col==2)) {
            return U[row][col];
        }
        return U[row+1][col];
    }
    
    public static double moveUp(int row, int col)
    {
        
        if ((row==0) || (row==2 && col==1)|| (row==2 && col==2)) {
            return U[row][col];
        }
        return U[row-1][col];
    }
    
    public static double moveRight(int row, int col)
    {
        
        if ((col==columnCount-1) || (row==1 && col==0)) {
            return U[row][col];
        }
        return U[row][col+1];
    }
    
    public static double moveLeft(int row, int col)
    {
        
        if ((col==0) || (row==1 && col==3)) {
            return U[row][col];
        }    
        return U[row][col-1];
    }
    
    public static int maxindex(double moves[]) 
    {
        int bestMoveIndex=0;
        for (int i=0; i<moves.length; i++)
        	bestMoveIndex = moves[i] > moves[bestMoveIndex] ? i : bestMoveIndex;
        	
        return bestMoveIndex;
    }
     
    
}