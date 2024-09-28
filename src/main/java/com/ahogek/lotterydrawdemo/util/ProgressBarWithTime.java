package com.ahogek.lotterydrawdemo.util;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author AhogeK ahogek@gmail.com
 * @since 2024-09-28 12:43:51
 */
public class ProgressBarWithTime {

    private final long startTime;
    private final long totalCount;
    private final int width;

    public ProgressBarWithTime(long totalCount, int width) {
        this.totalCount = totalCount;
        this.width = width;
        this.startTime = System.currentTimeMillis();
    }

    public void updateProgressBar(BufferedWriter writer, long count) throws IOException {
        double progress = (double) count / totalCount;
        int completedWidth = (int) (width * progress);

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        long estimatedTotalTime = (long) (elapsedTime / progress);
        long remainingTime = estimatedTotalTime - elapsedTime;

        StringBuilder sb = new StringBuilder("\r[");
        for (int i = 0; i < width; i++) {
            if (i < completedWidth) {
                sb.append("#");
            } else {
                sb.append(" ");
            }
        }
        sb.append("] ")
                .append(String.format("%.2f%%", progress * 100))
                .append(" 剩余时间: ")
                .append(formatDuration(remainingTime));

        writer.write(sb.toString());
        writer.flush();
    }

    private String formatDuration(long millis) {
        return String.format("%02d:%02d:%02d",
                millis / 3600000,
                (millis % 3600000) / 60000,
                (millis % 60000) / 1000);
    }
}
