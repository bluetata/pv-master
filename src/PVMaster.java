import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.net.Proxy;
import java.net.URL;


public class PVMaster {

    private static int totalExecutionCount = 0;
    private static final int MAX_EXECUTIONS = 900000000;  // 最大执行次数
    private static int fixedSleepTime = 10000;  //休眠时间
    private static int minSleepTime = 61000;    //休眠最小时间
    private static int maxSleepTime = 91000;    //休眠最大时间
    private static final int MAX_CONCURRENT_THREADS = 30; // 控制并发的线程数量
    private static final int MAX_ACCESS_PER_THREAD = MAX_EXECUTIONS / MAX_CONCURRENT_THREADS;


    // Replace with your actual proxy server details
    private static final String LOCAL_PROXY_HOST = "localhost"; // Localhost
    private static final int LOCAL_PROXY_PORT = 8888;           // Replace with any available port

    public static void main(String[] args) {

        String[] targetUrls = {
                // "http://example.com",  // 替换为您要访问的网页URL
                // "http://example2.com"  // 添加更多URL
                "https://bluetata.blog.csdn.net/article/details/132352779?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/132178622?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/132258964?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/132068024?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131745835?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131623399?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131346261?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131298521?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131012518?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131275051?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/131160750?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/130541151?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/129605505?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/129495550?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/129430372?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/129053070?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128537280?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128526816?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128392113?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128309595?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128294793?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/127694084?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/125876960?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/127255358?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/127197088?spm=1001.2014.3001.5502",
                "https://bluetata.blog.csdn.net/article/details/128294793",
                "https://bluetata.blog.csdn.net/article/details/124871026",
                "https://bluetata.blog.csdn.net/article/details/126268104",
                "https://bluetata.blog.csdn.net/article/details/126886976",
                "https://bluetata.blog.csdn.net/article/details/127077199"
        };



        Map<String, Integer> urlCounters = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        Semaphore semaphore = new Semaphore(MAX_ACCESS_PER_THREAD); // 控制每个线程的访问次数
        double sleepTimeFactor = (double) MAX_EXECUTIONS / (MAX_CONCURRENT_THREADS * MAX_ACCESS_PER_THREAD);

        System.out.println("sleepTimeFactor>>>>" + sleepTimeFactor);

        for (String targetUrl : targetUrls) {
            urlCounters.put(targetUrl, 0);
            executorService.submit(() -> {
                for (int i = 0; i < MAX_EXECUTIONS; i++) {
                    Random random = new Random();
                    int randomSleepTime = random.nextInt(maxSleepTime - minSleepTime + 1) + minSleepTime;

                    try {
                        semaphore.acquire(); // 获取信号量许可
                        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
//                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(LOCAL_PROXY_HOST, LOCAL_PROXY_PORT));
//                        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection(proxy);
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();

                        if (responseCode == 200) {
                            System.out.println("Successfully accessed " + targetUrl);
                        } else {
                            System.out.println("Failed to access " + targetUrl + ". Response code: " + responseCode);
                        }

                        int adjustedSleepTime = (int) (randomSleepTime * sleepTimeFactor);
                        System.out.println("进入【线程随机】睡眠休息>>>>" + adjustedSleepTime);
                        Thread.sleep(adjustedSleepTime);

                        connection.disconnect();
                    } catch (IOException | InterruptedException e) {
                        System.out.println("An error occurred: " + e.getMessage());
                    } finally {
                        semaphore.release(); // 释放信号量许可
                    }

                    try {
                        System.out.println("进入【固定】睡眠休息>>>>" + fixedSleepTime);
                        Thread.sleep(fixedSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    urlCounters.put(targetUrl, urlCounters.get(targetUrl) + 1);
                    System.out.println(targetUrl + " 执行次数 execution count: " + urlCounters.get(targetUrl));
                }
            });
        }

        executorService.shutdown();
    }
}



















