package br.nom.belo.marcio.simuladorvoo;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Aviao implements Runnable {

    private Aeroporto aeroporto;
    private String idAviao;
    private long tempoVoo=0;
    private static final Logger LOG = LoggerFactory.getLogger( "Aviao");

    public Aviao(Aeroporto aeroporto, String idAviao,long tempoVoo) {
        this.aeroporto = aeroporto;
        this.idAviao = idAviao;
        this.tempoVoo=tempoVoo;
    }

    public void run() {

        try {
            
            Thread.sleep( tempoVoo / 2);
        } catch (InterruptedException ie) {
            
            LOG.info( AppMessages.MSG_THREAD_INTERROMPIDA);
            Thread.currentThread().interrupt();
        }
        decolar();
        voar();
        aterrisar();
    }

    private void decolar() {
        LOG.info( "{}: esperando pista...", idAviao);
        String acao = idAviao + ": decolando...";
        aeroporto.esperarPistaDisponivel( acao); // Espera uma pista livre
    }

    private void voar() {

        LOG.info( "{}: voando...", idAviao);
        try {
            
            Thread.sleep( tempoVoo);
        } catch (InterruptedException e) {
            
            LOG.error( AppMessages.MSG_THREAD_INTERROMPIDA);
            Thread.currentThread().interrupt();
        }
    }

    private void aterrisar() {

        LOG.info( "{}: esperando pista...", idAviao);
        String acao = idAviao + ": aterissando...";
        aeroporto.esperarPistaDisponivel( acao); // Espera uma pista livre
    }
}

class Aeroporto implements Runnable {

    private boolean temPistaDisponivel = true;
    private String nomeAeroporto;
    private static final Logger LOG = LoggerFactory.getLogger( "Aeroporto");
    private Random random = new Random();

    public Aeroporto(String nomeAeroporto) {
        
        this.nomeAeroporto = nomeAeroporto;
    }

    public synchronized void esperarPistaDisponivel(String acao) {
        
        LOG.info( acao);
    }

    public synchronized void mudarEstadoPistaDisponivel() {
        
        // Inverte o estado da pista.
        temPistaDisponivel = !temPistaDisponivel;

        LOG.info( "{} tem pista disponível? {}", nomeAeroporto, (temPistaDisponivel ? "Sim" : "Não"));

        // Notifica a mudanca de estado para quem estiver esperando.
        if( temPistaDisponivel) this.notify();
    }

    public void run() {

        LOG.info( "Rodando aeroporto {}", nomeAeroporto);
        
        while (true) {
            try {
                mudarEstadoPistaDisponivel();
                // Coloca a thread aeroporto dormindo por um tempo de 0 a 5s
                Thread.sleep( random.nextInt( 5000)); 
            } catch (InterruptedException e) {

                LOG.error( AppMessages.MSG_THREAD_INTERROMPIDA);
                Thread.currentThread().interrupt();
            }
        }
    }
}
/*
 * Simulador de voo com threads
 */
public final class SimuladorVoo {
    
    private static final Logger LOG = LoggerFactory.getLogger( "SimuladorVoo");

    public static void main(String[] args) {

        LOG.info( "Rodando simulador de voo.");

        // Constroi aeroporto e inicia sua execucao.
        // NÃO MEXER NESSE TRECHO
        Aeroporto santosDumont = new Aeroporto( "Santos Dumont");
        Thread threadAeroporto = new Thread( santosDumont);

        // Constrói aviao e inicia sua execucao.
        // NÃO MEXER NESSE TRECHO
        Aviao aviao14bis = new Aviao( santosDumont, "Avião 14BIS",10000);
        Thread thread14bis = new Thread( aviao14bis);

        // Inicia as threads
        threadAeroporto.start();
        thread14bis.start();

        try {
            
            // Junta-se ao término da execução da thread do aeroporto
            threadAeroporto.join();
        } catch (InterruptedException ex) {
            
            LOG.error( AppMessages.MSG_THREAD_INTERROMPIDA);
            Thread.currentThread().interrupt();
        }
        LOG.info( "Terminando thread principal.");
    }
}

class AppMessages {

    static final String MSG_THREAD_INTERROMPIDA = "Thread interrompida";

    private AppMessages() { }
}