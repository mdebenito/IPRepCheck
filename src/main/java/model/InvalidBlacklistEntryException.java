package model;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
class InvalidBlacklistEntryException extends Exception{

    public InvalidBlacklistEntryException(String rawEntry) {
        super("Invalid Blacklist entry: '"+rawEntry+"'.");
    }
}
