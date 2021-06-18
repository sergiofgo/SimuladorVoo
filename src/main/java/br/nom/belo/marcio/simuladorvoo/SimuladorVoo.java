package br.nom.belo.marcio.simuladorvoo;

class Aviao implements Runnable {

    private Aeroporto aeroporto;
    private String idAviao;
    private long tempoVoo=0;

    public Aviao(Aeroporto aeroporto, String idAviao,long tempoVoo) {
        this.aeroporto = aeroporto;
        this.idAviao = idAviao;
        this.tempoVoo=tempoVoo;
    }

    public void run() {
        try {
            Thread.sleep(tempoVoo/2);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        decolar();
        voar();
        aterrisar();
        
    }

    private void decolar() {
        System.out.println(idAviao + ": esperando pista...");
        String acao=idAviao + ": decolando...";
        aeroporto.esperarPistaDisponivel(acao); // Espera uma pista livre
    }

    private void voar() {
        System.out.println(idAviao + ": voando...");
        try {
            Thread.sleep(tempoVoo);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    private void aterrisar() {
        System.out.println(idAviao + ": esperando pista...");
        String acao=idAviao + ": aterissando...";
        aeroporto.esperarPistaDisponivel(acao); // Espera uma pista livre
    }
}

class Aeroporto implements Runnable {

    private boolean temPistaDisponivel = true;
    private String nomeAeroporto;

    public Aeroporto(String nomeAeroporto) {
        this.nomeAeroporto = nomeAeroporto;
    }

    public synchronized void esperarPistaDisponivel(String acao) {
        System.out.println(acao);
    }

    public synchronized void mudarEstadoPistaDisponivel() {
        // Inverte o estado da pista.
        temPistaDisponivel = !temPistaDisponivel;
        System.out.println(nomeAeroporto + " tem pista disponível: " + 
                (temPistaDisponivel == true ? "Sim" : "Não"));
        // Notifica a mudanca de estado para quem estiver esperando.
        if(temPistaDisponivel) this.notify();
    }

    public void run() {
        System.out.println("Rodando aeroporto " + nomeAeroporto);
        while (true) {
            try {
                mudarEstadoPistaDisponivel();
                // Coloca a thread aeroporto dormindo por um tempo de 0 a 5s
                Thread.sleep((int)(Math.random()*5000)); 
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }

}
/*
 * Simulador de voo com threads
 */
public final class SimuladorVoo {

    public static void main(String[] args) {

        System.out.println("Rodando simulador de voo.");

        // Constroi aeroporto e inicia sua execucao.
        // NÃO MEXER NESSE TRECHO
        Aeroporto santosDumont = new Aeroporto("Santos Dumont");
        Thread threadAeroporto = new Thread(santosDumont);

        // Constrói aviao e inicia sua execucao.
        // NÃO MEXER NESSE TRECHO
        Aviao aviao14bis = new Aviao(santosDumont, "Avião 14BIS",10000);
        Thread thread14bis = new Thread(aviao14bis);

        // Inicia as threads
        threadAeroporto.start();
        thread14bis.start();

        try {
            // Junta-se ao término da execução da thread do aeroporto
            threadAeroporto.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Terminando thread principal.");

    }
}