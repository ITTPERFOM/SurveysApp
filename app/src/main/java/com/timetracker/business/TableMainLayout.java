package com.timetracker.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.timetracker.surveys.R;

public class TableMainLayout extends RelativeLayout {

    public final String TAG = "TableMainLayout.java";

    // set the header titles
    String rows[] ;
    String[] headers;

    TableLayout tableA;
    TableLayout tableB;
    TableLayout tableC;
    TableLayout tableD;
    boolean ternayOperator = true;

    HorizontalScrollView horizontalScrollViewB;
    HorizontalScrollView horizontalScrollViewD;

    ScrollView scrollViewC;
    ScrollView scrollViewD;

    Context context;


    int headerCellsWidth[] ;
    int option;

    public TableMainLayout(Context context, String[] rows,int option) {

        super(context);

        this.rows = rows;

        this.option = option;

        headers = rows[0].split(Pattern.quote(","));

        this.context = context;

        headerCellsWidth = new int[headers.length];

        // initialize the main components (TableLayouts, HorizontalScrollView, ScrollView)
        this.initComponents();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();

        // no need to assemble component A, since it is just a table
        this.horizontalScrollViewB.addView(this.tableB);

        this.scrollViewC.addView(this.tableC);

        this.scrollViewD.addView(this.horizontalScrollViewD);
        this.horizontalScrollViewD.addView(this.tableD);

        // add the components to be part of the main layout
        this.addComponentToMainLayout();
        //this.setBackgroundColor(Color.RED);

        // add some table rows
        this.addTableRowToTableA();
        this. addTableRowToTableB();

        this.resizeHeaderHeight();

        this.getTableRowHeaderCellWidth();

        this.generateTableC_AndTable_B();

        this.resizeBodyTableRowHeight();
    }

    // initalized components
    private void initComponents(){

        this.tableA = new TableLayout(this.context);
        this.tableB = new TableLayout(this.context);
        this.tableC = new TableLayout(this.context);
        this.tableD = new TableLayout(this.context);

        this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
        this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);

        this.scrollViewC = new MyScrollView(this.context);
        this.scrollViewD = new MyScrollView(this.context);

        this.tableA.setBackgroundColor(Color.GREEN);
        this.horizontalScrollViewB.setBackgroundColor(Color.LTGRAY);

    }

    // set essential component IDs
    @SuppressLint("ResourceType")
    private void setComponentsId(){
        this.tableA.setId(1);
        this.horizontalScrollViewB.setId(2);
        this.scrollViewC.setId(3);
        this.scrollViewD.setId(4);
    }

    // set tags for some horizontal and vertical scroll view
    private void setScrollViewAndHorizontalScrollViewTag(){

        this.horizontalScrollViewB.setTag("horizontal scroll view b");
        this.horizontalScrollViewD.setTag("horizontal scroll view d");

        this.scrollViewC.setTag("scroll view c");
        this.scrollViewD.setTag("scroll view d");
    }

    // we add the components here in our TableMainLayout
    private void addComponentToMainLayout(){

        // RelativeLayout params were very useful here
        // the addRule method is the key to arrange the components properly
        RelativeLayout.LayoutParams componentB_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA.getId());

        RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableA.getId());

        RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT );
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

        // 'this' is a relative layout,
        // we extend this table layout as relative layout as seen during the creation of this class
        this.addView(this.tableA);
        this.addView(this.horizontalScrollViewB, componentB_Params);
        this.addView(this.scrollViewC, componentC_Params);
        this.addView(this.scrollViewD, componentD_Params);

    }



    private void addTableRowToTableA(){
        this.tableA.addView(this.componentATableRow());
    }

    private void addTableRowToTableB(){
        this.tableB.addView(this.componentBTableRow());
    }

    // generate table row of table A
    TableRow componentATableRow(){

        TableRow componentATableRow = new TableRow(this.context);
        TextView textView = this.headerTextView(" " + this.headers[0] + " ");

        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setBackgroundColor(Color.parseColor("#126BAD"));
        textView.setTypeface(null, Typeface.BOLD);

        componentATableRow.addView(textView);

        return componentATableRow;
    }

    // generate table row of table B
    TableRow componentBTableRow(){

        TableRow componentBTableRow = new TableRow(this.context);
        int headerFieldCount = this.headers.length;

        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        params.setMargins(2, 0, 0, 0);

        for(int x=0; x<(headerFieldCount-1); x++){
            TextView textView = this.headerTextView("   " + this.headers[x+1] + "   ");
            textView.setLayoutParams(params);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            textView.setBackgroundColor(Color.parseColor("#126BAD"));
            textView.setTypeface(null, Typeface.BOLD);
            componentBTableRow.addView(textView);
        }

        return componentBTableRow;
    }

    // generate table row of table C and table D
    private void generateTableC_AndTable_B(){

        // just seeing some header cell width
        for(int x=0; x<this.headerCellsWidth.length; x++){
            Log.v("TableMainLayout.java", this.headerCellsWidth[x]+"");
        }

        rows =  Arrays.copyOfRange(rows, 1, rows.length);

        for(int i=0;i < rows.length;i++){

            if( ternayOperator ){
                ternayOperator = false;
            }else {
                ternayOperator = true;
            }

            TableRow tableRowForTableC = this.tableRowForTableC(rows[i]);
            TableRow taleRowForTableD = this.taleRowForTableD(rows[i],option);

            tableRowForTableC.setBackgroundColor(Color.LTGRAY);
            taleRowForTableD.setBackgroundColor(Color.LTGRAY);

            this.tableC.addView(tableRowForTableC);
            this.tableD.addView(taleRowForTableD);

        }
    }

    // a TableRow for table C
    TableRow tableRowForTableC(String tittles){

        TableRow.LayoutParams params = new TableRow.LayoutParams( this.headerCellsWidth[0],LayoutParams.MATCH_PARENT);
        params.setMargins(0, 2, 0, 0);

        TableRow tableRowForTableC = new TableRow(this.context);

         String[] tittle = tittles.split(Pattern.quote(","));

        TextView textView = this.bodyTextView( " "+ tittle[0]+" ");

        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        if( ternayOperator ){
            textView.setBackgroundColor(Color.parseColor("#C5D9F2"));
        }else {
            textView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        tableRowForTableC.addView(textView,params);

        return tableRowForTableC;
    }

    TableRow taleRowForTableD(String column,int option){

        TableRow taleRowForTableD = new TableRow(this.context);

        int loopCount = ((TableRow)this.tableB.getChildAt(0)).getChildCount();

        String info[] = column.split(Pattern.quote(","));

        for(int x=0 ; x<loopCount; x++){
            TableRow.LayoutParams params = new TableRow.LayoutParams( headerCellsWidth[x+1],LayoutParams.MATCH_PARENT);
            params.setMargins(2, 2, 0, 0);

            TextView textViewB = this.bodyTextView( info[x+1] );

            textViewB.setTextColor(Color.DKGRAY);

            if( ternayOperator ){
                textViewB.setBackgroundColor(Color.parseColor("#C5D9F2"));
            }else {
                textViewB.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            switch(option) {
                case 1:
                    maulecColorPorcentage(textViewB);

                    if(x == loopCount - 1 ){
                        maulecColorDDI(textViewB);
                    }
                    break;
                default:

            }
            textViewB.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            taleRowForTableD.addView(textViewB,params);
        }

        return taleRowForTableD;

    }

    // table cell standard TextView
    TextView bodyTextView(String label){

        TextView bodyTextView = new TextView(this.context);
        bodyTextView.setBackgroundColor(Color.WHITE);
        bodyTextView.setText(label);
        bodyTextView.setGravity(Gravity.CENTER);
        bodyTextView.setPadding(5, 5, 5, 5);

        return bodyTextView;
    }

    // header standard TextView
    TextView headerTextView(String label){

        TextView headerTextView = new TextView(this.context);
        headerTextView.setBackgroundColor(Color.WHITE);
        headerTextView.setText(label);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setPadding(5, 5, 5, 5);

        return headerTextView;
    }

    // resizing TableRow height starts here
    void resizeHeaderHeight() {

        TableRow productNameHeaderTableRow = (TableRow) this.tableA.getChildAt(0);
        TableRow productInfoTableRow = (TableRow)  this.tableB.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    void getTableRowHeaderCellWidth(){

        int tableAChildCount = ((TableRow)this.tableA.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow)this.tableB.getChildAt(0)).getChildCount();;

        for(int x=0; x<(tableAChildCount+tableBChildCount); x++){

            if(x==0){
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableA.getChildAt(0)).getChildAt(x));
            }else{
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableB.getChildAt(0)).getChildAt(x-1));
            }

        }
    }

    // resize body table row height
    void resizeBodyTableRowHeight(){

        int tableC_ChildCount = this.tableC.getChildCount();

        for(int x=0; x<tableC_ChildCount; x++){

            TableRow productNameHeaderTableRow = (TableRow) this.tableC.getChildAt(x);
            TableRow productInfoTableRow = (TableRow)  this.tableD.getChildAt(x);

            int rowAHeight = this.viewHeight(productNameHeaderTableRow);
            int rowBHeight = this.viewHeight(productInfoTableRow);

            TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
            int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

            this.matchLayoutHeight(tableRow, finalHeight);
        }

    }

    // match all height in a table row
    // to make a standard TableRow height
    private void matchLayoutHeight(TableRow tableRow, int height) {

        int tableRowChildCount = tableRow.getChildCount();

        // if a TableRow has only 1 child
        if(tableRow.getChildCount()==1){

            View view = tableRow.getChildAt(0);
            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
            params.height = height - (params.bottomMargin + params.topMargin);

            return ;
        }

        // if a TableRow has more than 1 child
        for (int x = 0; x < tableRowChildCount; x++) {

            View view = tableRow.getChildAt(x);

            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

            if (!isTheHeighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin);
                return;
            }
        }

    }

    // check if the view has the highest height in a TableRow
    private boolean isTheHeighestLayout(TableRow tableRow, int layoutPosition) {

        int tableRowChildCount = tableRow.getChildCount();
        int heighestViewPosition = -1;
        int viewHeight = 0;

        for (int x = 0; x < tableRowChildCount; x++) {
            View view = tableRow.getChildAt(x);
            int height = this.viewHeight(view);

            if (viewHeight < height) {
                heighestViewPosition = x;
                viewHeight = height;
            }
        }

        return heighestViewPosition == layoutPosition;
    }

    // read a view's height
    private int viewHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    // read a view's width
    private int viewWidth(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }

    // horizontal scroll view custom class
    class MyHorizontalScrollView extends HorizontalScrollView{

        public MyHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("horizontal scroll view b")){
                horizontalScrollViewD.scrollTo(l, 0);
            }else{
                horizontalScrollViewB.scrollTo(l, 0);
            }
        }

    }

    // scroll view custom class
    class MyScrollView extends ScrollView{

        public MyScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("scroll view c")){
                scrollViewD.scrollTo(0, t);
            }else{
                scrollViewC.scrollTo(0,t);
            }
        }
    }

    public void maulecColorPorcentage(TextView rowText){
        if (rowText.getText().toString().indexOf("%") >= 1) {
            int porcentage = 0;
            try{
                porcentage = Integer.parseInt(rowText.getText().toString().substring(0, rowText.getText().toString().indexOf("%")));
            }catch (Exception e){
                Log.d("Prueba", e.toString());
            }
            if (porcentage >= 80) {
                rowText.setBackgroundColor(getResources().getColor(R.color.bGreen));
                rowText.setTextColor(Color.WHITE);
            } else {
                rowText.setBackgroundColor(getResources().getColor(R.color.bRed));
                rowText.setTextColor(Color.WHITE);
            }
        }
    }

    public void maulecColorDDI(TextView rowText){
        int ddi = Integer.parseInt(rowText.getText().toString());
        if (ddi >= 16) {
            rowText.setBackgroundColor(getResources().getColor(R.color.bGreen));
            rowText.setTextColor(Color.WHITE);
        } else {
            rowText.setBackgroundColor(getResources().getColor(R.color.bRed));
            rowText.setTextColor(Color.WHITE);
        }
    }
}