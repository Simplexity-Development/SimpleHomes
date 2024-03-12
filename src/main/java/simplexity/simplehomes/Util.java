package simplexity.simplehomes;

import java.util.List;

public class Util {
    
    public static boolean homeExists(List<Home> homes, String homeName) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                return true;
            }
        }
        return false;
    }

}
