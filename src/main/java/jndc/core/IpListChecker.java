package jndc.core;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class IpListChecker {

    private Set<String> blackSet=new HashSet<>();

    private Set<String> whiteSet=new HashSet<>();//higher priority

    public void loadRule(String[] blackList,String[] whiteList){
        Stream.of(blackList).forEach(x->{
            blackSet.add(x);
        });

        Stream.of(whiteList).forEach(x->{
            whiteSet.add(x);
        });
    }

    public boolean checkIpAddress(String ipAddress){
        if (whiteSet.size()>0){
            boolean inWhiteList = whiteSet.contains(ipAddress);
            return inWhiteList;
        }

        boolean notInBlackList = !blackSet.contains(ipAddress);
        return notInBlackList;
    }
}
