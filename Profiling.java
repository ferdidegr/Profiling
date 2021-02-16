import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class Profiling{
static ArrayList<Profile> profiles = new ArrayList<Profile>();


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

// Return all profiles with the name containing the input string
private ArrayList<Profile> nameLookUp(String name){
	ArrayList<Profile> matches = new ArrayList<Profile>();
	for (Profile profile : profiles){
		if (profile.name.toLowerCase().contains(name)){
			matches.add(profile);
		}
	}
	return matches;
}	

// Return all profile with a book containing the input string	
private ArrayList<Profile> bookLookUp(String bookname){
	ArrayList<Profile> matches = new ArrayList<Profile>();
	for (Profile profile : profiles){
		for (String book : profile.books){
			if (book.toLowerCase().contains(bookname)){
				matches.add(profile);
				break;
			}
		}
	}
	return matches;
}

private void displayBookReaders(String bookname){
	ArrayList<Profile> matches = bookLookUp(bookname);
	for (Profile profile : matches){
		System.out.println(profile.name);
	}
}

private void recommendBook(String name){
	// Check if string matches a single person
	ArrayList<Profile> nameMatch = nameLookUp(name);
	if (nameMatch.size()==0){
		System.out.println("No person found by that name.");
	}
	else if (nameMatch.size()>1){
		System.out.println("Several people found. Enter more specific name:");
		for (Profile profile : nameMatch){
			System.out.println(profile.name);
		}
	}
	else{
		// Find recommendation, print
		Profile user = nameMatch.get(0);
		Set<String> bookRecs = new HashSet<String>();
		for (Profile profile : profiles){
			if (numBooksInCommon(user,profile)>=3){
				addUnread(user,profile,bookRecs);
			}
		}
		System.out.println("For the user "+user.name+":");
		System.out.println("Your Recommended book is '"+randomBook(bookRecs)+"'.");
		randomBook(bookRecs);
	}	
}

private String randomBook(Set<String> books){
	int x = new Random().nextInt(books.size());
	int i = 0;
	for (String book : books){
		if (i==x){
			return book;
		}
		i++;
	}
	System.out.println("Randomization out of bounds, possibly no books to choose from.");
	return null;
}

private int numBooksInCommon(Profile user1, Profile user2){
	Set<String> intersection = new HashSet<String>(user1.books);
	intersection.retainAll(user2.books);
	return intersection.size();
}

private void addUnread(Profile user1, Profile user2, Set<String> bookRecs){
	Set<String> unread = new HashSet<String>(user2.books);
	unread.removeAll(user1.books);
	bookRecs.addAll(unread);
}



private class Profile{
	String name;
	Set<String> books;
	
	Profile(String name){
		this.name = name;
		books = new HashSet<String>();
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

	
	
public static void main(String[] args){
	String filepath = "profiling-data.txt";
	Profiling database = new Profiling();
	database.parseFile(filepath);
	//database.nameLookUp("nels").get(0).printInfo();
	//database.displayBookReaders("war and peace");
	//database.recommendBook("nels");
	Set<String> temp = new HashSet<String>();
	database.addUnread(database.profiles.get(6),database.profiles.get(11),temp);
	database.recommendBook("dianna p");
}
}