package com.emj.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.test.calendarview.R
import com.test.calendarview.databinding.CalendarLayoutBinding
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ItemCalendarClicked{

    private val calendarViewAttr =  context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
    private val colorHeaderDayLabel =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_header_day_label,ContextCompat.getColor(context, R.color.colorLabelDays))
    private val colorHeaderDayBackground =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_header_day_background,ContextCompat.getColor(context, R.color.colorBackgroundDays))
    private val colorViewBackground =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_view_background,ContextCompat.getColor(context, R.color.colorViewBackground))
    private val colorNavBackgroundMonth =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_navigation_background_month,ContextCompat.getColor(context, R.color.colorNavigationBackgroundMonth))
    private val colorNavBackgroundYear =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_navigation_background_year,ContextCompat.getColor(context, R.color.colorNavigationBackgroundYear))
    private val colorEnabledDay     =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_enabled_day,ContextCompat.getColor(context, R.color.colorEnableDay))
    private val colorDisabledDay     =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_disabled_day,ContextCompat.getColor(context, R.color.colorDisableDay))
    private val colorSelection     =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_selection,ContextCompat.getColor(context, R.color.colorSelection))
    private val colorLabelMonth     =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_label_month,ContextCompat.getColor(context, R.color.colorLabelMonth))
    private val colorLabelYear     =  calendarViewAttr.getColor(R.styleable.CalendarView_cv_color_label_year,ContextCompat.getColor(context, R.color.colorLabelYear))
    private val formatLabelMonth     =  calendarViewAttr.getString(R.styleable.CalendarView_cv_format_label_month)?: FORMAT_MONTH
    private val formatLabelYear     =  calendarViewAttr.getString(R.styleable.CalendarView_cv_format_label_year)?: FORMAT_YEAR
    private val btnPrevious     =  calendarViewAttr.getDrawable(R.styleable.CalendarView_cv_bnt_previous)
    private val btnNext     =  calendarViewAttr.getDrawable(R.styleable.CalendarView_cv_bnt_next)


    private val mFormatter =
        SimpleDateFormat(formatLabelMonth, getMyLocal())
    private val yFormatter =
        SimpleDateFormat(formatLabelYear, getMyLocal())
    private val calendar = GregorianCalendar()

    lateinit var mAdapter: CalendarViewAdapter

    var maxDate: GregorianCalendar? = null
    var minDate: GregorianCalendar? = null
    val locale:String? = null
    var calendarDateSelected:CalendarDateSelected? = null


    private val binding: CalendarLayoutBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.calendar_layout,
            this,
            true)


    init {
        initializeUILayout()
        setCalendarView()
        setPreviousNextClickEvent()
    }

    private fun setCalendarView() {
        setUpCalendarAdapter()
        setMonthYearValues()
        enableNavigation()
        getYearStep(minDate, maxDate)
    }

    private fun initializeUILayout() {
        with(binding){
            viewContainer.setBackgroundColor(colorViewBackground)
            navigationMonth.setBackgroundColor(colorNavBackgroundMonth)
            navigationYear.setBackgroundColor(colorNavBackgroundYear)
            daysContainer.setBackgroundColor(colorHeaderDayBackground)


            displayCurrentMonth.setTextColor(colorLabelMonth)
            displayCurrentYear.setTextColor(colorLabelYear)
            val listMonth =  getListDays()
            sun.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[0]
            }
            mon.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[1]
            }
            tue.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[2]
            }
            wed.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[3]
            }
            thu.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[4]
            }
            fri.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[5]
            }
            sat.apply {
                setTextColor(colorHeaderDayLabel)
                text = listMonth[6]
            }

            previousYear.setImageResource(R.drawable.left_arrow)
            previousMonth.setImageResource(R.drawable.left_arrow)
            btnPrevious?.let{
                previousYear.setImageDrawable(it)
                previousMonth.setImageDrawable(it)

            }

            nextYear.setImageResource(R.drawable.right_arrow)
            nextMonth.setImageResource(R.drawable.right_arrow)
            btnNext?.let{
                nextYear.setImageDrawable(it)
                nextMonth.setImageDrawable(it)

            }
        }

       

    }

    private fun setPreviousNextClickEvent() {
        with(binding){
            previousMonth.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                setCalendarView()
            }
            nextMonth.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                setCalendarView()
            }
            previousYear.setOnClickListener {
                calendar.add(Calendar.MONTH, -getYearStep(minDate, calendar))
                setCalendarView()
            }
            nextYear.setOnClickListener {
                calendar.add(Calendar.MONTH, getYearStep(calendar, maxDate))
                setCalendarView()
            }
        }
        
    }

    private fun enableNavigation() {
        with(binding){
            maxDate?.let {
                nextYear.isEnabled = it.get(Calendar.YEAR) > calendar.get(Calendar.YEAR)
                nextMonth.isEnabled =
                    when {
                        it.get(Calendar.YEAR) > calendar.get(Calendar.YEAR) -> true
                        it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) -> it.get(Calendar.MONTH) > calendar.get(
                            Calendar.MONTH
                        )
                        else -> false
                    }
            }

            minDate?.let {
                previousYear.isEnabled = it.get(Calendar.YEAR) < calendar.get(Calendar.YEAR)
                previousMonth.isEnabled =
                    when {
                        it.get(Calendar.YEAR) < calendar.get(Calendar.YEAR) -> true
                        it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) -> it.get(Calendar.MONTH) < calendar.get(
                            Calendar.MONTH
                        )
                        else -> false
                    }
            }
        }
       
    }

    private fun setMonthYearValues() {
        with(binding){
            displayCurrentMonth.text = mFormatter.format(calendar.time)
            displayCurrentYear.text = yFormatter.format(calendar.time)
        }

    }

    private fun setUpCalendarAdapter() {
        val dayValueInCells = mutableListOf<GregorianCalendar>()
        val mDate = calendar.clone() as GregorianCalendar
        mDate[Calendar.DAY_OF_MONTH] = 1
        val firstDayOfTheMonth = mDate[Calendar.DAY_OF_WEEK] - 1
        mDate.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth)
        while (dayValueInCells.size < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(GregorianCalendar().apply {
                time = mDate.time
            })
            mDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        mAdapter = CalendarViewAdapter(
            dayValueInCells,
            calendar,
            minDate,
            maxDate,
            colorEnabledDay,
            colorDisabledDay,
            colorSelection,
            CalendarEvent(),
            this
        )
        val mLayoutManager = GridLayoutManager(context, 7)
        with(binding.calendarRecycler){
            layoutManager = mLayoutManager
            adapter = mAdapter
        }

    }


    private fun getYearStep(dateFrom: GregorianCalendar?, dateTo: GregorianCalendar?): Int {
        if (dateFrom == null || dateTo == null) return MAX_MONTH

        val mTo = dateTo.get(Calendar.YEAR).times(MAX_MONTH) + dateTo.get(Calendar.MONTH)
        val mFrom = dateFrom.get(Calendar.YEAR).times(MAX_MONTH) + dateFrom.get(Calendar.MONTH)
        val elapsedMonths = mTo - mFrom

        return if (elapsedMonths >= MAX_MONTH) {
            MAX_MONTH
        } else {
            elapsedMonths
        }
    }

    private fun getListDays(): MutableList<String> {
        // return 13 item (the last one is empty
        val dfs = DateFormatSymbols(getMyLocal())
        val list = mutableListOf<String>()
        dfs.shortWeekdays.toList().filter { it!= ""}.map{
            list.add(onlyFirstUpper(it.replace("[^A-Za-z]+".toRegex(), "")))
        }
        return  list
    }

    private fun onlyFirstUpper(text: String): String {
        return text.substring(0, 1).toUpperCase(Locale.ENGLISH) + text.substring(1).toLowerCase(Locale.ENGLISH)
    }

    private fun getMyLocal(): Locale {
        return if(locale == null){
            Locale.getDefault()
        }else{
            Locale(locale)
        }
    }

    companion object {
        private const val MAX_CALENDAR_COLUMN = 42
        private const val MAX_MONTH = 12
        private const val FORMAT_MONTH = "MMMM"
        private const val FORMAT_YEAR = "yyyy"
    }

    override fun onItemCalendarClicked(position: Int) {
        val dateCal = GregorianCalendar().apply {
            time = mAdapter.getItem(position).time
        }
        mAdapter.setRange(dateCal)
        calendarDateSelected?.onCalendarDateSelected(
            mAdapter.calendarEvent.dateFrom,
            mAdapter.calendarEvent.dateTo
        )
    }


}