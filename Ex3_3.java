import java.util.concurrent.*;
import java.util.*;

//classe runnable
class MyCallable implements Callable<Long> {
    //construtor
    MyCallable() {
    }

    //método para execução
    public Long call() throws Exception {
        long s = 0;
        for (long i=1; i<=100; i++) {
            s++;
        }
        return s;
    }
}

class PrimoCallable implements Callable<Boolean> {
    private final long numero;

    public PrimoCallable(long numero) {
        this.numero = numero;
    }

    @Override
    public Boolean call() {
        return ehPrimo(numero);
    }

    private boolean ehPrimo(long n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (long i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}

class PrimosCallable implements Callable<Long> {
    private final long inicio;
    private final long fim;

    public PrimosCallable(long inicio, long fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public Long call() {
        long count = 0;
        for (long i = inicio; i <= fim; i++) {
            if (new PrimoCallable(i).call()) {
                count++;
            }
        }
        return count;
    }
}

//classe do método main
public class Ex3_3  {
    private static final int N = 3;
    private static final int NTHREADS = 10;
    private static final long LIMITE_SUPERIOR = 1000000;

    public static void main(String[] args) {
        //cria um pool de threads (NTHREADS)
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

        //cria uma lista para armazenar referencias de chamadas assincronas
        List<Future<Long>> list = new ArrayList<Future<Long>>();
        List<Future<Long>> contador = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            Callable<Long> worker = new MyCallable();
            Future<Long> submit = executor.submit(worker);
            list.add(submit);
        }

        long tamanhoParte = LIMITE_SUPERIOR / NTHREADS;
        long inicio = 1;
        long totalPrimos = 0;

        for (int i = 0; i < NTHREADS; i++) {
            long fim = (i == NTHREADS - 1) ? LIMITE_SUPERIOR : inicio + tamanhoParte - 1;
            contador.add(executor.submit(new PrimosCallable(inicio, fim)));
            inicio = fim + 1;
        }

        //recupera os resultados e faz o somatório final
        long sum = 0;
        for (Future<Long> future : list) {
            try {
                sum += future.get(); //bloqueia se a computação nao tiver terminado
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        for (Future<Long> future : contador) {
            try {
                totalPrimos += future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Erro ao contar primos");
                e.printStackTrace();
            }
        }

        System.out.println(sum);
        System.out.println("Quantidade de números primos até " + LIMITE_SUPERIOR + ": " + totalPrimos);

        executor.shutdown();
    }
}