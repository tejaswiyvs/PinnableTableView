package tejaswi_yerukalapudi.com.pinnabletableview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity implements HorizontalScrollViewListener {

    static float FONT_SIZE = 11;
    final float scale = getBaseContext().getResources().getDisplayMetrics().density;
    int cellWidthFactor = (int) Math.ceil(FONT_SIZE * scale * (FONT_SIZE < 10 ? 0.9 : 0.7));

    private TableLayout mFrozenHeaderTable;
    private TableLayout mContentHeaderTable;
    private TableLayout mFrozenTable;
    private TableLayout mContentTable;

    private ObservableHorizontalScrollView mHeaderScrollView;
    private ObservableHorizontalScrollView mContentScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrozenTable = (TableLayout) findViewById(R.id.frozenTable);
        mContentTable = (TableLayout) findViewById(R.id.contentTable);
        mFrozenHeaderTable = (TableLayout) findViewById(R.id.frozenTableHeader);
        mContentHeaderTable = (TableLayout) findViewById(R.id.contentTableHeader);

        mHeaderScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHeaderHorizontalScrollView);
        mHeaderScrollView.setScrollViewListener(this);
        mContentScrollView = (ObservableHorizontalScrollView) findViewById(R.id.contentTableHorizontalScrollView);
        mContentScrollView.setScrollViewListener(this);
        mContentScrollView.setHorizontalScrollBarEnabled(false); // Only show the scroll bar on the header table (so that there aren't two)

        PopulateTempData();
    }

    // Helpers
    protected void PopulateTempData() {
        ArrayList<String[]> content = new ArrayList<String[]>();
        for (int i = 0; i < 100; i++) {
            ArrayList<String> result = new ArrayList<String>();
            for (int j = 0; j < 100; j++) {
                String c = "" + i + j;
                result.add(c);
            }
            content.add((String[])result.toArray(new String[result.size()]));
        }
        PopulateMainTable(content);
    }


    protected void PopulateMainTable(ArrayList<String[]> content) {
        mFrozenTable.setBackgroundResource(R.color.lightGrey);
        mContentTable.setBackgroundResource(R.color.lightGrey);

        TableLayout.LayoutParams frozenRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        frozenRowParams.setMargins(1, 1, 1, 1);
        frozenRowParams.weight=1;
        TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 1, 1, 1);
        tableRowParams.weight=1;

        TableRow frozenTableHeaderRow=null;
        TableRow contentTableHeaderRow=null;
        int maxFrozenChars = 0;
        int[] maxContentChars = new int[content.get(0).length-1];

        for (int i = 0; i < content.size(); i++){
            TableRow frozenRow = new TableRow(this);
            frozenRow.setLayoutParams(frozenRowParams);
            frozenRow.setBackgroundResource(R.color.tableRows);
            TextView frozenCell = new TextView(this);
            frozenCell.setText(content.get(i)[0]);
            frozenCell.setTextColor(Color.parseColor("#FF000000"));
            frozenCell.setPadding(5, 0, 5, 0);
//            if (0 == i) { frozenCell.setTypeface(font, Typeface.BOLD);
//            } else { frozenCell.setTypeface(font, Typeface.NORMAL); }
            frozenCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);
            frozenRow.addView(frozenCell);
            if (content.get(i)[0].length() > maxFrozenChars) {
                maxFrozenChars = content.get(i)[0].length();
            }

            // The rest of them
            TableRow row = new TableRow(this);
            row.setLayoutParams(tableRowParams);
            row.setBackgroundResource(R.color.tableRows);
            for (int j = 1; j < content.get(0).length; j++) {
                TextView rowCell = new TextView(this);
                rowCell.setText(content.get(i)[j]);
                rowCell.setPadding(10, 0, 0, 0);
                rowCell.setGravity(Gravity.RIGHT);
                rowCell.setTextColor(Color.parseColor("#FF000000"));
//                if ( 0 == i) { rowCell.setTypeface(font, Typeface.BOLD);
//                } else { rowCell.setTypeface(font, Typeface.NORMAL); }
                rowCell.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FONT_SIZE);
                row.addView(rowCell);
                if (content.get(i)[j].length() > maxContentChars[j-1]) {
                    maxContentChars[j-1] = content.get(i)[j].length();
                }
            }

            if (i==0) {
                frozenTableHeaderRow=frozenRow;
                contentTableHeaderRow=row;
                mFrozenHeaderTable.addView(frozenRow);
                mContentHeaderTable.addView(row);
            } else {
                mFrozenTable.addView(frozenRow);
                mContentTable.addView(row);
            }
        }

        setChildTextViewWidths(frozenTableHeaderRow, new int[]{maxFrozenChars});
        setChildTextViewWidths(contentTableHeaderRow, maxContentChars);
        for (int i = 0; i < mContentTable.getChildCount(); i++) {
            TableRow frozenRow = (TableRow) mFrozenTable.getChildAt(i);
            setChildTextViewWidths(frozenRow, new int[]{maxFrozenChars});
            TableRow row = (TableRow) mContentTable.getChildAt(i);
            setChildTextViewWidths(row, maxContentChars);
        }
    }

    private void setChildTextViewWidths(TableRow row, int[] widths) {
        if (null==row) {
            return;
        }

        for (int i = 0; i < row.getChildCount(); i++) {
            TextView cell = (TextView) row.getChildAt(i);
            int replacementWidth =
                    widths[i] == 1
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 2)
                            : widths[i] < 3
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 1.7)
                            : widths[i] < 5
                            ? (int) Math.ceil(widths[i] * cellWidthFactor * 1.2)
                            :widths[i] * cellWidthFactor;
            cell.setMinimumWidth(replacementWidth);
            cell.setMaxWidth(replacementWidth);
        }
    }

    public void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldX, int oldY) {
        if (scrollView== mHeaderScrollView) {
            mContentScrollView.scrollTo(x, y);
        } else if (scrollView== mContentScrollView) {
            mHeaderScrollView.scrollTo(x, y);
        }
    }
}
