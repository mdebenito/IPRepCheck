package model;

import org.apache.commons.net.util.SubnetUtils;

import java.util.regex.Pattern;

/**
 * Created by Mario de Benito on 19/06/2017.
 */
class BlacklistEntry {
    private final boolean isRange;
    private final String raw;
    private SubnetUtils.SubnetInfo subnetInfo;

    public BlacklistEntry(String raw) throws InvalidBlacklistEntryException {
        this.raw = raw;
        this.isRange = isValidCIDRRange(raw);
        if(!isValidIp(raw) && !isRange)
            throw new InvalidBlacklistEntryException(raw);
        if(isRange){
            SubnetUtils snu = new SubnetUtils(this.raw);
            subnetInfo = snu.getInfo();
        }


    }

    public boolean isRange() {
        return isRange;
    }

    public boolean isBlacklisted(String ipAddress){
        if(!isRange){
            if(ipAddress.equalsIgnoreCase(this.raw)){
                return true;
            }
        }else{
            return subnetInfo.isInRange(ipAddress);

        }
        return false;
    }

    public boolean equals(Object other){
        if(!(other instanceof  BlacklistEntry))
            return false;
        return ((BlacklistEntry) other).getRaw().equalsIgnoreCase(this.raw);

    }

    private String getRaw() {
        return raw;
    }

    /**
     * Checks if a string is a valid IP address
     * @param line String to check
     * @return true if the string is a valid IP address
     */
    private boolean isValidIp(String line) {
        final String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        boolean ipV4 = pattern.matcher(line).matches();
        if(!ipV4){
            final String IPV6_PATTERN = "(" +
                    "([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
                    "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                    ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
                    "::(ffff(:0{1,4}){0,1}:){0,1}" +
                    "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|" +
                    "([0-9a-fA-F]{1,4}:){1,4}:" +
                    "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])" +
                    ")";
            pattern = Pattern.compile(IPV6_PATTERN);
            return pattern.matcher(line).matches();
        }
        return true;
    }

    /**
     * Checks if a string is a valid CIDR range
     * @param line String to check
     * @return true if the string is a valid CIDR range
     */
    private boolean isValidCIDRRange(String line){

        final String CIDR_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])(/([0-9]|[1-2][0-9]|3[0-2]))$";
        Pattern pattern = Pattern.compile(CIDR_PATTERN);
        return pattern.matcher(line).matches();
    }
}
