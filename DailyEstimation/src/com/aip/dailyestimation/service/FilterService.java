package com.aip.dailyestimation.service;

import android.content.Context;

import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.database.DatabaseHelper;
import com.aip.dailyestimation.vo.FilterVO;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.util.List;

public class FilterService {

	Context mContext;

	static FilterService mFilterService;

	DatabaseService mDatabaseService;

	DatabaseHelper mDatabase;

	public static FilterService getInstance(Context context) {
		try {
			if (null == mFilterService) {
				mFilterService = new FilterService();
				mFilterService.mDatabaseService = DatabaseService
						.getInstance(context);
			}
			mFilterService.mContext = context;

			mFilterService.mDatabase = mFilterService.mDatabaseService
					.getDatabase();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mFilterService;
	}

	public List<ReceiptBean> getAllReceipts(String categoryName) {
		List<ReceiptBean> categoryBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			categoryBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
					.orderBy("date", false).where()
					.eq(IConstants.CATEGORY_NAME, categoryName).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBeans;
	}

	public List<ReceiptBean> getAllReceiptsByAddress(String addressName) {
		List<ReceiptBean> addressBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			addressBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
					.orderBy("date", false).where()
					.eq(IConstants.ADDRESS_NAME, addressName).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressBeans;
	}
	
	public List<ReceiptBean> getAllReceipts(FilterVO filterVO) {
		List<ReceiptBean> categoryBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			QueryBuilder<ReceiptBean, Integer> queryBuilder = receiptDao
					.queryBuilder();
			queryBuilder.orderBy("date", false);
			Where<ReceiptBean, Integer> w = queryBuilder.where();

			int cnt = 0;
			if (filterVO.getFromCalendar() != null
					&& filterVO.getToCalendar() != null) {
				
				w.between("date", filterVO.getFromCalendar().getTime(),
						filterVO.getToCalendar().getTime());
				++cnt;
			} else if (filterVO.getFromCalendar() != null) {
				w.ge("date", filterVO.getFromCalendar().getTime());
				++cnt;
			} else if (filterVO.getToCalendar() != null) {
				w.le("date", filterVO.getToCalendar().getTime());
				++cnt;
			}

			if (filterVO.getCategoryName() != null) {
				w.eq(IConstants.CATEGORY_NAME, filterVO.getCategoryName());
				++cnt;
			}

			if (filterVO.getMaxAmount() != null
					&& filterVO.getMinAmount() != null) {				
				w.between("amount", filterVO.getMinAmount(),
						filterVO.getMaxAmount());
				++cnt;
			} else if (filterVO.getMaxAmount() != null) {
				w.le(IConstants.AMOUNT, filterVO.getMaxAmount());
				++cnt;
			} else if (filterVO.getMinAmount() != null) {
				w.gt(IConstants.AMOUNT, filterVO.getMinAmount());
				++cnt;
			} else if (filterVO.getAmount() != null) {
				w.eq(IConstants.AMOUNT, filterVO.getAmount());
				++cnt;
			}

			if (filterVO.getComment() != null
					&& filterVO.getComment().trim().length() > 0) {
				w.like(IConstants.COMMENT, "%" + filterVO.getComment() + "%");
				++cnt;
			}

			if (cnt > 0) {
				w.and(cnt);
			}

			categoryBeans = (List<ReceiptBean>) queryBuilder.query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBeans;
	}
}