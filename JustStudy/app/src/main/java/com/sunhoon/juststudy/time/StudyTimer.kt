package com.sunhoon.juststudy.time

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.sunhoon.juststudy.data.StatusManager
import com.sunhoon.juststudy.myEnum.ProgressStatus
import com.sunhoon.juststudy.ui.study.StudyViewModel

class StudyTimer(millisInFuture: Long,
                 countDownInterval: Long,
                 private val settingTime: Long,
                 private var timeText: MutableLiveData<String>,
                 private val viewModel: StudyViewModel
) : CountDownTimer(millisInFuture, countDownInterval) {

    private val statusManager = StatusManager.getInstance()

    private var mOnExtendTimeListener: OnExtendTimeListener? = null


    override fun onTick(millisUntilFinished: Long) {
        statusManager.remainTime = millisUntilFinished
        val remainHours = "%02d".format(millisUntilFinished / 1000 / (60 * 60))
        val remainMinutes = "%02d".format((millisUntilFinished / 1000 % (60 * 60)) / 60)
        val remainSeconds = "%02d".format(millisUntilFinished / 1000 % 60)

        timeText.value = "$remainHours:$remainMinutes:$remainSeconds"

        Log.d("MyTag", "mill: $millisUntilFinished")
        Log.d("MyTag", "setting: $settingTime")

        // 공부가 80% 이상 진행되었을 때
        mOnExtendTimeListener?.let {
            if (millisUntilFinished < settingTime * 0.2) {
                it.onTime()
            }
        }
    }

    override fun onFinish() {

        when (statusManager.progressStatus) {
            ProgressStatus.STUDYING -> {
                Log.i("MyTag", "학습 끝")
                statusManager.progressStatus = ProgressStatus.RESTING
                viewModel.startBreakTimer()
            }
            ProgressStatus.RESTING -> {
                Log.i("MyTag", "휴식 끝")
                statusManager.progressStatus = ProgressStatus.STUDYING
                viewModel.startStudyTimer()
            }
            else -> timeText.value = "Finish"
        }
        // 알람
    }

    fun setOnExtendTimeListener(listener: OnExtendTimeListener) {
        mOnExtendTimeListener = listener
    }

    public interface OnExtendTimeListener {
        fun onTime()
    }
}