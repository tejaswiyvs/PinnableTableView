package tejaswi_yerukalapudi.com.pinnabletableview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tejaswi_yerukalapudi.com.pinnabletableview.Model.Doctor;
import tejaswi_yerukalapudi.com.pinnabletableview.Model.DoctorAvailability;
import tejaswi_yerukalapudi.com.pinnabletableview.Model.TimeSlotStatus;

public class MainActivity extends Activity implements HorizontalScrollViewListener {

    static final float FONT_SIZE = 11;
    float mDeviceScale;
    int mRowHeight;

    private TableLayout mFrozenHeaderTable;
    private TableLayout mContentHeaderTable;
    private TableLayout mFrozenTable;
    private TableLayout mContentTable;

    private ObservableHorizontalScrollView mHeaderScrollView;
    private ObservableHorizontalScrollView mContentScrollView;

    private ArrayList<Doctor> mPhysicians;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrozenTable = (TableLayout) findViewById(R.id.physicianNamesTable);
        mContentTable = (TableLayout) findViewById(R.id.appointmentTimesTable);
        mFrozenHeaderTable = (TableLayout) findViewById(R.id.frozenTableHeader);
        mContentHeaderTable = (TableLayout) findViewById(R.id.contentTableHeader);

        mHeaderScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHeaderHorizontalScrollView);
        mHeaderScrollView.setScrollViewListener(this);
        mContentScrollView = (ObservableHorizontalScrollView) findViewById(R.id.appointmentTimesHorizontalScrollView);
        mContentScrollView.setScrollViewListener(this);
        mContentScrollView.setHorizontalScrollBarEnabled(false); // Only show the scroll bar on the header table (so that there aren't two)

        mDeviceScale = getBaseContext().getResources().getDisplayMetrics().density;
        mRowHeight = (int) (50 * mDeviceScale);

        PopulateTempData();
    }

    // Helpers
    protected void PopulateTempData() {
        mPhysicians = new ArrayList<Doctor>();

        for (int i = 0; i < 100; i++) {
            Doctor p = new Doctor();
            p.physicianName = "Physician " + i;
            p.slots = new ArrayList<DoctorAvailability>();
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
                p.slots.add(slot);
            }
            mPhysicians.add(p);
        }

        ArrayList<String> header = this.getTimeSlotHeader();

        PopulateMainTable(header, mPhysicians);
    }

    protected void PopulateMainTable(ArrayList<String> header, ArrayList<Doctor> content) {
        mFrozenTable.setBackgroundResource(R.color.lightGrey);
        mContentTable.setBackgroundResource(R.color.lightGrey);

        TableLayout.LayoutParams frozenRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        frozenRowParams.setMargins(1, 1, 1, 1);
        frozenRowParams.weight = 1;
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 1, 1, 1);
        tableRowParams.weight = 1;

        TableRow frozenTableHeaderRow = null;
        TableRow contentTableHeaderRow = null;

        // Setup table content
        for (int i = 0; i < content.size() + 1; i++) {
            String frozenText = "";
            ArrayList<String> data = new ArrayList<String>();
            if (i == 0) {
                // Header (0, 0) does not have any text.
                frozenText = "";
                data = header;
            }
            else {
                Doctor p = content.get(i - 1);
                ArrayList<DoctorAvailability> slots = p.slots;
                frozenText = p.physicianName;
                ArrayList<String> slotData = new ArrayList<String>();
                for (int j = 0; j < slots.size(); j++) {
                    slotData.add(slots.get(j).status.toString());
                }
                data = slotData;
            }

            TableRow frozenRow = new TableRow(this);
            frozenRow.setLayoutParams(frozenRowParams);
            frozenRow.setBackgroundResource(R.color.tableRows);

            TextView frozenCell = new TextView(this);
            frozenCell.setText(frozenText);
            frozenCell.setTextColor(Color.parseColor("#FF000000"));
            frozenCell.setPadding(5, 0, 5, 0);
            frozenCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);

            frozenRow.addView(frozenCell);

            // The rest of them
            TableRow row = new TableRow(this);
            row.setLayoutParams(tableRowParams);
            row.setBackgroundResource(R.color.tableRows);

            for (int j = 0; j < data.size(); j++) {
                TextView rowCell = new TextView(this);
                rowCell.setText(data.get(j));
                rowCell.setPadding(10, 0, 0, 0);
                rowCell.setGravity(Gravity.RIGHT);
                rowCell.setTextColor(Color.parseColor("#FF000000"));
                rowCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);
                row.addView(rowCell);
            }

            if (i == 0) {
                frozenTableHeaderRow = frozenRow;
                contentTableHeaderRow = row;
                mFrozenHeaderTable.addView(frozenRow);
                mContentHeaderTable.addView(row);
            }
            else {
                mFrozenTable.addView(frozenRow);
                mContentTable.addView(row);
            }
        }

        frozenTableHeaderRow.setMinimumHeight(mRowHeight);
        contentTableHeaderRow.setMinimumHeight(mRowHeight);

        setChildTextViewWidths(frozenTableHeaderRow, 100);
        setChildTextViewWidths(contentTableHeaderRow, 100);

        for (int i = 0; i < mContentTable.getChildCount(); i++) {
            TableRow frozenRow = (TableRow) mFrozenTable.getChildAt(i);
            frozenRow.setMinimumHeight(mRowHeight);
            setChildTextViewWidths(frozenRow, 100);
            TableRow row = (TableRow) mContentTable.getChildAt(i);
            row.setMinimumHeight(mRowHeight);
            setChildTextViewWidths(row, 100);
        }
    }

    private void setChildTextViewWidths(TableRow row, int width) {
        if (row == null) {
            return;
        }

        for (int i = 0; i < row.getChildCount(); i++) {
            TextView cell = (TextView) row.getChildAt(i);
            int replacementWidth = (int) Math.ceil(width * mDeviceScale);
            cell.setMinimumWidth(replacementWidth);
            cell.setMaxWidth(replacementWidth);
        }
    }

    private ArrayList<String> getTimeSlotHeader() {
        final Date start = getBeginningOfDay();
        final Date end = getEndOfDay();

        Calendar cal = new GregorianCalendar();
        cal.setTime(start);
        Date tmp = start;
        ArrayList<String> result = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");
        while (tmp.getTime() < end.getTime()) {
            result.add(dateFormat.format(tmp));
            cal.add(Calendar.MINUTE, 30);
            tmp = cal.getTime();
        }

        return result;
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

    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (scrollView== mHeaderScrollView) {
            mContentScrollView.scrollTo(x, y);
        } else if (scrollView== mContentScrollView) {
            mHeaderScrollView.scrollTo(x, y);
        }
    }
}
