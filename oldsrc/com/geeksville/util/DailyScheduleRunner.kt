package com.geeksville.util

import kotlinx.coroutines.*
import mu.KotlinLogging
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class ScheduleEntry(val time: LocalTime, val toRun: () -> Unit)

private val logger = KotlinLogging.logger {}

/**
 * Runs a set of scheduled callbacks, every day at the same time.  If the schedule is changed
 * any existing timers will be properly restarted.
 */
class DailyScheduleRunner {

    @Volatile
    var schedule = arrayOf<ScheduleEntry>()
        set(value) {
            // In-place sort
            value.sortBy { it.time }

            // Swap to the new array
            field = value

            // Ask any running jobs to exit
            val curJob = scheduledJob
            if (curJob != null && curJob.isActive) {
                logger.debug { "Asking existing schedule to cancel" }
                curJob.cancel()

                // FIXME - do I need to wait for the job? hmm possible threading problem
            }

            scheduleNext()
        }

    @Volatile
    private var scheduledJob: Job? = null

    private fun scheduleNext(currentTime: LocalTime = LocalTime.now()) {

        val curSchedule = schedule // In case it gets swapped by another thread
        if (curSchedule.isEmpty())
            logger.debug { "Schedule is empty - not scheduling job" }
        else {
            val now = LocalTime.now()

            // If nothing after now, try looking for first event tomorrow
            var nextEntry = curSchedule.find { it.time.isAfter(currentTime) }

            val msTillNext = if (nextEntry == null) {
                nextEntry = curSchedule[0] // First item tomorrow

                // in this case, we need to wait from now until midnight
                // then from midnight till the entry
                now.until(LocalTime.MAX, ChronoUnit.MILLIS) + LocalTime.MIN.until(nextEntry.time, ChronoUnit.MILLIS)
            } else
                now.until(nextEntry.time, ChronoUnit.MILLIS) // just the same day case

            // We start our job manually, to ensure that our global job is set before it starts running
            val job = GlobalScope.launch(start = CoroutineStart.LAZY) {
                logger.debug { "schedule coroutine sleeping for $msTillNext ms (till ${nextEntry.time})" }
                delay(msTillNext)
                logger.debug { "running scheduled item" }
                try {
                    nextEntry.toRun()
                } catch (ex: Exception) {
                    logger.error { "Ignoring exception in schedule (FIXME, use Pushover): $ex" }
                }

                // Pick a new job (we pass in the current time, to make sure we pick one _after_ that
                // regardless of how we get scheduled to run
                scheduleNext(nextEntry.time)
            }
            logger.debug { "Scheduling next job for ${nextEntry.time}" }
            scheduledJob = job
            job.start()
        }
    }
}