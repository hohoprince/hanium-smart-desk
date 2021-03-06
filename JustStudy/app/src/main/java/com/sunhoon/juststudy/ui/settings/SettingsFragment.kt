package com.sunhoon.juststudy.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sunhoon.juststudy.R
import com.sunhoon.juststudy.data.SharedPref
import com.sunhoon.juststudy.myEnum.BreakTime
import com.sunhoon.juststudy.myEnum.ConcentrationLevel
import com.sunhoon.juststudy.time.TimeConverter

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        // SharedPreference 값 불러오기
        val sharedPref = SharedPref.getSharedPref(requireActivity())
        settingsViewModel.breakTime.value = sharedPref.getInt("breakTime", 0)
        settingsViewModel.minConcentration.value = sharedPref.getInt("minConcentration", 0)
        settingsViewModel.setStringConTime(TimeConverter.longToStringMinute(sharedPref.getLong("conTime", 0L)))

        // 집중 시간 텍스트 뷰
        val textConTime = root.findViewById<TextView>(R.id.text_con_time)
        settingsViewModel.stringConcentrationTime.observe(viewLifecycleOwner) {
            textConTime.text = it
        }

        // 휴식 시간 텍스트 뷰
        val breakTimeTextView = root.findViewById<TextView>(R.id.break_time_textview)
        settingsViewModel.breakTime.observe(viewLifecycleOwner) {
            var text = ""
            when (it) {
                0 -> text = BreakTime.MINUTE_5.description
                1 -> text = BreakTime.MINUTE_10.description
                2 -> text = BreakTime.MINUTE_15.description
                3 -> text = BreakTime.MINUTE_20.description
                4 -> text = BreakTime.MINUTE_25.description
                5 -> text = BreakTime.MINUTE_30.description
            }
            breakTimeTextView.text = text
            sharedPref.edit().putInt("breakTime", it).apply()
        }

        // 집중 시간 설정
        val constraintLayout = root.findViewById<ConstraintLayout>(R.id.concentrationTimeLayout)
        constraintLayout.setOnClickListener {
            val timePickerDialog = TimePickerDialog(it.context,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                { _, hourOfDay, minute ->
                    settingsViewModel.setStringConTime(TimeConverter.hourMinuteToStringMinute(hourOfDay, minute))
                    sharedPref.edit().putLong("conTime", TimeConverter.hourMinuteToLong(hourOfDay, minute)).apply()
                }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timePickerDialog.show()
        }

        // 휴식 시간 설정
        val breakTimeLayout = root.findViewById<ConstraintLayout>(R.id.breaktimeLayout)
        breakTimeLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setCancelable(false)
            dlg.setContentView(R.layout.dialog_breaktime)

            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (settingsViewModel.breakTime.value) {
                0 -> buttonId = R.id.radio_break_time1
                1 -> buttonId = R.id.radio_break_time2
                2 -> buttonId = R.id.radio_break_time3
                3 -> buttonId = R.id.radio_break_time4
                4 -> buttonId = R.id.radio_break_time5
                5 -> buttonId = R.id.radio_break_time6
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_break_time1 -> settingsViewModel.breakTime.value = 0
                    R.id.radio_break_time2 -> settingsViewModel.breakTime.value = 1
                    R.id.radio_break_time3 -> settingsViewModel.breakTime.value = 2
                    R.id.radio_break_time4 -> settingsViewModel.breakTime.value = 3
                    R.id.radio_break_time5 -> settingsViewModel.breakTime.value = 4
                    R.id.radio_break_time6 -> settingsViewModel.breakTime.value = 5
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.break_time_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 최소 집중도 설정
        val minConcentrationLayout = root.findViewById<ConstraintLayout>(R.id.min_concentration_layout)
        minConcentrationLayout.setOnClickListener {
            val dlg = Dialog(requireContext())
            dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dlg.setCancelable(false)
            dlg.setContentView(R.layout.dialog_min_concentration)
            // 라디오 그룹
            val radioGroup = dlg.findViewById<RadioGroup>(R.id.radioGroup)
            var buttonId = 0
            when (settingsViewModel.minConcentration.value) {
                0 -> buttonId = R.id.radio_min_concentration4
                1 -> buttonId = R.id.radio_min_concentration3
                2 -> buttonId = R.id.radio_min_concentration2
                3 -> buttonId = R.id.radio_min_concentration1
            }
            radioGroup.check(buttonId)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_min_concentration1 -> settingsViewModel.minConcentration.value = 3
                    R.id.radio_min_concentration2 -> settingsViewModel.minConcentration.value = 2
                    R.id.radio_min_concentration3 -> settingsViewModel.minConcentration.value = 1
                    R.id.radio_min_concentration4 -> settingsViewModel.minConcentration.value = 0
                }
            }

            // 확인 버튼
            val okButton = dlg.findViewById<Button>(R.id.start_screen_ok_button)
            okButton.setOnClickListener {
                dlg.dismiss()
            }

            dlg.show()
        }

        // 최소 집중도 텍스트 뷰
        val minConcentrationTextView = root.findViewById<TextView>(R.id.min_concentration_textview)
        settingsViewModel.minConcentration.observe(viewLifecycleOwner) {
            var text = ""
            when (it) {
                0 -> text = ConcentrationLevel.VERY_LOW.description
                1 -> text = ConcentrationLevel.LOW.description
                2 -> text = ConcentrationLevel.NORMAL.description
                3 -> text = ConcentrationLevel.HIGH.description
            }
            minConcentrationTextView.text = text
            sharedPref.edit().putInt("minConcentration", it).apply()
            settingsViewModel.setMinConcentration(ConcentrationLevel.getByOrdinal(it))
        }

        return root
    }

}