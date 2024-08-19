package edu.eci.arsw.blacklistvalidator;
import java.util.LinkedList;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 * Clase de tipo Thread que representa el ciclo de vida de un hilo.
 * Realiza la búsqueda de un segmento del conjunto de servidores disponibles.
 */
public class MaliciousHostCounter extends Thread {
    private String ip;
    private int startIdx, endIdx;
    private int foundCount;
    private int checkedCount;
    private HostBlacklistsDataSourceFacade dataSource;
    private LinkedList<Integer> occurrences;
    /**
     * Constructor de la clase.
     * @param dataSource Fuente de datos de listas negras.
     * @param ip Dirección IP a buscar.
     * @param startIdx Índice de inicio del segmento a revisar.
     * @param endIdx Índice de fin del segmento a revisar.
     */
    public MaliciousHostCounter(HostBlacklistsDataSourceFacade dataSource, String ip, int startIdx, int endIdx) {
        this.dataSource = dataSource;
        this.ip = ip;
        this.startIdx = startIdx;
        this.endIdx = endIdx;   
        this.occurrences = new LinkedList<>();
        foundCount = 0;
        checkedCount = 0;
    }

    /**
     * Ejecución del Thread.
     */
    @Override
    public void run() {
        int localFoundCount = 0;
        int localCheckedCount = 0;
    
        for (int i = startIdx; i < endIdx; i++) {
            localCheckedCount++;
            if (dataSource.isInBlackListServer(i, ip)) {
                occurrences.add(i);
                localFoundCount++;
            } 
        }
        this.checkedCount = localCheckedCount;
        this.foundCount = localFoundCount;
    }
    
    /**
     * Método que permite preguntar cuántas ocurrencias de servidores maliciosos ha encontrado.
     * @return cantidad de ocurrencias encontradas.
     */
    public int getFoundCount() {
        return foundCount;
    }

    /**
     * Método que permite preguntar cuántas listas de servidores maliciosos ha revisado.
     * @return cantidad de listas revisadas.
     */
    public int getCheckedCount() {
        return checkedCount;
    }

    /**
     * Método que permite obtener las posiciones dentro de la lista donde se encontró la IP.
     * @return posiciones dentro de la lista (int).
     */
    public LinkedList<Integer> getOccurrences() {
        return occurrences;
    }
}
