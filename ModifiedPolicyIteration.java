import java.io.File;
import java.util.Random;

import org.ini4j.Wini;

public class ModifiedPolicyIteration {
	   private static double reward ;            
	    private static double gama ;          
	    private static double pExecution;        
	    private static double pNotExecution; 
	    private static double U[][];  
	    private static double Reward[][];  
	    private static char  Policy[][];  
	    private static char result[][];
	    
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
				
			} catch (Exception e) {
				
			}
	        int row,col;
	        Policy = new char[rowCount][columnCount]; 
	         
	        
	        U = new double[rowCount][columnCount]; 
	        for (row=0; row<rowCount; row++) {
	            for (col=0; col<columnCount; col++) {
	                U[row][col] = 0;
	            }
	        }
	         
	        
	        Reward = new double[rowCount][columnCount]; 
	        for (row=0; row<rowCount; row++) {
	            for (col=0; col<columnCount; col++) {
	            	Reward[row][col] = reward;
	            }
	            
	        }
			
	        Reward[rewardRow1][rewardColumn1] =  reward1;  
	        Reward[rewardRow2][rewardColumn2] = reward2;
	        Reward[rewardRow3][rewardColumn3] = reward3;  
	        Reward[wallRow1][wallColumn1] =    0;  
	        Reward[wallRow2][wallColumn2] =    0; 
	
	        Policy[rewardRow1][rewardColumn1] = '-'; Policy[rewardRow2][rewardColumn2] = '+';Policy[rewardRow3][rewardColumn3]='+';
	        Policy[wallRow1][wallColumn1] = '#';Policy[wallRow2][wallColumn2] = '#';
	        result=PolicyIteration(gama);
	        PrintOptimalPolicy();
	        
	    }

		
		public static double summationUtility(int row, int col) {													
																
			double[] possibleUtilities = {0, 0, 0, 0};				
			try { 												
			possibleUtilities[0] = expectedUtility(row,  col, 'U') + UnexpectedUtility(row, col, 'U');		  
			possibleUtilities[1] = expectedUtility(row,  col, 'D') + UnexpectedUtility(row, col, 'D');	
			possibleUtilities[2] = expectedUtility(row,  col, 'L') + UnexpectedUtility(row, col, 'L');
			possibleUtilities[3] = expectedUtility(row,  col, 'R') + UnexpectedUtility(row, col, 'R');
			} catch (Exception e){
				e.printStackTrace();
				
			}
			
			double maxUtility = possibleUtilities[0]; 							
			for (double utility: possibleUtilities) {  
				if ( utility > maxUtility) {
					maxUtility = utility;
				}
			}
			
			return maxUtility;						
		}
		
	    public static char [][] PolicyIteration(double discount){
			
			randomPolicy();				
			boolean isUnchanged = true;										
			int iterations = 1;					
				
			do {
				
				U = PolicyEvaluation(discount, 25);		
				isUnchanged = true;
				for (int row = 0; row < rowCount; row++){		
					for (int col = 0; col < columnCount; col++) {
						if((row==1 && col==1)|| (row==1 && col==2)) { 
						} else {
							try {
								double maxUtility = summationUtility(row, col);							 
																								
								double policyDefinedUtility = expectedUtility(row, col, Policy[row][col] ) + UnexpectedUtility(row, col, Policy[row][col]); 
									if (maxUtility > policyDefinedUtility) {							
									Policy[row][col] = actionCorrespondingMaxUtility(row, col);		
									isUnchanged = false;
									}
								} catch (Exception e) {
									e.printStackTrace();
									
								}
							}
					}
				}
				
				iterations++;																		

			} while (isUnchanged == false);							
			
			System.out.println("After Policy Iteration #"+ iterations);										
			
			return Policy;															
		}
		
		public static void randomPolicy() {					
			for (int col = 0; col < columnCount; col++) {
				for (int row = 0; row < rowCount; row++) {
					
					Random rand = new Random();
					switch(rand.nextInt(4)) {
					case 0:
						Policy[row][col] = 'U';
						break;
					case 1:
						Policy[row][col] = 'D';
						break;
					case 2:
						Policy[row][col] = 'L';
						break;
					case 3:
						Policy[row][col] = 'R';
						break;
						
					}
					
					
				}
			}
		}
		
		public static double[][] PolicyEvaluation(double discount, int k){
			
			double[][] newUtility = new double[rowCount][columnCount];
			
			for (int col = 0; col < columnCount; col++) {
				for (int row = 0; row < rowCount; row++) {
					newUtility[row][col] = 0;
				}
			}
							
			for(int i=0;i<k;i++) {
					
				for (int row = 0; row < rowCount; row++) {
					for (int col = 0; col < columnCount; col++) {	
						if (Reward[row][col] == 0) {									
							
						} else {
							try {
							double utilityEstimate = expectedUtility(row, col, Policy[row][col]) + UnexpectedUtility(row, col, Policy[row][col]);	
							newUtility[row][col] = Reward[row][col] + discount*utilityEstimate; 															
							} catch (Exception e)
							{	
								e.printStackTrace();
								System.out.println(e.getMessage());
							}
						}
					}
				}
				
				U = newUtility.clone();	
			}	

			return newUtility;  
		}
		
		
		
		public static double expectedUtility(int row, int col, char action) throws Exception {

			switch(action){													
			case 'U':		
				if (row-1 < 0){ 											
					return U[row][col]*pExecution;			
				} else if (Reward[row-1][col] == 0){ 	
					return U[row][col]*pExecution;			
				} else {													
					return U[row-1][col]*pExecution;		
				}															
			case 'D':
				if (row+1 > rowCount-1){									
					return U[row][col]*pExecution;			
				} else if (Reward[row+1][col] == 0){ 	
					return U[row][col]*pExecution;			
				} else {													
					return U[row+1][col]*pExecution;		
				}	
			case 'L':
				if (col-1 < 0){ 											
					return U[row][col]*pExecution;			
				} else if (Reward[row][col-1] == 0){ 	
					return U[row][col]*pExecution;			
				} else {													
					return U[row][col-1]*pExecution;		
				}	
			case 'R':
				if (col+1 > columnCount-1){ 								
					return U[row][col]*pExecution;			
				} else if (Reward[row][col+1] == 0){ 	
					return U[row][col]*pExecution;			
				} else {													
					return U[row][col+1]*pExecution;		
				}	
				
			}
			
			throw new Exception("Failed to calculate expected probability");

		}
		
		public static char actionCorrespondingMaxUtility(int row, int col) {		
			double[] possibleUtilities = {0, 0, 0, 0};					

			try { //calculates utility of all four actions
			possibleUtilities[0] = expectedUtility(row,  col, 'U') + UnexpectedUtility(row, col, 'U');

			possibleUtilities[1] = expectedUtility(row,  col, 'D') + UnexpectedUtility(row, col, 'D');

			possibleUtilities[2] = expectedUtility(row,  col, 'L') + UnexpectedUtility(row, col, 'L');

			possibleUtilities[3] = expectedUtility(row,  col, 'R') + UnexpectedUtility(row, col, 'R');
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("column:"+col + " row:"+ row);
			}
			
			double maxUtil = possibleUtilities[0];
			int indexOfMaxUtil=0;
			
			for (int i = 0; i< possibleUtilities.length; i++ ) {  
				if ( possibleUtilities[i] >= maxUtil) {
					maxUtil = possibleUtilities[i];
					indexOfMaxUtil=i;
				}
			}
			
			switch(indexOfMaxUtil) { 
			case 0:
				return 'U';
			case 1: 
				return 'D';
			case 2: 
				return 'L';
			case 3: 
				return 'R';
				
			default: System.out.println("Error");
			return 'N';
			}
		}
		
		public static double UnexpectedUtility(int row, int col, char action) throws Exception{
			
			double unexpectedProb = 0;													
			
			switch(action){
			case 'U':																	  
			case 'D':																	
					
				if (col-1 < 0 ){ 														
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else if (Reward[row][col-1] == 0){					
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else { 																
					unexpectedProb+= U[row][col-1]*pNotExecution;			
				}
					
					
				if (col+1 > columnCount-1 ){ 											
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else if (Reward[row][col+1] == 0){					
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else { 																
					unexpectedProb+= U[row][col+1]*pNotExecution;			
				}
				
				return unexpectedProb;													
				
			case 'L':																	
			case 'R':																	
				
				if (row-1 < 0 ){ 														
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else if (Reward[row-1][col] == 0){								

					unexpectedProb+= U[row][col]*pNotExecution;			
				} else { 																

					unexpectedProb+= U[row-1][col]*pNotExecution;			
				}
					
					
				if (row+1> rowCount-1 ){ 												
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else if (Reward[row+1][col] == 0){								
					unexpectedProb+= U[row][col]*pNotExecution;			
				} else { 																
					unexpectedProb+= U[row+1][col]*pNotExecution;			
				}
				
				return unexpectedProb;													
			}
			
			throw new Exception("Failed to calculate unexpected probability");
		}
		
		
		
		
		
		public static void PrintOptimalPolicy() { 
			
			System.out.println("Printing optimal policy");
			
			
			for (int row = 0; row < rowCount; row++) {
				for (int col = 0; col < columnCount; col++) {
					
					if (Reward[row][col] == 0) {
						System.out.print(" # ");
						continue;
					}
					else if (Reward[row][col] == -3) {
						System.out.print(" - ");
						continue;
					}
					else if (Reward[row][col] == 2) {
						System.out.print(" + ");
						continue;
					}
					else if (Reward[row][col] == 1) {
						System.out.print(" + ");
						continue;
					}
					switch(Policy[row][col]) {
					case 'U':
						System.out.print(" U ");
						break;
					case 'D':
						System.out.print(" D ");
						break;
					case 'L':
						System.out.print(" L ");
						break;
					case 'R':
						System.out.print(" R ");
						break;
					}
					
				}
				System.out.println();
			
			}
			
		}
		
		
		 
}
