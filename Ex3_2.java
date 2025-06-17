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

//classe do método main
public class Ex3_2  {
  private static final int N = 3;
  private static final int NTHREADS = 10;

  public static void main(String[] args) {
    //cria um pool de threads (NTHREADS)
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

    // Números só para testar mesmo
    long[] numeros = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 104729};

    //cria uma lista para armazenar referencias de chamadas assincronas
    List<Future<Long>> list = new ArrayList<Future<Long>>();
    List<Future<Boolean>> list1 = new ArrayList<Future<Boolean>>();

    for (int i = 0; i < N; i++) {
      Callable<Long> worker = new MyCallable();
      Future<Long> submit = executor.submit(worker);
      list.add(submit);
    }

    for (long num : numeros) {
      Callable<Boolean> task = new PrimoCallable(num);
      Future<Boolean> future = executor.submit(task);
      list1.add(future);
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

    for (int i = 0; i < numeros.length; i++) {
      try {
        boolean isPrimo = list1.get(i).get();
        System.out.println(numeros[i] + (isPrimo ? " é primo" : " não é primo"));
      } catch (InterruptedException | ExecutionException e) {
        System.err.println("Erro ao verificar o número " + numeros[i]);
        e.printStackTrace();
      }
    }
    System.out.println(sum);
    executor.shutdown();
  }
}