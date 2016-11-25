package com.aip.dailyestimation.service;

import android.content.Context;
import android.util.Log;

import com.aip.dailyestimation.bean.AccountBean;
import com.aip.dailyestimation.bean.AddressBean;
import com.aip.dailyestimation.bean.CategoryBean;
import com.aip.dailyestimation.bean.ReceiptBean;
import com.aip.dailyestimation.common.util.IConstants;
import com.aip.dailyestimation.common.util.Logger;
import com.aip.dailyestimation.common.util.SharedPrefrenceUtil;
import com.aip.dailyestimation.common.util.Util;
import com.aip.dailyestimation.database.DatabaseHelper;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DatabaseService {

	Context mContext;

	static DatabaseService mDatabaseService;

	DatabaseHelper mDatabase;

	public static DatabaseService getInstance(Context context) {
		try {
			if (null == mDatabaseService) {
				mDatabaseService = new DatabaseService();
				mDatabaseService.mDatabase = DatabaseHelper
						.getInstance(context);
			}
			mDatabaseService.mContext = context;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mDatabaseService;
	}

	public DatabaseHelper getDatabase() {
		return mDatabase;
	}

	public AccountBean getAccount() {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("serverid", getCurrentUserId()).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public int deleteCurrentAccount() {
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> accountDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			accountDao = DaoManager.createDao(connectionSource,
					AccountBean.class);

			DeleteBuilder<AccountBean, Integer> deleteBuilder = accountDao
					.deleteBuilder();			
			deleteBuilder.where().eq("serverid", getAccount().getServerId());

			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public AccountBean getLocalAccount(int accountId) {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryForId(accountId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public AccountBean getAccount(String email, String password) {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryBuilder().where().eq("email", email)
					.and().eq("password", password).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public AccountBean getAccount(int serverAccountId) {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryBuilder().where()
					.eq("serverid", serverAccountId).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public AccountBean getAccount(String email) {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryBuilder().where().eq("email", email)
					.queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public AccountBean isValidCredentials(String email, String password) {
		AccountBean accountBean = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBean = contactDao.queryBuilder().where().eq("email", email)
					.and().eq("password", password).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBean;
	}

	public int insertUpdateContact(final AccountBean contactBean) {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<AccountBean, Integer> contactDao = DaoManager.createDao(
					connectionSource, AccountBean.class);

			if (isRecordAvailable(contactBean.getID(), contactDao)) {
				SharedPrefrenceUtil
						.setPrefrence(mContext, IConstants.PREF_CURR_USER_ID,
								contactBean.getServerId());

				AccountBean localAccountBean = getLocalAccount(contactBean
						.getID());

				contactDao.update(Util.getToAccountBean(contactBean,
						localAccountBean));
				Log.e("#insertUpdateContact#",
						"User detail Updated" + contactBean.toString());
			} else {

				int nextId = getNextAccountId(contactDao);
				SharedPrefrenceUtil
						.setPrefrence(mContext, IConstants.PREF_CURR_USER_ID,
								contactBean.getServerId());
				Log.e("#Current account id", "Current Id is " + nextId);
				contactBean.setID(nextId);
				contactDao.create(contactBean);
				Log.e("#insertUpdateContact#",
						"User detail Inserted" + contactBean.toString());
			}
			
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
		return -1;
	}

	
	public boolean isRecordAvailable(int id,
			Dao<AccountBean, Integer> contactDao) {
		try {
			AccountBean contactBean = contactDao.queryForId(id);

			if (contactBean != null) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private int getNextAccountId(final Dao<AccountBean, Integer> contactDao)
			throws SQLException {
		QueryBuilder<AccountBean, Integer> builder = contactDao.queryBuilder();
		builder.orderBy("id", false); // true or false for ascending so change
										// to true to get min id
		AccountBean accountbean = contactDao.queryForFirst(builder.prepare());
		int id = 0;
		if (accountbean == null)
			id = 0;
		else
			id = accountbean.getID();

		return ++id;
	}

	public List<CategoryBean> getAllLocalCategories() {
		List<CategoryBean> categoryBeans = null;
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);

			categoryBeans = (List<CategoryBean>) categoryDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("userid", getCurrentUserId()).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBeans;
	}

	public List<String> getAllLocalCategoriesName() {
		List<String> categoryNames = new ArrayList<String>();

		try {
			List<CategoryBean> categoryBeans = getAllLocalCategories();
			if (categoryBeans != null) {
				for (CategoryBean categoryBean : categoryBeans) {
					categoryNames.add(categoryBean.getCategoryName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryNames;
	}

	public int deleteCategory(int categoryId) {
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);

			return categoryDao.deleteById(categoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int deleteAllCategory(int userId) {
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);

			DeleteBuilder<CategoryBean, Integer> deleteBuilder = categoryDao
					.deleteBuilder();
			deleteBuilder.where().eq("userid", userId);

			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int deleteAllUserCategory() {
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);

			DeleteBuilder<CategoryBean, Integer> deleteBuilder = categoryDao
					.deleteBuilder();			

			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public CategoryBean getCategory(int categoryId) {
		CategoryBean categoryBean = null;
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);
			categoryBean = categoryDao.queryForId(categoryId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBean;
	}

	public double getCategoryCount() {
		List<CategoryBean> categoryBeans = null;
		CategoryBean categoryBean = null;
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;
		double Count = 0;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);
			// Count = categoryDao.countOf();
			categoryBeans = (List<CategoryBean>) categoryDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("userid", getCurrentUserId()).query();
			Count = categoryBeans.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Count;
	}

	public CategoryBean getCategory(String categoryName) {

		CategoryBean categoryBean = null;
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);
			categoryBean = categoryDao.queryBuilder().where()
					.eq("name", categoryName).and()
					.eq("userid", getCurrentUserId()).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBean;
	}

	public boolean isCategoryExist(CategoryBean category) {
		CategoryBean categoryBean = null;
		ConnectionSource connectionSource = null;
		Dao<CategoryBean, Integer> categoryDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			categoryDao = DaoManager.createDao(connectionSource,
					CategoryBean.class);
			categoryBean = categoryDao.queryBuilder().where()
					.eq("userid", getCurrentUserId()).and()
					.eq("name", category.getCategoryName()).or()
					.eq("categoryid", category.getCategoryID()).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return categoryBean == null ? false : true;
	}

	public int insertUpdateCategory(final CategoryBean categoryBean,
			boolean mayForceUpdate) throws IllegalAccessException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<CategoryBean, Integer> categoryDao = DaoManager
					.createDao(connectionSource, CategoryBean.class);

			boolean isCategoryExist = isCategoryExist(categoryBean);
			if (!isCategoryExist) {
				int nextId = getNextCategoryId(categoryDao);
				Logger.debug(IConstants.LOG_TAG,
						"New category added and its id is " + nextId);
				categoryBean.setCategoryID(nextId);

				int userId = getCurrentUserId();
				Log.e("#Current account id",
						"insertUpdateCategory : Current Id is " + userId);
				categoryBean.setUserId(userId);

				categoryDao.create(categoryBean);

			} else if (mayForceUpdate)

				categoryDao.update(categoryBean);
			else
				throw new IllegalAccessException(
						"Category for this name is already exist. Please try again");

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalAccessException(e.getMessage());
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
	}

	public int insertUpdateAddress(final AddressBean addressBean,
			boolean mayForceUpdate) throws IllegalAccessException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<AddressBean, Integer> addressDao = DaoManager.createDao(
					connectionSource, AddressBean.class);

			Log.d("" + addressBean.getAddressID(),
					"" + addressBean.getAddressName());

			boolean isAddressExist = isAddressExist(addressBean);
			if (!isAddressExist) {
				int nextId = getNextAddressId(addressDao);
				Logger.debug(IConstants.LOG_TAG,
						"New address added and its id is " + nextId);
				addressBean.setAddressID(nextId);

				int userId = getCurrentUserId();
				Log.e("#Current account id",
						"insertUpdateAddress : Current Id is " + userId);
				addressBean.setUserId(userId);

				addressDao.create(addressBean);

			} else if (mayForceUpdate)

				addressDao.update(addressBean);
			else
				throw new IllegalAccessException(
						"Address for this name is already exist. Please try again");

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalAccessException(e.getMessage());
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
	}

	private int getNextCategoryId(final Dao<CategoryBean, Integer> categoryDao)
			throws SQLException {
		QueryBuilder<CategoryBean, Integer> builder = categoryDao
				.queryBuilder();
		builder.orderBy("categoryid", false); // true or false for ascending so
												// change to true to get min id
		CategoryBean categoryBean = categoryDao
				.queryForFirst(builder.prepare());
		int id = 0;
		if (categoryBean == null)
			id = 0;
		else
			id = categoryBean.getCategoryID();

		return ++id;
	}

	// ============================================================================================
	// ============================================================================================

	public List<AddressBean> getAllLocalAddresses() {
		List<AddressBean> addressBeans = null;
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);

			addressBeans = (List<AddressBean>) addressDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("userid", getCurrentUserId()).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressBeans;
	}

	public List<String> getAllLocalAddressName() {
		List<String> addressNames = new ArrayList<String>();

		try {
			List<AddressBean> addressBeans = getAllLocalAddresses();
			if (addressBeans != null) {
				for (AddressBean addressBean : addressBeans) {
					addressNames.add(addressBean.getAddressName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressNames;
	}

	public int deleteAddress(int addressId) {
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);

			return addressDao.deleteById(addressId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int deleteAllAddress(int userId) {
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);

			DeleteBuilder<AddressBean, Integer> deleteBuilder = addressDao
					.deleteBuilder();
			deleteBuilder.where().eq("userid", userId);

			return deleteBuilder.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	
	public int deleteAllUserAddress() {
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);

			DeleteBuilder<AddressBean, Integer> deleteBuilder = addressDao
					.deleteBuilder();

			return deleteBuilder.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public AddressBean getAddress(int addressId) {
		AddressBean addressBean = null;
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);
			addressBean = addressDao.queryForId(addressId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressBean;
	}

	public double getAddressCount() {
		List<AddressBean> addressBeans = null;
		AddressBean addressBean = null;
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;
		double Count = 0;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);
			// Count = addressDao.countOf();
			addressBeans = (List<AddressBean>) addressDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("userid", getCurrentUserId()).query();
			Count = addressBeans.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Count;
	}

	public AddressBean getAddress(String addressName) {
		AddressBean addressBean = null;

		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);
			addressBean = addressDao.queryBuilder().where()
					.eq("name", addressName).and()
					.eq("userid", getCurrentUserId()).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return addressBean;
	}

	public boolean isAddressExist(AddressBean address) {
		AddressBean addressBean = null;
		ConnectionSource connectionSource = null;
		Dao<AddressBean, Integer> addressDao = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			addressDao = DaoManager.createDao(connectionSource,
					AddressBean.class);
			addressBean = addressDao.queryBuilder().where()
					.eq("userid", getCurrentUserId()).and()
					.eq("name", address.getAddressName()).or()
					.eq("addressid", address.getAddressID()).queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addressBean == null ? false : true;
	}

	public int insertUpdateCategory(final AddressBean addressBean,
			boolean mayForceUpdate) throws IllegalAccessException {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<AddressBean, Integer> addressDao = DaoManager.createDao(
					connectionSource, AddressBean.class);

			boolean isAddressExist = isAddressExist(addressBean);
			if (!isAddressExist) {
				int nextId = getNextAddressId(addressDao);
				Logger.debug(IConstants.LOG_TAG,
						"New address added and its id is " + nextId);
				addressBean.setAddressID(nextId);

				int userId = getCurrentUserId();
				Log.e("#Current account id",
						"insertUpdateAddress : Current Id is " + userId);
				addressBean.setUserId(userId);

				addressDao.create(addressBean);

			} else if (mayForceUpdate)

				addressDao.update(addressBean);
			else
				throw new IllegalAccessException(
						"Address for this name is already exist. Please try again");

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalAccessException(e.getMessage());
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
	}

	private int getNextAddressId(final Dao<AddressBean, Integer> addressDao)
			throws SQLException {
		QueryBuilder<AddressBean, Integer> builder = addressDao.queryBuilder();
		builder.orderBy("addressid", false); // true or false for ascending so
												// change to true to get min id
		AddressBean addressBean = addressDao.queryForFirst(builder.prepare());
		int id = 0;
		if (addressBean == null)
			id = 0;
		else
			id = addressBean.getAddressID();

		return ++id;
	}

	// ============================================================================================
	// ============================================================================================-

	public double getReceiptCount() {
		List<ReceiptBean> receiptBeans = null;
		ReceiptBean receiptBean = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;
		double Count = 0;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);
			// Count = receiptDao.countOf();
			receiptBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).where()
					.eq("userid", getCurrentUserId()).query();
			Count = receiptBeans.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Count;
	}

	public List<ReceiptBean> getAllLocalReceipts() {
		List<ReceiptBean> receiptBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			receiptBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
					.orderBy("updatedat", false).orderBy("date", false)
					.orderBy("createdat", false).orderBy("receiptid", false)
					.where().eq("userid", getCurrentUserId()).and()
					.eq("isdelete", 0).query();

			// receiptBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
			// .orderBy("receiptid", false).where()
			// .eq("userid", getCurrentUserId()).and().eq("isdelete", 0)
			// .query();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiptBeans;
	}

	public ReceiptBean getReceipt(int receiptId) {
		ReceiptBean receiptBean = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			receiptBean = receiptDao.queryForId(receiptId);

			if (receiptBean != null && receiptBean.getIsDelete() == 1)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiptBean;
	}

	public ReceiptBean getReceiptByServerId(int serverReceiptId) {
		ReceiptBean receiptBean = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			receiptBean = receiptDao.queryBuilder().where()
					.eq("serverid", serverReceiptId).and().eq("isdelete", 0)
					.queryForFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiptBean;
	}

	public int deleteReceipt(int receiptId) {
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			int i = receiptDao.deleteById(receiptId);

			Log.e("deleteReceipt", receiptId + " try to delete and result is "
					+ receiptId);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int deleteAllReceipt(int userId) {
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			DeleteBuilder<ReceiptBean, Integer> deleteBuilder = receiptDao
					.deleteBuilder();
			deleteBuilder.where().eq("userid", userId);

			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int deleteAllUserReceipt() {
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			DeleteBuilder<ReceiptBean, Integer> deleteBuilder = receiptDao
					.deleteBuilder();

			return deleteBuilder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int insertUpdateReceipt(final ReceiptBean receiptBean) {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<ReceiptBean, Integer> receiptDao = DaoManager.createDao(
					connectionSource, ReceiptBean.class);

			ReceiptBean localReceiptBean = isReceiptAvailable(
					receiptBean.getReceiptID(), receiptDao);
			if (localReceiptBean != null) {
				receiptDao.update(Util.getToReceiptBean(receiptBean,
						localReceiptBean));
			} else {

				int nextId = getNextReceiptId(receiptDao);
				Logger.debug(IConstants.LOG_TAG,
						"New recepit added and its id is " + nextId);
				receiptBean.setReceiptID(nextId);

				int accountId = getCurrentUserId();
				Log.e("#Current UserId id",
						"insertUpdateReceipt : Current Id is " + accountId);
				receiptBean.setUserId(accountId);

				receiptDao.create(receiptBean);
			}

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
		return -1;
	}

	public int insertReceipt(final ReceiptBean receiptBean) {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<ReceiptBean, Integer> receiptDao = DaoManager.createDao(
					connectionSource, ReceiptBean.class);

			int nextId = getNextReceiptId(receiptDao);
			Logger.debug(IConstants.LOG_TAG, "New recepit added and its id is "
					+ nextId);
			receiptBean.setReceiptID(nextId);

			int accountId = getCurrentUserId();
			Log.e("#Current UserId id", "insertUpdateReceipt : Current Id is "
					+ accountId);
			receiptBean.setUserId(accountId);

			receiptDao.create(receiptBean);

			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}
		}
		return -1;
	}

	public int insertUpdateReceipts(final List<ReceiptBean> receiptBeans) {
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			final Dao<ReceiptBean, Integer> receiptDao = DaoManager.createDao(
					connectionSource, ReceiptBean.class);

			receiptDao.callBatchTasks(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					for (ReceiptBean receiptBean : receiptBeans) {
						ReceiptBean localReceiptBean = isReceiptAvailable(
								receiptBean.getReceiptID(), receiptDao);
						if (localReceiptBean != null) {
							receiptDao.update(Util.getToReceiptBean(
									receiptBean, localReceiptBean));
						} else {
							receiptBean
									.setReceiptID(getNextReceiptId(receiptDao));

							int accountId = getCurrentUserId();
							Log.e("#Current user id",
									"insertUpdateReceipts : Current Id is "
											+ accountId);
							receiptBean.setUserId(accountId);

							receiptDao.create(receiptBean);
						}
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connectionSource != null) {
				try {
					connectionSource.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connectionSource = null;
			}

		}
		return -1;
	}

	public ReceiptBean isReceiptAvailable(int id,
			Dao<ReceiptBean, Integer> receiptDao) {
		ReceiptBean receiptBean = null;
		try {
			receiptBean = receiptDao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return receiptBean;
	}

	private int getNextReceiptId(final Dao<ReceiptBean, Integer> receiptDao)
			throws SQLException {
		QueryBuilder<ReceiptBean, Integer> builder = receiptDao.queryBuilder();
		builder.orderBy("receiptid", false); // true or false for ascending so
												// change to true to get min id
		ReceiptBean receiptBean = receiptDao.queryForFirst(builder.prepare());
		int id = 0;
		if (receiptBean == null)
			id = 0;
		else
			id = receiptBean.getReceiptID();

		return ++id;
	}

	public void clearData() {
		clearPreference();
		ConnectionSource connectionSource = null;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			TableUtils.clearTable(connectionSource, CategoryBean.class);
			TableUtils.clearTable(connectionSource, AddressBean.class);
			TableUtils.clearTable(connectionSource, ReceiptBean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearPreference() {
		SharedPrefrenceUtil.setPrefrence(mContext, IConstants.PREF_EMAIL, null);
		SharedPrefrenceUtil.setPrefrence(mContext, IConstants.PREF_PASSWORD,
				null);
	}

	public int getCurrentUserId() {
		return SharedPrefrenceUtil.getPrefrence(mContext,
				IConstants.PREF_CURR_USER_ID, -1);
	}

	public String getCurrentUserType() {
		return SharedPrefrenceUtil.getPrefrence(mContext,
				IConstants.PREF_CURR_USER_TYPE, "Free");
	}

	// for the syncing purpose
	public List<AccountBean> getAllAccounts() {
		List<AccountBean> accountBeans = null;
		ConnectionSource connectionSource = null;
		Dao<AccountBean, Integer> contactDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			contactDao = DaoManager.createDao(connectionSource,
					AccountBean.class);
			accountBeans = contactDao.queryBuilder()
					.orderBy(IConstants.ORDER_BY_UPDATED_AT, false).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accountBeans;
	}

	public List<ReceiptBean> getAllReceipts() {
		List<ReceiptBean> receiptBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			receiptBeans = (List<ReceiptBean>) receiptDao.queryBuilder()
					.orderBy("date", false).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return receiptBeans;
	}

	public double getAllReceiptsCount() {
		List<ReceiptBean> receiptBeans = null;
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;
		double Count = 0;
		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);
			Count = receiptDao.countOf();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Count;
	}

	public int deleteServerReceipt(int serverReceiptId) {
		ConnectionSource connectionSource = null;
		Dao<ReceiptBean, Integer> receiptDao = null;

		try {
			connectionSource = new AndroidConnectionSource(mDatabase);
			receiptDao = DaoManager.createDao(connectionSource,
					ReceiptBean.class);

			DeleteBuilder<ReceiptBean, Integer> deleteBuilder = receiptDao
					.deleteBuilder();

			deleteBuilder.where().eq("serverid", serverReceiptId);
			return deleteBuilder.delete();
			// Where<ReceiptBean, Integer> where =
			// receiptDao.deleteBuilder().where();
			// where.eq("serverid", serverReceiptId);
			// return receiptDao.deleteBuilder().delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}