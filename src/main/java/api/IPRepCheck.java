package api;

import model.Blacklist;
import model.BlacklistNotLoadedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
public class IPRepCheck {

    private static final int MAX_THREADS = 8;

    private final List<Blacklist> blacklists;
    private final ThreadPoolExecutor executor;

    public void addBlacklist(Blacklist bl) throws IOException {
        bl.load();
        this.blacklists.add(bl);
    }

    private final List<IPRepCheckListener> listeners;

    public void addListener(IPRepCheckListener l){
        listeners.add(l);
    }

    public IPRepCheck(){
        this.blacklists = new ArrayList<>();
        this.listeners = new ArrayList<>();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
    }

    private void isBlacklistedCallback(String ipAddress, Blacklist bl, boolean blacklisted){
        for(IPRepCheckListener l : listeners){
            l.isBlacklisted(ipAddress,bl,blacklisted);
        }
    }

    private void isBlacklistedMT(final String ipAddress){
        for(final Blacklist bl : blacklists){
            Thread worker = new Thread(() -> {
                try {
                    boolean blacklisted = bl.isBlacklisted(ipAddress);
                    isBlacklistedCallback(ipAddress,bl,blacklisted);
                } catch (BlacklistNotLoadedException e) {
                    e.printStackTrace();
                }
            });
            executor.execute(worker);
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
            int finalCount = count;
            Thread worker = new Thread(() -> {
                isBlacklistedMT(ipAddress);
                publishProgress(finalCount,ipAddresses.size());

            });
           executor.execute(worker);
        }

    }

    private void publishProgress(int count, int size) {
        for(IPRepCheckListener l : listeners){
            l.progressUpdate(count,size);
        }
    }
}
