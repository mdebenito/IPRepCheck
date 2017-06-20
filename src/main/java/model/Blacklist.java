package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
public class Blacklist {
    private String url;
    private String name;
    private URL parsedURL;
    private List<BlacklistEntry> entries;
    private static String COMMENT_CHAR="#";
    private boolean isLoaded;

    public Blacklist(String url) throws MalformedURLException {
        this.url = url;
        this.name = url;
        this.parsedURL = new URL(this.url);
        this.entries = new ArrayList<BlacklistEntry>();
    }
    public Blacklist(String url, String name) throws MalformedURLException {
        this.url = url;
        this.name = name;
        this.parsedURL = new URL(this.url);
        this.entries = new ArrayList<BlacklistEntry>();
    }

    public void load() throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(parsedURL.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            try {
                if(!inputLine.startsWith(COMMENT_CHAR)) {
                    this.entries.add(new BlacklistEntry(inputLine));
                }
            } catch (InvalidBlacklistEntryException e) {
                e.printStackTrace();
            }
        }
        in.close();
        isLoaded=true;
    }



    public boolean isBlacklisted(String ipAddress) throws BlacklistNotLoadedException {
        if(!isLoaded)
            throw new BlacklistNotLoadedException();
        for(BlacklistEntry entry : entries){
            if(entry.isBlacklisted(ipAddress))
                return true;
        }
        return false;
    }

    public String toString(){
        return this.name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
