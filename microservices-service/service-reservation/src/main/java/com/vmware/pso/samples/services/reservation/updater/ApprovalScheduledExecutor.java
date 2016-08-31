package com.vmware.pso.samples.services.reservation.updater;

import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vmware.pso.samples.core.model.Reservation;

public class ApprovalScheduledExecutor extends ScheduledThreadPoolExecutor {

    @Autowired
    protected ApplicationContext context;

    private final Logger LOG = Logger.getLogger(ApprovalScheduledExecutor.class);

    private final AtomicBoolean paused = new AtomicBoolean(); // default is false

    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unpausedLockCondition = pauseLock.newCondition();

    public ApprovalScheduledExecutor(final int corePoolSize) {
        super(corePoolSize);
    }

    // dynamically load new task from context and schedule reservation for approval
    public UUID scheduleReservationForApproval(final Reservation reservation) {
        final ApprovalTask approvalTask = ((ApprovalTask) context.getBean("approvalTask", reservation.getId()));

        LOG.debug("Scheduling approval : " + approvalTask.getApprovalId());

        schedule(approvalTask, 8, TimeUnit.SECONDS);

        return approvalTask.getApprovalId();
    }

    @Override
    protected void beforeExecute(final Thread t, final Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (paused.get()) {
                unpausedLockCondition.await();
            }
        } catch (final InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public boolean isPaused() {
        return paused.get();
    }

    public void pause() {
        pauseLock.lock();
        try {
            paused.set(true);
            ;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            paused.set(false);
            unpausedLockCondition.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

}
