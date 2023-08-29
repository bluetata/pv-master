import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class PVMaster {

//    private static int totalExecutionCount = 0;
    private static final int MAX_EXECUTIONS = 900000000;  // 最大执行次数
    private static final int fixedSleepTime = 10000;    //休眠时间
//    private static final int minSleepTime = 81000;    //休眠最小时间
//    private static final int maxSleepTime = 91000;    //休眠最大时间
    private static final int MAX_CONCURRENT_THREADS = 30; // 控制并发的线程数量
    private static final int MAX_ACCESS_PER_THREAD = MAX_EXECUTIONS / MAX_CONCURRENT_THREADS;


    // Replace with your actual proxy server details
//    private static final String LOCAL_PROXY_HOST = "localhost"; // Localhost
//    private static final int LOCAL_PROXY_PORT = 8888;           // Replace with any available port

    public static void main(String[] args) {

        String[] targetUrls = {
                // "http://example.com",  // 替换为您要访问的网页URL
                // "http://example2.com"  // 添加更多URL
                "https://blog.csdn.net/dietime1943/article/details/132352779?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/132178622?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/132258964?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/132068024?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131745835?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131623399?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131346261?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131298521?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131012518?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131275051?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/131160750?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/130541151?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/129605505?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/129495550?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/129430372?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/129053070?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128537280?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128526816?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128392113?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128309595?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128294793?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/127694084?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/125876960?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/127255358?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/127197088?spm=1001.2014.3001.5502",
                "https://blog.csdn.net/dietime1943/article/details/128294793",
                "https://blog.csdn.net/dietime1943/article/details/124871026",
                "https://blog.csdn.net/dietime1943/article/details/126268104",
                "https://blog.csdn.net/dietime1943/article/details/126886976",
                "https://blog.csdn.net/dietime1943/article/details/132531011"
        };

        // 获取当前时间以及最小间隔时间段（分）
        AtomicReference<LocalTime> currentInitTime = new AtomicReference<>(LocalTime.now());
        AtomicInteger minIntervalTime = new AtomicInteger(calculateIntVariableValue(currentInitTime.get()));

        // 计算随机时间（毫秒），增加了上下差值 1分钟，即如果当前设置的间隔时间是2分钟，那么会取出1~3分钟内的随机分钟数再转换成毫秒
        AtomicInteger minSleepTime = new AtomicInteger((minIntervalTime.get() - 1) * 60 * 1000);    // 休眠最小时间
        AtomicInteger maxSleepTime = new AtomicInteger((minIntervalTime.get() + 1) * 60 * 1000);    // 休眠最大时间


        Map<String, Integer> urlCounters = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_CONCURRENT_THREADS);
        Semaphore semaphore = new Semaphore(MAX_ACCESS_PER_THREAD); // 控制每个线程的访问次数
        double sleepTimeFactor = (double) MAX_EXECUTIONS / (MAX_CONCURRENT_THREADS * MAX_ACCESS_PER_THREAD);

        System.out.println("sleepTimeFactor>>>>" + sleepTimeFactor);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



        for (String targetUrl : targetUrls) {

            urlCounters.put(targetUrl, 0);
            executorService.submit(() -> {
                for (int i = 0; i < MAX_EXECUTIONS; i++) {
                    LocalDateTime currentTime;
                    String formattedTime;
                    Random random = new Random();
                    // 计算随机时间（毫秒），增加了上下差值1分钟，即如果当前设置的间隔时间是2分钟，那么会取出1~3分钟内的随机分钟数再转换成毫秒
                    int randomSleepTime = random.nextInt(maxSleepTime.get() - minSleepTime.get() + 1) + minSleepTime.get();

                    try {
                        semaphore.acquire(); // 获取信号量许可
                        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection();
//                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(LOCAL_PROXY_HOST, LOCAL_PROXY_PORT));
//                        HttpURLConnection connection = (HttpURLConnection) new URL(targetUrl).openConnection(proxy);
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();

                        if (responseCode == 200) {
                            currentTime = LocalDateTime.now();
                            formattedTime = currentTime.format(formatter);
                            System.out.println("[" + formattedTime + "] Successfully accessed. " + targetUrl);
                        } else {
                            System.out.println("Failed to access " + targetUrl + ". Response code: " + responseCode);
                        }

                        int adjustedSleepTime = (int) (randomSleepTime * sleepTimeFactor);
                        System.out.println("进入【线程随机】睡眠休息 >>>> " + adjustedSleepTime / 1000 + "s");
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

                    // 每执行6次，获取一次当前时间，并且重新计算最小和最大间隔时间，并重新生成随机间隔时间
                    if (urlCounters.get(targetUrl) % 6 == 0) {
                        System.out.println(">>>开始重新计算最小和最大间隔时间<<<");
                        // 获取当前时间以及最小间隔时间段（分）
                        currentInitTime.set(LocalTime.now());
                        minIntervalTime.set(calculateIntVariableValue(currentInitTime.get()));
                        // 计算随机时间（毫秒），增加了上下差值1分钟，即如果当前设置的间隔时间是2分钟，
                        // 那么会取出1~3分钟内的随机分钟数再转换成毫秒
                        minSleepTime.set((minIntervalTime.get() - 1) * 60 * 1000);    // 休眠最小时间
                        maxSleepTime.set((minIntervalTime.get() + 1) * 60 * 1000);    // 休眠最大时间
                        System.out.println(">>>重新计算后当前最小休眠时间：" + minSleepTime + " <<<");
                        System.out.println(">>>重新计算后当前最大休眠时间：" + maxSleepTime + " <<<");

                    }

                    System.out.println(targetUrl + " 执行次数 execution count: " + urlCounters.get(targetUrl));
                }
            });
        }

        executorService.shutdown();
    }

    private static int calculateIntVariableValue(LocalTime currentTime) {
        TimeRange[] timeRanges = {
                new TimeRange(LocalTime.of(0,  0),  LocalTime.of(7,  0),  10),
                new TimeRange(LocalTime.of(7,  0),  LocalTime.of(9,  0),  8),
                new TimeRange(LocalTime.of(9,  0),  LocalTime.of(12, 0),  3),
                new TimeRange(LocalTime.of(12, 0),  LocalTime.of(13, 30), 5),
                new TimeRange(LocalTime.of(13, 30), LocalTime.of(19, 30), 2),
                new TimeRange(LocalTime.of(19, 30), LocalTime.of(23, 59), 5)
        };

        int intVariableValue = 0;
        for (TimeRange timeRange : timeRanges) {
            if (timeRange.isWithinRange(currentTime)) {
                intVariableValue = timeRange.getValue();
                break;
            }
        }

        return intVariableValue;
    }


}

class TimeRange {
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int value;

    public TimeRange(LocalTime startTime, LocalTime endTime, int value) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.value = value;
    }

    public boolean isWithinRange(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }

    public int getValue() {
        return value;
    }
}


















