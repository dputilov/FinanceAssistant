package com.example.financeassistant.services

import android.app.job.JobInfo
import android.app.job.JobInfo.Builder
import android.app.job.JobInfo.PRIORITY_HIGH
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.example.financeassistant.manager.DatabaseManager

class TaskService : JobService() {

    companion object {
        const val SUCCESS_KEY = "SUCCESS"
        const val FAILED_KEY = "FAILED"
        const val JOB_ID = 123
        const val PERIODIC_TIME: Long = 15 * 60 * 1000 //24 * 60 * 60 * 1000

        fun initTaskJobScheduler(context: Context) {
            val jobService = ComponentName(context, TaskService::class.java)

            val taskJobBuilder = Builder(JOB_ID, jobService)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setRequiresDeviceIdle(false)
//                .setRequiresCharging(true)
//                .setPersisted(true)
                //.setPriority(PRIORITY_HIGH)
                .setPeriodic(PERIODIC_TIME)
                .build()

            val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = jobScheduler.schedule(taskJobBuilder)

            val isJobScheduledSuccess = resultCode == JobScheduler.RESULT_SUCCESS
            Log.d("TaskService", "Job Scheduled init ${if (isJobScheduledSuccess) TaskService.SUCCESS_KEY else TaskService.FAILED_KEY}")
        }
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        Log.d("TaskService", "Job Scheduled onStartJob")

        startTaskService()
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d("TaskService", "Job Scheduled onStopJob")
        return false
    }

    private fun startTaskService() {
        autoCreateTask()
    }

    private fun autoCreateTask() {
        Thread {
            DatabaseManager.instance.autoCreateTask()
            stopTaskService()
        }.start()
    }

    private fun stopTaskService() {
       // stopSelf()
    }
}