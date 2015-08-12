package tejaswi_yerukalapudi.com.pinnabletableview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
    int mRowDimension;

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
        mRowDimension = (int) (50 * mDeviceScale);

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

        // Setup table header
        this.setupTableHeader(header);

        // Setup table content
        for (int i = 0; i < content.size(); i++) {
            // Physician name in the frozen row.
            Doctor p = content.get(i);
            TableRow frozenRow = getTableRow();
            TextView frozenCell = getTextView(p.physicianName);
            frozenRow.addView(frozenCell);

            // Appointment info in the rest of the sections.
            TableRow row = getTableRow();
            for (int j = 0; j < p.slots.size(); j++) {
                View cell = getStatusView(p.slots.get(j));
                row.addView(cell);
            }

            mFrozenTable.addView(frozenRow);
            mContentTable.addView(row);
        }

        // Update content heights?
        for (int i = 0; i < mContentTable.getChildCount(); i++) {
            TableRow frozenRow = (TableRow) mFrozenTable.getChildAt(i);
            frozenRow.setMinimumHeight(mRowDimension);
            setChildTextViewWidths(frozenRow, 100);
        }
    }

    /*
     *   Sets up row-0 of the table with text headers.
     */
    private void setupTableHeader(ArrayList<String> header) {
        TableRow frozenTableHeaderRow = getTableRow();
        TextView frozenCell = getTextView("");
        frozenTableHeaderRow.addView(frozenCell);

        TableRow contentTableHeaderRow = getTableRow();
        for (int j = 0; j < header.size(); j++) {
            TextView rowCell = getTextView(header.get(j));
            contentTableHeaderRow.addView(rowCell);
        }

        mFrozenHeaderTable.addView(frozenTableHeaderRow);
        mContentHeaderTable.addView(contentTableHeaderRow);

        frozenTableHeaderRow.setMinimumHeight(mRowDimension);
        contentTableHeaderRow.setMinimumHeight(mRowDimension);

        setChildTextViewWidths(frozenTableHeaderRow, 100);
        setChildTextViewWidths(contentTableHeaderRow, 100);
    }

    private View getStatusView(DoctorAvailability availability) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_doctor_availability, null);
        ImageView imageView = (ImageView) findViewById(R.id.doctorAvailabilityViewStatus);
        if (availability.status == TimeSlotStatus.Available) {
//            view.setMinimumWidth(mRowDimension);
//            view.setMinimumHeight(mRowDimension);
//            imageView.setBackgroundResource(R.drawable.available_circular_shape);
        }
        else {
//            view.setMinimumWidth(mRowDimension / 4);
//            view.setMinimumHeight(mRowDimension / 4);
//            imageView.setBackgroundResource(R.drawable.unavailable_circular_shape);
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

    private TextView getTextView(String text) {
        TextView frozenCell = new TextView(this);
        frozenCell.setText(text);
        frozenCell.setTextColor(Color.parseColor("#FF000000"));
        frozenCell.setPadding(5, 0, 5, 0);
        frozenCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);
        return frozenCell;
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

    private void setChildViewWidths(TableRow row, int width) {
        if (row == null) {
            return;
        }

        for (int i = 0; i < row.getChildCount(); i++) {
            View cell = (View) row.getChildAt(i);
            int replacementWidth = (int) Math.ceil(width * mDeviceScale);
            cell.setMinimumWidth(replacementWidth);
        }
    }

    private ArrayList<String> getTimeSlotHeader() {
        final Date start = getBeginningOfDay();
        final Date end = getEndOfDay();

        Calendar cal = new GregorianCalendar();
        cal.setTime(start);
        Date tmp = start;
        ArrayList<String> result = new ArrayList<String>();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
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
