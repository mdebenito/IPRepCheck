import api.IPRepCheck;
import api.IPRepCheckListener;
import model.Blacklist;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by tf05046 on 19/06/2017.
 */
public class BlacklistTest implements IPRepCheckListener {

    @Test
    public void isIPBlacklistedMonoTest(){
        IPRepCheck repcheck = new IPRepCheck();
        try {
            repcheck.addBlacklist(new Blacklist("https://raw.githubusercontent.com/ktsaou/blocklist-ipsets/master/firehol_level1.netset"));
            boolean v1 = repcheck.isBlacklisted("220.154.0.20");
            assertEquals("IP not found",true,v1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void areBlacklistedTest(){

        List<String> ips = new ArrayList<>();

        for(int i=0; i<9;i++) {
            ips.add("217.175.8.10" + i);
            ips.add("217.175.8.15" + i);
        }
        IPRepCheck repcheck = new IPRepCheck();
        repcheck.addListener(this);
        try {
            repcheck.addBlacklist(new Blacklist("https://raw.githubusercontent.com/ktsaou/blocklist-ipsets/master/firehol_level1.netset"));
            repcheck.addBlacklist(new Blacklist("https://lists.blocklist.de/lists/all.txt"));
            repcheck.areBlacklisted(ips);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void isBlacklisted(String ipAddress, Blacklist bl, boolean blacklisted) {
        if(blacklisted)
            System.out.println(ipAddress+" is blacklisted in "+bl.toString());
        else
            System.out.println(ipAddress+" is NOT blacklisted in "+bl.toString());
    }

    public void progressUpdate(int count, int size) {
        System.out.println("Progress: "+count+"/"+size);
    }
}
