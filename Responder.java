import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Eric Brown
 * @version 11-27-2022
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";

    private static final String RESPONSE_MAP_FILE = "response_map.txt";

    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Populate the response map with all known keywords from those 
     * defined in a response map file, with each discrete entry separated
     * by a blank line. 
     * 
     */
    private void fillResponseMap()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(RESPONSE_MAP_FILE);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = reader.readLine();
            String workingLine = "";
            String entrySeparator = "";
            boolean firstLine = true;
            ArrayList<String> keys = new ArrayList<String>();
            String value = "";
            while(line != null) {

                workingLine = line.trim();
                line = reader.readLine();
                if (firstLine){
                    if (workingLine.length() > 0){
                        keys = new ArrayList<String>(Arrays.asList(workingLine.split(",")));
                        firstLine = false;}
                }
                else if (workingLine.equals(entrySeparator) || line == null){                    
                    value += "\n";
                    for(String key : keys){
                        responseMap.put(key,value);
                    }
                    value = "";
                    firstLine = true;
                }
                else{  
                    value += "\n" + workingLine;    
                }

            } 
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + RESPONSE_MAP_FILE);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                RESPONSE_MAP_FILE);
        }
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = reader.readLine();
            String workingLine = "";
            String response = "";
            String entrySeparator = "";
            while(line != null) {
                workingLine = line.trim();
                if (line.equals(entrySeparator) && !response.equals("")){
                    if (response.trim().length() > 0){
                        defaultResponses.add(response);
                        response = "";
                    }
                }
                else{
                    if (workingLine.length() > 0){
                        response += "\n" + workingLine;
                    }
                }
                line = reader.readLine();
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
