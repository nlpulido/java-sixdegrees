import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

class BFS {
	String startingPoint;
	String goal;
	boolean found = false;

	/* this is a function in order to calculate the shortest path between two actor strings */
	void shortestPath(HashMap<String, LinkedList<String>> map, String startingPoint, String goal){
		this.startingPoint = startingPoint;
		this.goal = goal;

		/* queue to store what actor to check */
		LinkedList<String> queue = new LinkedList<String>();
		queue.add(startingPoint);

		/* visited hashset to keep track of actors who have been checked */
		HashSet<String> visited = new HashSet<String>();
		visited.add(startingPoint);

		/* a preliminary path that keeps track of the paths */
		Hashtable<String, String> path = new Hashtable<String, String>();
		path.put(startingPoint, "");

		/* a stack that stores the final path */
		Stack<String> finalPath = new Stack<String>();
		String tempString = goal;

		/* if theres a direct connection, don't execute BFS, just end */
		if (map.get(startingPoint).contains(goal)){
			System.out.println("Path between " + startingPoint + " and " + goal + ": " + startingPoint + " --> " + goal);

		/* otherwise, execute BFS */
		} else {
			/* while the queue of actors to be searched */
			while(!queue.isEmpty()){

				/* take the first item in the queue */
				String currActor = queue.poll();

				LinkedList<String> connections = map.get(currActor);

				/* iterate through the connections of the current actor */
				for (String coactor : connections){

					// if actor hasn't been visited, add them to be checked
					if (!visited.contains(coactor)){
						queue.add(coactor);
						visited.add(coactor);
						path.put(coactor, currActor);

						// if the goal is one of the connections... break?
						if (coactor.equals(goal)){
							break;
						}
					}
				}
			}

			// form the final path by pushing only the specified path to the final path
			while (!tempString.equals(startingPoint)){
				finalPath.push(tempString);
				String link = path.get(tempString);
				tempString = link;
			}
			// push the starting point towards the final path
			finalPath.push(tempString);

			// format the final path's output
			System.out.print("Path between " + startingPoint + " and " + goal + ": ");

			// prints out all items inside the final path stack
			while (!finalPath.isEmpty()){
				System.out.print(finalPath.pop());

				// only prints the arrows if there is another actor after the current actor
				try {
					String peek = finalPath.peek();
					System.out.print(" --> ");
				} catch (EmptyStackException e){
					break;
				}
			}
		}
	}
}

public class SixDegrees {
	public static void main(String[] args){
		File inputFile = null;

		/* instantiate a Array List of Actors to represent graph */
		HashMap<String, LinkedList<String>> actorsList = new HashMap<String, LinkedList<String>>();

		/* retrieve user input */
		if (args.length > 0) {
			inputFile = new File(args[0]);
		}

		/* instantiate the buffered reader */
		BufferedReader br = null;

		/* read in the file entered through the command line, parse actor/actress names and store em */
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(inputFile));
			JSONParser parser = new JSONParser();


			// read in each line from the file
			while ((currentLine = br.readLine()) != null){

				/* array list to temporarily store actors */
				ArrayList<String> tempArray = new ArrayList<String>();

				if (currentLine.indexOf("[") != -1) {

					/* edge cases to take care of extra brackets */
					if (currentLine.contains("[Cameo]")){
						currentLine = currentLine.replace("[Cameo]", "(Cameo)");
					}

					if (currentLine.contains("[cameo]")){
						currentLine = currentLine.replace("[cameo]", "(cameo)");
					}

					if (currentLine.contains("[REC]")){
						currentLine = currentLine.replace("[REC]", "(REC)");
					}

					if (currentLine.contains("[Singing voice]")){
						currentLine = currentLine.replace("[Singing voice]", "(Singing voice)");
					}

					/* substring the cast and remove double quotes for parsing */
					currentLine = currentLine.substring(currentLine.indexOf("["), currentLine.indexOf("]") + 1);
					currentLine = currentLine.replace("\"\"", "\"");

					/* objectify them */
					Object jsonCast = (Object) parser.parse(currentLine);

					/* get the names of each object and add them to a temporary set and a temporary arraylist */
					JSONArray jsonCastArray = (JSONArray) jsonCast;
					for (Object obj : jsonCastArray) {
						obj = ((JSONObject) obj).get("name");
						String currActor = ((String) obj);
						currActor = currActor.toLowerCase();
						tempArray.add(currActor);
						LinkedList<String> connections = new LinkedList<String>();
						if (!actorsList.containsKey(currActor)){
							actorsList.put(currActor, connections);
						}
					}


					// add the edges or "connections" to each actor
					for (String actor : tempArray){
						LinkedList<String> currActorConnections = actorsList.get(actor);
						for (String coactor : tempArray){
							if (!coactor.equals(actor)){
								currActorConnections.add(coactor);
							}
						}
					}

				}
			}


		} catch (EOFException e){
		} catch (IOException e){
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ParseException e){
			e.printStackTrace();
		}

		/* initialize BFS */
		BFS bfs = new BFS();

		/* scanner for user input */
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter a name for Actor 1. (include capitals for first and last name)");
		String actor1 = (scanner.nextLine()).toLowerCase();


		/* check if actor1 exists */
		if (!actorsList.containsKey(actor1)){
			System.out.println("Actor 1 does not exist.");
			return;
		} else {
			System.out.println("Actor 1: " + actor1.toString());
		}


		/* check if actor2 exists */
		System.out.println("Enter a name for Actor 2. (include capitals for first and last name)");
		String actor2 = (scanner.nextLine()).toLowerCase();

		if (!actorsList.containsKey(actor2)){
			System.out.println("Actor 2 does not exist.");
			return;
		} else {
			System.out.println("Actor 2: " + actor2.toString());
		}

		/* if both actors exist, calculate the shortest path */
		bfs.shortestPath(actorsList, actor1, actor2);

	}
}