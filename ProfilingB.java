import java.util.ArrayList;
import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.Arrays;
import java.nio.file.Files;

public class ProfilingB{
static ArrayList<Profile> profiles = new ArrayList<Profile>();
static PrintWriter output;



public void parseFile(String filepath){
	try{
		File file = new File(filepath);
		Scanner in = new Scanner(file);
	while (in.hasNextLine()){
		String line = in.nextLine();
		// Split line into name and books entries
		String[] entries = line.split(", ");
		// Split name to parts to remove extra whitespace
		String[] nameParts = entries[0].split("  ");
		// Create new profile, add books
		Profile profile = new Profile(nameParts[0]+" "+nameParts[1]);
		for (int i=1;i<entries.length;i++){
			profile.addBook(entries[i]);
		}
		profiles.add(profile);
	}
	}
	catch(FileNotFoundException e){
		System.out.println("Specified file not found.");
	}
}

private class Profile{
	String name;
	ArrayList<String> books;
	
	Profile(String name){
		this.name = name;
		books = new ArrayList<String>();
	}
	
	private void printInfo(){
		System.out.println(name);
		for (String book : books){
			System.out.println("   -"+book);
		}
	}
	
	private void addBook(String book){
		this.books.add(book);
	}	
}

// Predicates
// Profile predicates
public static Predicate<Profile> hasReadAtLeast(int amount) {
	return p -> p.books.size() >= amount;
}

public static Predicate<Profile> hasReadBook(String book){
	return p -> p.books.contains(book);
}

public static Predicate<Profile> isFirstLetter(char c){
	return p -> p.name.charAt(0)==c;
}

public static Predicate<Profile> hasReadStartingWith(String str){
	return p -> p.books.stream().filter(b -> b.startsWith(str)).collect(Collectors.toList()).size() > 4;
}

// Predicate filter
public static ArrayList<Profile> filterProfiles(Predicate<Profile> predicate){
	List<Profile> list = profiles.stream()
								 .filter(predicate)
								 .collect(Collectors.<Profile>toList());
	return new ArrayList<Profile>(list);
}

// Functions
// Profile functions
public static Function<Profile,String> replaceLastName(){
	return p -> p.name.split(" ")[0]+" "+Arrays.stream(p.books.get(0).split(" ")).filter(a -> (!a.equalsIgnoreCase("The") && !a.equalsIgnoreCase("To"))).findFirst().get();
}

public static Function<Profile,String> repeatName(){
	return p -> p.books.stream()
					   .map(b -> p.name.split(" ")[0])
					   .collect(Collectors.joining());
}

public static Function<Profile,String> initials(){
	return p -> p.name.charAt(0)+" "+p.name.split(" ")[1].charAt(0);
}

// Function map
public static ArrayList<String> mapProfiles(Function<Profile, String> function){
	List<String> list = profiles.stream()
								.map(function)
								.collect(Collectors.<String>toList());
	return new ArrayList<String>(list);
}	

// Consumers
// String consumers
public static Consumer<String> toConsole = s -> System.out.println(s);

public static Consumer<String> toFile = s -> output.write(s+"\n");

// Consumer function
public static void consumeStrings(Consumer<String> consumer, ArrayList<String> strings){
	
	strings.stream().forEach(consumer);
}

public static void writeToFile(ArrayList<String> profiles, String filepath){

	try{
		output = new PrintWriter(filepath, "UTF-8");
		consumeStrings(toFile,profiles);
	}
	catch(Exception e){
		System.out.println(e);
	}
	finally{
		output.close();
	}
}

// Combine functions
// Function filtering all profiles to show just the initials of people who have read "Great Expectations", output on console.
public static void combineGE_initials_toConsole(){
	profiles.stream().filter(hasReadBook("Great Expectations")).map(initials()).forEach(toConsole);
}

// Function as above, except output is written both to console as to an ouputfile without using the stream multiple times.
public static void combineSteps(String filepath){
	try{
		output = new PrintWriter(filepath, "UTF-8");
		profiles.stream().filter(hasReadBook("Great Expectations")).map(initials()).peek(toConsole).forEach(toFile);
	}
	catch(Exception e){
		System.out.println(e);
	}
	finally{
		output.close();
	}
}


public static void main(String[] args){
	// Build database from file
	String filepath = "profiling-data.txt";
	ProfilingB database = new ProfilingB();
	database.parseFile(filepath);
	
	// Tests
	//	ArrayList<Profile> filteredProf = filterProfiles(isFL);
	//	for (Profile prof : filteredProf){
	//		prof.printInfo();
	//	}

	//ArrayList<String> mappedProf = mapProfiles(replaceLastName());
	//System.out.println(mappedProf.get(2));
	
	//ArrayList<String> mappedProf2 = mapProfiles(initials());
	//System.out.println(mappedProf2.get(2));
	
	//writeToFile(mappedProf2,"output.txt");
	
	//combineGE_initials_toConsole();
	
	combineSteps("outputCombined.txt");
	
}
	

}
