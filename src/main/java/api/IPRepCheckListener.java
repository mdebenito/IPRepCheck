package api;

import model.Blacklist;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
public interface IPRepCheckListener {
    void isBlacklisted(String ipAddress, Blacklist bl, boolean blacklisted);

    void progressUpdate(int count, int size);
}
