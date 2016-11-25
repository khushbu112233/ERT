package com.aip.dailyestimation.view.frag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aip.dailyestimation.R;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.adapter.ReceiptAdapter;
import com.aip.dailyestimation.common.async.DeleteReceiptAsync;
import com.aip.dailyestimation.common.util.CSVWriter;
import com.aip.dailyestimation.common.util.FunctionHelper;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.IResultListner;
import com.aip.dailyestimation.common.util.L;
import com.aip.dailyestimation.common.util.L.IL;
import com.aip.dailyestimation.common.util.ProgressHUD;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.core.CoreFragment;
import com.aip.dailyestimation.customdialog.CustomAlertDialog;
import com.aip.dailyestimation.customdialog.CustomAlertDialog.IOnOptionSelcted;
import com.aip.dailyestimation.service.DatabaseService;
import com.aip.dailyestimation.service.FilterService;
import com.aip.dailyestimation.view.activity.BillingActivity;
import com.aip.dailyestimation.view.activity.MainActivity;
import com.aip.dailyestimation.vo.FilterVO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class FilterReceiptFragment extends CoreFragment {

    View rootView;

    double Total = 0;

    Timer timer;
    File myFile;
    private String resultUrl = "result.txt";

    TextView txtNoReceipt;
    ListView listReceipt;

    TextView txtTotal;
    TextView txtTotalValue;
    TextView txtDummySign;
    TextView txtExportcsv;
    TextView txtExportpdf;

    DatabaseService mDatabaseService;

    FilterService mFilterService;

    FunctionHelper mFunctionHelper;

    Dialog dialog;

    int ReceiptID = 0;
    ProgressDialog pDialog;
    List<ReceiptBean> receiptBeanList;
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    private boolean csv_status = false;
    PdfPTable table1;
    int selectPdfCSV = 0;
    private ProgressHUD mProgressHUD;
    PdfPTable table;
    Document doc;
    int reduceReceiptCount = 0;
    ReceiptAdapter receiptAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_receipt, container,
                    false);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        pDialog = new ProgressDialog(getActivity());
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        ((MainActivity) getActivity()).setHeaderTitle(R.id.receipt_fragment);

        SharedPreferences sharedPref = getActivity().getPreferences(
                Context.MODE_PRIVATE);
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);


        init();
        if (mDatabaseService.getAccount().getUserType().equals("free")) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.bringToFront();
            //  txtExportcsv.setEnabled(false);
            //  txtExportpdf.setEnabled(false);
        } else {
            mAdView.setVisibility(View.INVISIBLE);
            //  txtExportcsv.setEnabled(true);
            //  txtExportpdf.setEnabled(true);
        }

        return rootView;
    }


    private class CreatePDFTask extends AsyncTask<Void, Integer, String> implements DialogInterface.OnCancelListener {

        //ProgressDialog progress = new ProgressDialog(getActivity());

        protected String doInBackground(Void... urls) {

            try {


                createPdf();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPreExecute() {

            // show the dialog


            mProgressHUD = ProgressHUD.show(getActivity(), "", true, false, this);
            mProgressHUD.setCancelable(false);
            mProgressHUD.setCanceledOnTouchOutside(false);
        }

        protected void onPostExecute(String result) {
            mProgressHUD.dismiss();
            promptForNextAction();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (mProgressHUD != null && mProgressHUD.isShowing())
                mProgressHUD.dismiss();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        receiptAdapter.notifyDataSetChanged();
        //refrehList();
        if (Util.isNetAvailable(getActivity())) {
            // refrehList();
            // receiptAdapter.notifyDataSetChanged();
        } else {
            L.alert(getActivity(),
                    getResources().getString(R.string.msg_internet_error));
        }

        // try {
        // // start read data from server
        //
        // timer = new Timer();
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // RefreshAdapter();
        // }
        //
        // private void RefreshAdapter() {
        // // TODO Auto-generated method stub
        // // do your thing here, such as execute AsyncTask or send
        // // data to
        // // server
        // mHandler.obtainMessage(1).sendToTarget();
        // ServiceHandler.startExportService(getActivity());
        // Log.d("Timer Work", "Timer Run");
        // }
        // }, 1, TimeUnit.MINUTES.toMillis(1));
        //
        // // TimeUnit.MINUTES.toMillis(minit)
        // // convert 1 minit to milisecconds
        //
        // // stop read data from server
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            refrehList();
            receiptAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // try {
        // timer.cancel();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    private void init() {
        receiptBeanList = new ArrayList<>();
        txtNoReceipt = (TextView) rootView.findViewById(R.id.txtNoReceiptFound);
        listReceipt = (ListView) rootView.findViewById(R.id.list_receipts);
        txtTotal = (TextView) rootView.findViewById(R.id.total);
        txtTotalValue = (TextView) rootView.findViewById(R.id.totalvalue);
        txtDummySign = (TextView) rootView.findViewById(R.id.dummySign);
        txtExportcsv = (TextView) rootView.findViewById(R.id.txtExportcsv);
        txtExportpdf = (TextView) rootView.findViewById(R.id.txtExportpdf);
        txtExportcsv.setVisibility(View.GONE);
        txtExportpdf.setVisibility(View.GONE);

        mDatabaseService = DatabaseService.getInstance(getActivity());
        mFilterService = FilterService.getInstance(getActivity());

        mFunctionHelper = FunctionHelper.getFunctionHelper(getActivity());
        //ImageView img_export=(ImageView)getActivity().findViewById(R.id.img_export);
        //img_export.setVisibility(View.VISIBLE);

        txtExportcsv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createCSV();

            }
        });


        txtExportpdf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new CreatePDFTask().execute();


                Log.v("pdf", bitmapArray.size() + "--");


            }
        });

        getActivity().findViewById(R.id.image_export).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
        getActivity().findViewById(R.id.actBar_rightText).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (Util.isNetAvailable(getActivity())) {

                            String OCR_VALUE = SharedPrefrenceUtil
                                    .getPrefrence(getActivity(),
                                            IConstants.CLOUD_OCR_VALUE, null);

                            if (OCR_VALUE.toString().equals("off")) {
                                double CountValue = mDatabaseService
                                        .getAllReceiptsCount();
                                // Toast.makeText(getActivity(), "" +
                                // CountValue,
                                // Toast.LENGTH_SHORT).show();
                                int valueCount = (int) CountValue;

                                if (valueCount < 10) {
                                    next(-1);
                                } else {
                                    Log.d("Data1", "Off1");
                                    String UserType = SharedPrefrenceUtil
                                            .getPrefrence(getActivity(),
                                                    "userType", "");
                                    if (UserType.equals("Paid")) {
                                        next(-1);
                                    } else {
                                        // Toast.makeText(getActivity(),
                                        // "Activate Your Account",
                                        // Toast.LENGTH_SHORT).show();
                                        Log.d("Data2", "Off2");
                                        L.confirmDialog(
                                                getActivity(),
                                                getResources().getString(
                                                        R.string.buy_ocr),
                                                new IL() {

                                                    @Override
                                                    public void onSuccess() {
                                                        startActivity(new Intent(
                                                                getActivity(),
                                                                BillingActivity.class));
                                                    }

                                                    @Override
                                                    public void onCancel() {

                                                    }
                                                });

                                    }

                                }

                            } else {
                                next(-1);
                            }

                        } else {
                            L.alert(getActivity(),
                                    getResources().getString(
                                            R.string.msg_internet_error));
                        }
                    }
                });

        listReceipt.setEmptyView(txtNoReceipt);

        if (Util.isNetAvailable(getActivity())) {
            refrehList();
        } else {
            // L.alert(getActivity(),
            // getResources().getString(R.string.msg_internet_error));
        }

        listReceipt.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                final int Selected = position;

                try {
                    final int receiptId = v.getId();
                    ReceiptID = receiptId;
                    final ReceiptBean receiptBean = mDatabaseService
                            .getReceipt(receiptId);
                    view(receiptBean.getReceiptID());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        listReceipt.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View v,
                                           int position, long id) {

                final int receiptId = v.getId();
                final ReceiptBean receiptBean = mDatabaseService
                        .getReceipt(receiptId);
                final String categoryName = ((TextView) v
                        .findViewById(R.id.item_txtName)).getText().toString();
                CustomAlertDialog.openOption(getActivity(), categoryName,
                        new IOnOptionSelcted() {

                            @Override
                            public void onOptionSelected(int id) {
                                switch (id) {
                                    case R.id.update:
                                        onEdit(receiptBean);
                                        break;
                                    case R.id.delete:
                                        onDelete(receiptBean);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                return true;
            }
        });
    }


    private void createPdf() throws FileNotFoundException, DocumentException {
        //     Log.e("createPdf", "--" + Pbar.getVisibility());
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdf");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.e("self", "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);


        doc = new Document();
        PdfWriter docWriter = null;

        DecimalFormat df = new DecimalFormat("0.00");

        try {

            //special font sizes
            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);

            //file path
            String path = String.valueOf(myFile);
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));

            //document header attributes
            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("GenrateCSV.com");
            doc.addTitle("Report with Column Headings");
            doc.setPageSize(PageSize.LETTER);

            //open document
            doc.open();


            if (receiptBeanList.size() == 1) {
                table = new PdfPTable(1);
                table.setWidths(new int[]{20});
            } else if (receiptBeanList.size() == 2) {
                table = new PdfPTable(2);
                table.setWidths(new int[]{20, 20});

            } else {
                table = new PdfPTable(3);
                table.setWidths(new int[]{20, 20, 20});
            }
            table.setTotalWidth(555);
            int count = receiptBeanList.size();
            Log.e("receiptBeanList", "count" + count);
            // reduceReceiptCount = receiptBeanList.size();
            for (int i = 0; i < receiptBeanList.size(); i++) {

                Log.e("receiptBeanList", "receiptBeanList" + receiptBeanList.size());
                String imagePath = receiptBeanList.get(i).getServerImgPath();

                reduceReceiptCount = count--;
                Log.e("receiptBeanList", "newCount" + reduceReceiptCount);
                PdfPCell cell = new PdfPCell();
                Paragraph paragraph1 = new Paragraph();
                //   Log.e("Width", "11 11" + imagePath);
                if (reduceReceiptCount == 1) {
                    cell.setColspan(3);
                } else if (reduceReceiptCount == 2) {
                    cell.setColspan(1);
                } else {
                    cell.setColspan(1);
                }
                Log.e("Width", "Width" + receiptBeanList.get(i).getName() + " %%% " + receiptBeanList.get(i).getAmount());
                cell.setPadding(15);
                paragraph1.add(new Paragraph(receiptBeanList.get(i).getName()));
                paragraph1.add(new Paragraph("Category: " + receiptBeanList.get(i).getCategoryName()));
                paragraph1.add(new Paragraph("Amount: " + receiptBeanList.get(i).getAmount()));
                paragraph1.add(new Paragraph("Tip: " + receiptBeanList.get(i).getTip()));
                paragraph1.add(new Paragraph("Total Amount: " + receiptBeanList.get(i).getAmount()));
                paragraph1.add(new Paragraph("Date: " + Util.getLongToDate(receiptBeanList.get(i).getDate().getTime(),
                        IConstants.DATE_FORMAT)));
                paragraph1.add(new Paragraph("Comment: " + receiptBeanList.get(i).getComment()));
                paragraph1.add(new Paragraph(""));

                if (imagePath == null || imagePath.equals("")) {
                    Log.e("Width", "---");
                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.receipt1);
                    Log.e("Width", "bm!! " + image);
                    Bitmap data = getResizedBitmap(image, 100, 100);
                    // Bitmap data = Bitmap.createScaledBitmap(image, 100, 100, false);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    data.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image1 = null;
                    try {
                        image1 = Image.getInstance(stream.toByteArray());
                        image1.scaleToFit(555f * 20f / 90f, 10000);
                        cell.addElement(image1);
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("Width", "####");
                    Bitmap bm = getBitmapFromURL(imagePath);
                    Log.e("Width", "bm " + bm);
                    Bitmap data = getResizedBitmap(bm, 100, 100);
                    //Bitmap data = Bitmap.createScaledBitmap(bm, 100, 100, false);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    data.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = null;
                    try {
                        image = Image.getInstance(stream.toByteArray());
                        image.scaleToFit(555f * 20f / 90f, 10000);

                        cell.addElement(image);
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    }
                }


               /* if (!imagePath.equals("") || imagePath != null) {
                    Log.e("Width", "####");
                    Bitmap bm = getBitmapFromURL(imagePath);
                    Bitmap data = getResizedBitmap(bm, 100, 100);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    data.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image = null;
                    try {
                        image = Image.getInstance(stream.toByteArray());
                        image.scaleToFit(555f * 20f / 90f, 10000);

                        cell.addElement(image);
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("Width", "---");
                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.receipt1);
                    Bitmap data = getResizedBitmap(image, 100, 100);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    data.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image image1 = null;
                    try {
                        image1 = Image.getInstance(stream.toByteArray());
                        image1.scaleToFit(555f * 20f / 90f, 10000);
                        cell.addElement(image1);
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    }


                }*/

                Log.e("receiptBeanList", "name " + receiptBeanList.get(i).getCategoryName());
                cell.addElement(paragraph1);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                // float[] columnWidths = new float[]{10f, 10f, 10f};
                // table.setWidths(columnWidths);
            }
            doc.add(table);
            doc.close();

        } catch (DocumentException dex) {
            dex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } /*finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }*/

        //  promptForNextAction();
    }

    public void makecell(int k) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(1);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.add("jjkhjksd");
        cell.addElement(paragraph1);
        cell.addElement(new Paragraph("Test 1"));
        cell.addElement(new Paragraph("Test 3"));
        table1.addCell(cell);
    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }


    public Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("Width", "src " + src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Width", "myBitmap " + myBitmap);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        Log.e("Width", "11 " + bm.getWidth());
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

    private void addImage(Paragraph document, Bitmap imagepath) {


        try {
            // get input stream
          /*  InputStream ims = getActivity().openFileInput(imagepath);
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            InputStream is = null;
            try {
                is = (InputStream) new URL(imagepath).getContent();
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }*/
           /* Drawable d = Drawable.createFromStream(is, "profile_picture");
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            FileOutputStream out=null;
            try {
                out = getActivity().getApplicationContext().openFileOutput("profile_picture", getActivity().getApplicationContext().MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);*/
            // Bitmap bmp = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagepath.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = null;
            try {
                image = Image.getInstance(stream.toByteArray());
            } catch (BadElementException e) {
                e.printStackTrace();
            }
            document.add(image);
        } catch (IOException ex) {
            return;
        }
    }

    private void promptForCSVFILEAction() {
        pDialog.dismiss();
        final String[] options = {getString(R.string.label_email), getString(R.string.label_preview),
                getString(R.string.label_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_email))) {
                    exportCSV();
                } else if (options[which].equals(getString(R.string.label_preview))) {
                    viewCSVFile();
                } else if (options[which].equals(getString(R.string.label_cancel))) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    private void viewCSVFile() {
        final File CSVFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_detail.csv");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(CSVFile), "text/csv");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void promptForNextAction() {
        pDialog.dismiss();
        final String[] options = {getString(R.string.label_email), getString(R.string.label_preview),
                getString(R.string.label_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_email))) {
                    emailNote();
                } else if (options[which].equals(getString(R.string.label_preview))) {
                    viewPdf();
                } else if (options[which].equals(getString(R.string.label_cancel))) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void emailNote() {
      /*  Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, mSubjectEditText.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
        Uri uri = Uri.parse(myFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);*/

        Uri uri = Uri.fromFile(myFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PDF Report");
        // emailIntent.putExtra(Intent.EXTRA_TEXT, ViewAllAccountFragment.selectac+" PDF Report");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }


    private void createCSV() {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_detail.csv"));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (writer != null) {
            writer.writeNext(new String[]{"Imageurl", "name", "Category", "Amount", "Tip", "Total Amount", "Date", "Comment"});
        }

        for (int i = 0; i < receiptBeanList.size(); i++) {
           /* if (receiptBeanList.size() == 0) {
                txtExportcsv.setVisibility(View.GONE);
            } else {
                txtExportcsv.setVisibility(View.VISIBLE);
            }
*/
            String imagePath = receiptBeanList.get(i).getServerImgPath();


            if (writer != null) {
                writer.writeNext(new String[]{imagePath, receiptBeanList.get(i).getName(), receiptBeanList.get(i).getCategoryName(), String.valueOf(receiptBeanList.get(i).getAmount()), String.valueOf(receiptBeanList.get(i).getTip()), String.valueOf(receiptBeanList.get(i).getAmount()), Util.getLongToDate(receiptBeanList.get(i).getDate().getTime(),
                        IConstants.DATE_FORMAT), receiptBeanList.get(i).getComment()});
               /* writer.writeNext(new String[]{receiptBeanList.get(i).getName()});
                writer.writeNext(new String[]{"Category: " + receiptBeanList.get(i).getCategoryName()});
                writer.writeNext(new String[]{"Amount: " + receiptBeanList.get(i).getAmount()});
                writer.writeNext(new String[]{"Tip: " + receiptBeanList.get(i).getTip()});
                writer.writeNext(new String[]{"Total Amount: " + receiptBeanList.get(i).getAmount()});
                writer.writeNext(new String[]{"Date: " + Util.getLongToDate(receiptBeanList.get(i).getDate().getTime(),
                        IConstants.DATE_FORMAT)});
                writer.writeNext(new String[]{"Comment: " + receiptBeanList.get(i).getComment()});
                writer.writeNext(new String[]{""});*/
            }
            csv_status = true;
        }

        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            Log.w("Test", e.toString());
        }
        promptForCSVFILEAction();

    }// Method  close.


    private void exportCSV() {
        if (csv_status == true) {
            //CSV file is created so we need to Export that ...
            final File CSVFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_detail.csv");
            //Log.i("SEND EMAIL TESTING", "Email sending");
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/csv");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CSV Report ");
            //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\nAdroid developer");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + CSVFile.getAbsolutePath()));
            emailIntent.setType("message/rfc822"); // Shows all application that supports SEND activity
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getActivity(), "Email client : " + ex.toString(), Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(getActivity(), "Information not available to create CSV.", Toast.LENGTH_SHORT).show();
        }
    }


    private void next(int id) {
        if (id != -1) {
            AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(IConstants.ARG_ID, id);
            addReceiptFragment.setArguments(bundle);
            switchFragment(R.id.main_content, addReceiptFragment);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select Your Choice").setItems(
                    R.array.Select_choice,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item

                            if (which == 0) {
                                ((MainActivity) getActivity()).startScan();
                            } else if (which == 1) {
                                ((MainActivity) getActivity()).submitForm();
                            } else {
                                // cancel dialog
                            }

                        }
                    });

            builder.create();
            builder.show();

        }

    }

    private void view(int id) {

        // use below code for only show receipt

        // if (id != -1) {
        // ViewReceiptFragment viewReceiptFragment = new ViewReceiptFragment();
        // Bundle bundle = new Bundle();
        // bundle.putInt(IConstants.ARG_ID, id);
        // viewReceiptFragment.setArguments(bundle);
        // switchFragment(R.id.main_content, viewReceiptFragment);
        // }

        if (Util.isNetAvailable(getActivity())) {

            ReceiptBean receiptBean = mDatabaseService.getReceipt(ReceiptID);

            if (receiptBean != null) {
                AddReceiptFragment addReceiptFragment = new AddReceiptFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(IConstants.ARG_ID, receiptBean.getReceiptID());
                addReceiptFragment.setArguments(bundle);
                switchFragment(R.id.main_content, addReceiptFragment);
            }
        } else {
            L.alert(getActivity(),
                    getResources().getString(R.string.msg_internet_error));
        }

    }


    private void refrehList() {

		/*
         * Thread t = new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { handler.sendEmptyMessage(0);
		 * receiptAdapter = new ReceiptAdapter(getActivity(), getReceipts());
		 * handler.sendEmptyMessage(2); } catch (Exception e) {
		 * e.printStackTrace(); }finally { handler.sendEmptyMessage(1); } } });
		 * 
		 * t.start();
		 */

        try {
            // handler.sendEmptyMessage(0);
            receiptAdapter = new ReceiptAdapter(getActivity(), getReceipts());
            receiptBeanList.clear();
            receiptBeanList.addAll(getReceipts());

            handler.sendEmptyMessage(2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // handler.sendEmptyMessage(1);
        }
    }

    private void onDelete(final ReceiptBean receiptBean) {
        if (receiptBean == null)
            return;

        L.confirmDialog(
                getActivity(),
                getResources().getString(R.string.msg_receipt_delete,
                        receiptBean.getName()), new L.IL() {

                    @Override
                    public void onSuccess() {
                        try {
                            if (receiptBean.getServerId() > 0) {
                                if (Util.isNetAvailable(getActivity()))
                                    onlineDelete(receiptBean);
                                else {
                                    receiptBean.setIsDelete(1);
                                    mDatabaseService
                                            .insertUpdateReceipt(receiptBean);
                                    refrehList();
                                }
                            } else {
                                mDatabaseService.deleteReceipt(receiptBean
                                        .getReceiptID());
                                refrehList();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    public void onEdit(ReceiptBean receiptBean) {
        if (receiptBean != null) {
            next(receiptBean.getReceiptID());
        }
    }

    private List<ReceiptBean> getReceipts() {
        List<ReceiptBean> receiptBeans = null;

        String SearchByAddress = SharedPrefrenceUtil.getPrefrence(
                getActivity(), "SearchByAddress", "false");

        if (SearchByAddress.equals("true")) {
            SharedPrefrenceUtil.setPrefrence(getActivity(), "SearchByAddress",
                    "false");
            if (getAddress() != null) {
                receiptBeans = mFilterService
                        .getAllReceiptsByAddress(getAddress());
                txtNoReceipt.setText("No receipt found in this " + getAddress()
                        + " address");
            }
        } else if (getCategory() != null) {
            receiptBeans = mFilterService.getAllReceipts(getCategory());
            txtNoReceipt.setText("No receipt found in this " + getCategory()
                    + " category");
        } else if (getFilter() != null) {
            receiptBeans = mFilterService.getAllReceipts(getFilter());
            txtNoReceipt.setText("No receipt found for your search");
        } else
            receiptBeans = mDatabaseService.getAllLocalReceipts();

        if (receiptBeans == null)
            receiptBeans = new ArrayList<ReceiptBean>();
        Log.d("SetAdapter", "SetAdapter");

        Total = 0;

        if (receiptBeans.size() == 0) {
            txtExportcsv.setVisibility(View.GONE);
            txtExportpdf.setVisibility(View.GONE);
        } else {
            txtExportcsv.setVisibility(View.GONE);
            txtExportpdf.setVisibility(View.GONE);
        }

        if (receiptBeans.size() > 0) {

            for (int counter = 0; counter < receiptBeans.size(); counter++) {
                Total = Total + receiptBeans.get(counter).getAmount()
                        + receiptBeans.get(counter).getTip();
            }
            Log.e("Total", "Total: " + Total);
        }

        if (Total > 0) {
            txtTotal.setVisibility(View.VISIBLE);
            txtTotalValue.setVisibility(View.VISIBLE);
            txtDummySign.setVisibility(View.VISIBLE);
        } else {
//			txtDummySign.setVisibility(View.INVISIBLE);
//			txtTotal.setVisibility(View.INVISIBLE);
//			txtTotalValue.setVisibility(View.INVISIBLE);			
        }

        txtTotalValue.setText(Util.getAmount(Total));

        return receiptBeans;
    }

    private String getCategory() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return getArguments().getString(IConstants.ARG_ID);
        }

        return null;
    }

    private String getAddress() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_ID)) {
            return getArguments().getString(IConstants.ARG_ID);
        }

        return null;
    }

    private FilterVO getFilter() {
        if (getArguments() != null
                && getArguments().containsKey(IConstants.ARG_FILTER)) {
            return (FilterVO) getArguments().getSerializable(
                    IConstants.ARG_FILTER);
        }

        return null;
    }

    private void onlineDelete(final ReceiptBean receiptBean) {
        DeleteReceiptAsync deleteReceiptAsync = new DeleteReceiptAsync(
                getActivity(), new IResultListner() {

            @Override
            public void result(Object result, boolean isSuccess) {
                if (isSuccess) {
                    mDatabaseService.deleteReceipt(receiptBean
                            .getReceiptID());
                } else {
                    receiptBean.setIsDelete(1);
                    mDatabaseService.insertUpdateReceipt(receiptBean);
                }
                refrehList();
            }
        });

        deleteReceiptAsync.execute(receiptBean.getServerId());
    }

    Handler handler = new Handler(Looper.getMainLooper(), new Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    mFunctionHelper.disableUserInteration();
                    break;
                case 1:
                    mFunctionHelper.enableUserInteration();
                    break;

                case 2:
                    listReceipt.setAdapter(receiptAdapter);
                    receiptAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
            return false;
        }
    });

}
