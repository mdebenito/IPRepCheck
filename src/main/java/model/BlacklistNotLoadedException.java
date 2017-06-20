package model;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
public class BlacklistNotLoadedException extends Exception {
    public BlacklistNotLoadedException(){
        super("The blacklist you are trying to check has not been loaded.");
    }
}
