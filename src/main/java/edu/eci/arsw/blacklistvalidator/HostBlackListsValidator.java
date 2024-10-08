/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {
    private int checkedCount=0;
    private int ocurrencesCount=0;
    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */


    public List<Integer> checkHost(String ipaddress, int N){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

        int partir = skds.getRegisteredServersCount() / N;

        List<MaliciousHostCounter> maliciousHosts = new ArrayList<>();
        
        int start,end;

        for (int i=0;i < N ;i++){
           start = i * partir;
           if (i == N - 1){
                end = skds.getRegisteredServersCount();
           }else{
                end = (i + 1) * partir;
           }
           MaliciousHostCounter host = new MaliciousHostCounter(skds, ipaddress, start, end);
           maliciousHosts.add(host);
        }

        for (MaliciousHostCounter host : maliciousHosts) {
            host.start();
        }
        
        for (MaliciousHostCounter host : maliciousHosts) {
            try {
                host.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            blackListOcurrences.addAll(host.getOccurrences());
            
                for (Integer e: host.getOccurrences()) {
                    if (!blackListOcurrences.contains(e)) {
                        blackListOcurrences.add(e);
                    }
                }
                checkedCount += host.getCheckedCount();
                ocurrencesCount += host.getFoundCount();
        }

        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
