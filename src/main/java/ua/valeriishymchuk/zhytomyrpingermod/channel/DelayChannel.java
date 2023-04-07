package ua.valeriishymchuk.zhytomyrpingermod.channel;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayChannel extends ChannelDuplexHandler {

    private static int DELAY = 200;

    private int packetIdIn = 0;
    private int packetIdOut = 0;
    private int currentDelay;

    private final DelayQueue<DelayedRunnable> inQueue = new DelayQueue<>();
    private final DelayQueue<DelayedRunnable> outQueue = new DelayQueue<>();
    private boolean isClosed = false;

    public DelayChannel() {
        currentDelay = DELAY;
        runQueueCheck(inQueue);
        runQueueCheck(outQueue);
    }

    private void runQueueCheck(DelayQueue<DelayedRunnable> queue) {
        runThread(() -> {
            while (!isClosed) {
                DelayedRunnable runnable = queue.take();
                try {
                    runnable.runnable.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void runThread(CheckedRunnable runnable) {
        Thread thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void runTask(int id, CheckedRunnable checkedRunnable, DelayQueue<DelayedRunnable> queue) {
        if (currentDelay == 0) {
            try {
                checkedRunnable.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return;
        }
        queue.add(new DelayedRunnable(id, System.currentTimeMillis() + DELAY, checkedRunnable));
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        runTask(++packetIdIn, () -> ctx.fireChannelRead(msg), inQueue);
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        runTask(++packetIdOut,() -> ctx.write(msg, promise), outQueue);
    }

    public static void setDelay(int DELAY) {
        DelayChannel.DELAY = DELAY;
    }

    public static int getDelay() {
        return DELAY;
    }

    private interface CheckedRunnable {

        void run() throws Throwable;

    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        isClosed = true;
    }

    private static class DelayedRunnable implements Delayed {

        private final int id;
        private final long startTime;
        private final CheckedRunnable runnable;

        private DelayedRunnable(int id, long startTime, CheckedRunnable runnable) {
            this.id = id;
            this.startTime = startTime;
            this.runnable = runnable;
        }


        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            long diff = startTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(@NotNull Delayed o) {
            DelayedRunnable obj = (DelayedRunnable) o;
            return id - obj.id;
        }
    }

}
