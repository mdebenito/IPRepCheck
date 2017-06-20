package api;

import model.Blacklist;
import model.BlacklistNotLoadedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
public class IPRepCheck {

    public static final int MAX_THREADS = 8;
    private static int CURRENT_THREADS = 0;

    private List<Blacklist> blacklists;

    public void addBlacklist(Blacklist bl) throws IOException {
        bl.load();
        this.blacklists.add(bl);
    }

    private List<IPRepCheckListener> listeners;

    public void addListener(IPRepCheckListener l){
        listeners.add(l);
    }

    public IPRepCheck(){
        this.blacklists = new ArrayList<Blacklist>();
        this.listeners = new ArrayList<IPRepCheckListener>();
    }

    protected final void isBlacklistedCallback(String ipAddress,Blacklist bl,boolean blacklisted){
        CURRENT_THREADS--;
        for(IPRepCheckListener l : listeners){
            l.isBlacklisted(ipAddress,bl,blacklisted);
        }
    }

    private void isBlacklistedMT(final String ipAddress){
        for(final Blacklist bl : blacklists){
            Thread worker = new Thread() {

                public void run() {
                    try {
                        boolean blacklisted = bl.isBlacklisted(ipAddress);
                        isBlacklistedCallback(ipAddress,bl,blacklisted);
                    } catch (BlacklistNotLoadedException e) {
                        e.printStackTrace();
                    }
                }
            };
            while(CURRENT_THREADS>=MAX_THREADS) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CURRENT_THREADS++;
            worker.start();
        }
    }
    public boolean isBlacklisted(String ipAddress){
        int count=0;
        for(final Blacklist bl : blacklists){
            count++;
            publishProgress(count,blacklists.size());
            try {
                if(bl.isBlacklisted(ipAddress)){
                    return true;
                }
            } catch (BlacklistNotLoadedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public void areBlacklisted(final List<String> ipAddresses){
        int count = 0;
        for(final String ipAddress : ipAddresses){
            count++;
            publishProgress(count,ipAddresses.size());
            Thread worker = new Thread() {
                public void run() {
                    isBlacklistedMT(ipAddress);
                    CURRENT_THREADS--;
                }
            };
            while(CURRENT_THREADS>=MAX_THREADS) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CURRENT_THREADS++;
            worker.start();
        }

    }

    private void publishProgress(int count, int size) {
        for(IPRepCheckListener l : listeners){
            l.progressUpdate(count,size);
        }
    }
}
