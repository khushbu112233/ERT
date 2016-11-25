package com.aip.dailyestimation.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.Logger;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.IOException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DB_NAME = "DailyEstimation";
	private static final int DATABASE_VERSION = 1;
	private Context mContext;
	public static String Lock = "dblock";
	private SQLiteDatabase mDataBase;

	private static DatabaseHelper uniqueInstance;

	public static DatabaseHelper getInstance(Context context) throws Exception {
		if (null == uniqueInstance) {
			uniqueInstance = new DatabaseHelper(context);
		}

		return uniqueInstance;
	}

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 * @throws IOException
	 */
	private DatabaseHelper(Context context) throws Exception {
		super(context, DB_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTable(connectionSource, AccountBean.class);
			TableUtils.createTable(connectionSource, CategoryBean.class);
			TableUtils.createTable(connectionSource, AddressBean.class);
			TableUtils.createTable(connectionSource, ReceiptBean.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1,
			int oldVersion, int newVersion) {
		try {

			// if (oldVersion < 2) {
			// // we added the date column in version 2
			// final Dao<ReceiptBean, Integer> receiptDao =
			// DaoManager.createDao(connectionSource, ReceiptBean.class);
			// receiptDao.executeRaw("ALTER TABLE `TblReceipt` ADD COLUMN date DATE;");
			// }
			//
			// if (oldVersion < 3) {
			// // we added the server id column in version 3
			// final Dao<ReceiptBean, Integer> receiptDao =
			// DaoManager.createDao(connectionSource, ReceiptBean.class);
			// receiptDao.executeRaw("ALTER TABLE `TblReceipt` ADD COLUMN serverid INTEGER;");
			// receiptDao.executeRaw("ALTER TABLE `TblReceipt` ADD COLUMN accountid INTEGER;");
			//
			// final Dao<ReceiptBean, Integer> categoryDao =
			// DaoManager.createDao(connectionSource, CategoryBean.class);
			// categoryDao.executeRaw("ALTER TABLE `TblCategory` ADD COLUMN serverid INTEGER;");
			// categoryDao.executeRaw("ALTER TABLE `TblCategory` ADD COLUMN accountid INTEGER;");
			//
			// final Dao<ReceiptBean, Integer> accountDao =
			// DaoManager.createDao(connectionSource, AccountBean.class);
			// accountDao.executeRaw("ALTER TABLE `TblAccount` ADD COLUMN serverid INTEGER;");
			// }
			TableUtils.dropTable(arg1, AccountBean.class, true);
			TableUtils.dropTable(arg1, CategoryBean.class, true);
			TableUtils.dropTable(arg1, AddressBean.class, true);
			TableUtils.dropTable(arg1, ReceiptBean.class, true);

			onCreate(arg0);

		} catch (Exception e) {
			Logger.error("onUpgrade", e.getMessage());
		} finally {
			// onCreate(arg0, arg1);
		}
		Log.d("OnUpgrade", "Called");
	}
}
