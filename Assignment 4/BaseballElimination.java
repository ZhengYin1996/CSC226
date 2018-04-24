/* BaseballElimination.java
   CSC 226 - Summer 2017
   Assignment 4 - Baseball Elimination Program
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java BaseballElimination
	
   To conveniently test the algorithm with a large input, create a text file
   containing one or more test divisions (in the format described below) and run
   the program with
	java BaseballElimination file.txt
   where file.txt is replaced by the name of the text file.
   
   The input consists of an integer representing the number of teams in the division and then
   for each team, the team name (no whitespace), number of wins, number of losses, and a list
   of integers represnting the number of games remaining against each team (in order from the first
   team to the last). That is, the text file looks like:
   
	<number of teams in division1>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	<number of teams in division2>
	<team1_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	<teamn_name wins losses games_vs_team1 games_vs_team2 ... games_vs_teamn>
	...
	
   An input file can contain an unlimited number of divisions but all team names are unique, i.e.
   no team can be in more than one division.


   R. Little - 07/10/2017
*/

import java.util.*;
import java.io.File;
import edu.princeton.cs.algs4.*;

//Do not change the name of the BaseballElimination class
public class BaseballElimination{
	
	// We use an ArrayList to keep track of the eliminated teams.
	public static ArrayList<String> eliminated = new ArrayList<String>();
	public static int numTeamsInDivision;
	public static String[] teams;
	public static double infinity = Double.POSITIVE_INFINITY;
	/* BaseballElimination(s)
		Given an input stream connected to a collection of baseball division
		standings we determine for each division which teams have been eliminated 
		from the playoffs. For each team in each division we create a flow network
		and determine the maxflow in that network. If the maxflow exceeds the number
		of inter-divisional games between all other teams in the division, the current
		team is eliminated.
	*/

	private static class Pair<T> {
		    T i, j;
    		Pair(T i, T j) {
    	    	this.i = i;
        		this.j = j;
    	}
    }


	public static ArrayList<String> BaseballElimination(Scanner s){
		int n;
		int [][] intputData;
		n = s.nextInt();
		numTeamsInDivision=n;
		intputData = new int[n][n+3];

		teams = new String[n];
		for (int u = 0; u<n; u++ ) {
			for (int v =0; v<(n+3); v++) {
				if(v==0){
					teams[u]=s.next();
					intputData[u][v]=u;
				}
				else{
					if(s.hasNextInt()){
						intputData[u][v]=s.nextInt();
					}
				}
			}
		}

		if(n!=0){
			int teamBeingTested=0;
			while(teamBeingTested<n){
				checkEliminated(teamBeingTested, intputData);
				teamBeingTested++;
			}

		}else{
			System.out.println("The number of teams is set to 0");
		}

		return  eliminated;	
	}
	
	public static void checkEliminated(int team, int[][] intputData){
		
		int W = intputData[team][1] + intputData[team][2];	//Wins + Games to play
		int numTeams = numTeamsInDivision-1;
		int sumation = (numTeams*(numTeams+1))/2;
		int totalGamesLeftInbetweenAllTeams = 0;
		ArrayList<Pair<Integer>> games = new ArrayList<Pair<Integer>>();

		for (int i =0; i<numTeamsInDivision; i++) {
			for (int j=i+1; j<numTeamsInDivision; j++) {
				if(i!=team && j!=team){
					Pair<Integer> unorderedPair = new Pair<Integer>(i,j);
					games.add(unorderedPair);
				}
			}
		}

		int maxSizeNetwork = numTeams + sumation + 2; //num teams is T', sumation is the upper limit for L and 2 for s and t.
		int sink = maxSizeNetwork-1;
		FlowNetwork flow = new FlowNetwork(maxSizeNetwork);
		FlowNetwork residual = new FlowNetwork(maxSizeNetwork);
		
		int size = games.size();
		boolean[] hasEdge = new boolean[numTeamsInDivision];
		for (int i =0; i<size; i++) {
			Pair<Integer> unorderedPair = games.get(i);
			int gamesLeftBetween=0;

			gamesLeftBetween = intputData[unorderedPair.i][unorderedPair.j+3];

			totalGamesLeftInbetweenAllTeams+=gamesLeftBetween;

			if(gamesLeftBetween==0){	
				if(intputData[unorderedPair.i][1] > W || intputData[unorderedPair.j][1] > W){
					if(!eliminated.contains(teams[team]))	eliminated.add(teams[team]);	
				}

				continue;
			}



			if(intputData[unorderedPair.i][1] > W || intputData[unorderedPair.j][1] > W){
				if(!eliminated.contains(teams[team]))	eliminated.add(teams[team]);	
				return;
			}

			FlowEdge e = new FlowEdge(team, numTeamsInDivision+i, (double)gamesLeftBetween, 0.0);	//from source to unorderedPair representing matchup
			
			FlowEdge e2 = new FlowEdge(numTeamsInDivision+i, unorderedPair.i, infinity, 0.0);	//from unordered pair i,j to i

			FlowEdge e3 = new FlowEdge(numTeamsInDivision+i, unorderedPair.j, infinity, 0.0);	//from unordered pair i,j to j
			flow.addEdge(e);
			flow.addEdge(e2);
			flow.addEdge(e3);
		
			if(!hasEdge[unorderedPair.i]){
				FlowEdge e4 = new FlowEdge(unorderedPair.i, sink, (double)(W-intputData[unorderedPair.i][1]), 0.0);	 
				flow.addEdge(e4);
				hasEdge[unorderedPair.i]=true;
			}

			if(!hasEdge[unorderedPair.j]){
				FlowEdge e5 = new FlowEdge(unorderedPair.j, sink, (double)(W-intputData[unorderedPair.j][1]), 0.0);
				flow.addEdge(e5);
				hasEdge[unorderedPair.j]=true;
			}
		}
		
		FordFulkerson ford = new FordFulkerson(flow, team, sink);
		double val = ford.value();

		if(val < (double)totalGamesLeftInbetweenAllTeams){
			if(!eliminated.contains(teams[team]))	eliminated.add(teams[team]);
		}
	}


	/* main()
	   Contains code to test the BaseballElimantion function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}
		BaseballElimination be = new BaseballElimination();
		be.BaseballElimination(s);
		if (be.eliminated.size() == 0)
			System.out.println("No teams have been eliminated.");
		else
			System.out.println("Teams eliminated: " + be.eliminated);
	}
}