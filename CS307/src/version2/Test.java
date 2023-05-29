package version2;

public class Test {
    public static void main(String[] args) {
            MyThread myThread = new MyThread();
            MyThread myThread1 = new MyThread();
            myThread.start();
            myThread1.start();

    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100000000; i++) {
            System.out.println(i);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//                try {
//                    sleep(200000000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

    }

}
