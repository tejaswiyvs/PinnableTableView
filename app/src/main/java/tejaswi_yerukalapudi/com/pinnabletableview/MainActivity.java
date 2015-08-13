package tejaswi_yerukalapudi.com.pinnabletableview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tejaswi_yerukalapudi.com.pinnabletableview.Lib.Helper.Helper;
import tejaswi_yerukalapudi.com.pinnabletableview.Model.Doctor;
import tejaswi_yerukalapudi.com.pinnabletableview.Model.DoctorAvailability;
import tejaswi_yerukalapudi.com.pinnabletableview.Model.TimeSlotStatus;

public class MainActivity extends Activity implements HorizontalScrollViewListener {

    static final float FONT_SIZE = 12;
    static final float FROZEN_HEADER_FONT_SIZE = 14;

    // Width of the status cell
    static final float CONTENT_CELL_WIDTH = 69.0f;

    // Width of the cell that holds the physician name, billing rate.
    // This cell is frozen to the left side of the screen.
    static final float INDEX_CELL_WIDTH = 200.0f;

    // Height of both the index, regular rows should be the same
    // If not, alignment issues show up and the physician names will not be perfectly aligned with
    // the available timeslots.
    static final float CELL_HEIGHT = 69.0f;

    private TableLayout mPhysicianInfoHeaderTable;
    private TableLayout mContentHeaderTable;
    private TableLayout mPhysicianInfoTable;
    private TableLayout mContentTable;

    private ObservableHorizontalScrollView mHeaderScrollView;
    private ObservableHorizontalScrollView mContentScrollView;

    private ArrayList<Doctor> mPhysicians;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhysicianInfoTable = (TableLayout) findViewById(R.id.physicianNamesTable);
        mContentTable = (TableLayout) findViewById(R.id.appointmentTimesTable);
        mPhysicianInfoHeaderTable = (TableLayout) findViewById(R.id.frozenTableHeader);
        mContentHeaderTable = (TableLayout) findViewById(R.id.contentTableHeader);

        mHeaderScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHeaderHorizontalScrollView);
        mHeaderScrollView.setScrollViewListener(this);
        mContentScrollView = (ObservableHorizontalScrollView) findViewById(R.id.appointmentTimesHorizontalScrollView);
        mContentScrollView.setScrollViewListener(this);
        mContentScrollView.setHorizontalScrollBarEnabled(false); // Only show the scroll bar on the header table (so that there aren't two)

        PopulateTempData();
        ArrayList<Date> header = this.getTimeSlotHeader();

        this.setupCalendar();
        this.PopulateMainTable(header, mPhysicians);
    }

    // Helpers
    protected void PopulateTempData() {
        mPhysicians = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Doctor p = new Doctor();
            p.physicianName = "Physician " + i;
            p.slots = new ArrayList<>();
            for (int j = 0; j < 48; j++) {
                DoctorAvailability slot = new DoctorAvailability();
                java.util.Random random = new java.util.Random();
                int rand = random.nextInt(2);
                if (rand == 0) {
                    slot.status = TimeSlotStatus.Filled;
                }
                else if (rand == 1) {
                    slot.status = TimeSlotStatus.Available;
                }
                else if (rand == 2) {
                    slot.status = TimeSlotStatus.Unavailable;
                }
                slot.startDateTime = new Date();
                p.rate = "$" + random.nextInt(100);
                p.slots.add(slot);
            }
            mPhysicians.add(p);
        }
    }

    protected void setupMonthCalendar() {

    }

    protected void setupCalendar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout scrollViewLayout = (LinearLayout) this.findViewById(R.id.calendarLayout);

        Date startDate = getBeginningOfDay();
        Date endDate = add2Months(startDate);

        while (startDate.getTime() < endDate.getTime()) {
            View view = inflater.inflate(R.layout.view_calendar_date, null);
            TextView dateTxt = (TextView) view.findViewById(R.id.dateTxt);
            TextView dayTxt = (TextView) view.findViewById(R.id.dayTxt);
            dateTxt.setText(getDayOfMonth(startDate));
            dayTxt.setText(getDayOfWeek(startDate));
            startDate = addDay(startDate);
            scrollViewLayout.addView(view);
        }
    }

    protected void PopulateMainTable(ArrayList<Date> header, ArrayList<Doctor> content) {
        mPhysicianInfoTable.setBackgroundResource(R.color.lightGray);
        mContentTable.setBackgroundResource(R.color.lightGray);

        // Setup table header
        this.setupTableHeader(header);

        // Setup table content
        for (int i = 0; i < content.size(); i++) {
            // Physician name in the frozen row.
            Doctor p = content.get(i);
            TableRow frozenRow = getTableRow();
            View frozenCell = getPhysicianCell(p.physicianName, p.rate);
            frozenRow.addView(frozenCell);

            // Appointment info in the rest of the sections.
            TableRow row = getTableRow();
            for (int j = 0; j < p.slots.size(); j++) {
                View cell = getStatusView(p.slots.get(j));
                row.addView(cell);
            }

            mPhysicianInfoTable.addView(frozenRow);
            mContentTable.addView(row);
        }
    }

    /*
     *   Sets up row-0 of the table with text headers.
     */
    private void setupTableHeader(ArrayList<Date> header) {
        TableRow frozenTableHeaderRow = getTableRow();
        View frozenCell = getPhysicianCell("Specialist", "$");

        // Update the font color to light gray.
        TextView nameTxt = (TextView) frozenCell.findViewById(R.id.physicianRateCellName);
        TextView rateTxt = (TextView) frozenCell.findViewById(R.id.physicianRateCellRate);
        nameTxt.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        nameTxt.setTextSize(FROZEN_HEADER_FONT_SIZE);
        nameTxt.setTypeface(nameTxt.getTypeface(), Typeface.NORMAL);
        rateTxt.setTextSize(FROZEN_HEADER_FONT_SIZE);
        rateTxt.setTypeface(rateTxt.getTypeface(), Typeface.NORMAL);

        frozenTableHeaderRow.addView(frozenCell);

        TableRow contentTableHeaderRow = getTableRow();
        for (int j = 0; j < header.size(); j++) {
            TextView rowCell = getTextView();

            // xx:30
            if (j % 2 != 0) {
                rowCell.setTextSize(11);
                rowCell.setTextColor(getResources().getColor(R.color.alternateSecondaryTextColor));
                rowCell.setText(longDateFormat(header.get(j)));
            }
            // xx:00
            else {
                rowCell.setTextSize(14);
                rowCell.setTextColor(getResources().getColor(R.color.alternatePrimaryTextColor));
                rowCell.setText(simpleDateFormat(header.get(j)));
            }
            contentTableHeaderRow.addView(rowCell);
        }

        mPhysicianInfoHeaderTable.addView(frozenTableHeaderRow);
        mContentHeaderTable.addView(contentTableHeaderRow);
    }

    private View getPhysicianCell(String doctorName, String rate) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_physician_rate_cell, null);
        view.setMinimumWidth(getIndexCellWidth());
        view.setMinimumHeight(getCellHeight());
        TextView physicianNameTxtView = (TextView)view.findViewById(R.id.physicianRateCellName);
        TextView physicianRateTxtView = (TextView)view.findViewById(R.id.physicianRateCellRate);
        physicianNameTxtView.setText(doctorName);
        physicianRateTxtView.setText(rate);
        return view;
    }

    private View getStatusView(DoctorAvailability availability) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_doctor_availability, null);
        view.setMinimumWidth(getContentCellWidth());
        view.setMinimumHeight(getCellHeight());

        ImageView imageView = (ImageView) view.findViewById(R.id.doctorAvailabilityViewStatus);
        if (availability.status == TimeSlotStatus.Available) {
            imageView.setBackgroundResource(R.drawable.slot_available);
        }
        else {
            imageView.setBackgroundResource(R.drawable.slot_unavailable);
        }
        return view;
    }

    private TableLayout.LayoutParams getTableRowLayoutParams() {
        TableLayout.LayoutParams frozenRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        frozenRowParams.setMargins(1, 1, 1, 1);
        frozenRowParams.weight = 1;
        return frozenRowParams;
    }

    private TableRow getTableRow() {
        TableRow frozenRow = new TableRow(this);
        frozenRow.setLayoutParams(getTableRowLayoutParams());
        frozenRow.setBackgroundResource(R.color.tableRows);
        return frozenRow;
    }

    private TextView getTextView() {
        TextView cell = new TextView(this);
        cell.setTextColor(getResources().getColor(R.color.primaryTextColor));
        cell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);
        cell.setPadding(5, 0, 5, 0);
        cell.setMinimumWidth(getContentCellWidth());
        cell.setMinimumHeight(getCellHeight());
        cell.setGravity(Gravity.CENTER);
        return cell;
    }

    private ArrayList<Date> getTimeSlotHeader() {
        final Date start = getBeginningOfDay();
        final Date end = getEndOfDay();

        Calendar cal = new GregorianCalendar();
        cal.setTime(start);
        Date tmp = start;
        ArrayList<Date> result = new ArrayList<>();
        while (tmp.getTime() < end.getTime()) {
            result.add(tmp);
            cal.add(Calendar.MINUTE, 30);
            tmp = cal.getTime();
        }

        return result;
    }

    private String simpleDateFormat(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("h a");
        return dateFormat.format(date);
    }

    private String longDateFormat(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("H:mm");
        return dateFormat.format(date);
    }

    private String getDayOfMonth(Date date) {
        DateFormat format = new SimpleDateFormat("d");
        return format.format(date);
    }

    private String getDayOfWeek(Date date) {
        DateFormat format = new SimpleDateFormat("EEE");
        return format.format(date);
    }

    private Date addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    private Date getBeginningOfDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }

    private Date add2Months(Date date) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.add(Calendar.MONTH, 2);
        return calendar.getTime();
    }

    private int getContentCellWidth() {
        return Helper.getHeightForDP(this, CONTENT_CELL_WIDTH);
    }

    private int getIndexCellWidth() {
        return Helper.getHeightForDP(this, INDEX_CELL_WIDTH);
    }

    private int getCellHeight() {
        return Helper.getHeightForDP(this, CELL_HEIGHT);
    }

    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (scrollView== mHeaderScrollView) {
            mContentScrollView.scrollTo(x, y);
        } else if (scrollView== mContentScrollView) {
            mHeaderScrollView.scrollTo(x, y);
        }
    }
}
