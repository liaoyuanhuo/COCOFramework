package com.cocosw.framework.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.cocosw.accessory.connectivity.NetworkConnectivity;
import com.cocosw.framework.R;
import com.cocosw.framework.exception.CocoException;
import com.cocosw.framework.exception.ErrorCode;
import com.cocosw.framework.exception.ExceptionHandler;
import com.cocosw.framework.loader.CocoLoader;
import com.cocosw.framework.loader.ThrowableLoader;
import com.cocosw.framework.uiquery.CocoQuery;
import com.cocosw.undobar.UndoBarController;
import com.cocosw.undobar.UndoBarController.UndoListener;

public abstract class BaseFragment<T> extends SherlockFragment implements
		DialogResultListener, CocoLoader<T> {

	protected static final int NEVER = -1;
	protected static final int ONCREATE = 0;
	protected Context context;
	private T items;
	protected ThrowableLoader<T> loader;
	CocoDialog parentDialog;
	protected CocoQuery q;
	protected View v;

	/**
	 * 用于检查网络情况
	 * 
	 * @throws CocoException
	 */
	protected void checkNetwork() throws CocoException {
		if (!NetworkConnectivity.getInstance().isConnected()) {
			throw new CocoException(ErrorCode.NETWORK_ERROR,
					getString(R.string.network_error));
		}
	}

	/**
	 * 如果是embeded到activity或dialog中，关闭当前窗口的方法
	 */
	public void finish() {
		if (parentDialog != null) {
			parentDialog.dismiss();
		} else {
			getActivity().finish();
		}
	}

	/**
	 * Get exception from loader if it provides one by being a
	 * {@link ThrowableLoader}
	 * 
	 * @param loader
	 * @return exception or null if none provided
	 */
	protected Exception getException(final Loader<T> loader) {
		if (loader instanceof ThrowableLoader) {
			return ((ThrowableLoader<T>) loader).clearException();
		} else {
			return null;
		}
	}

	public ThrowableLoader<T> getLoader() {
		return loader;
	}

	/**
	 * 设置loader初始化的时机，默认为OnCreate
	 *
	 */
	protected int getLoaderOn() {
		return BaseFragment.NEVER;
	}

	public CharSequence getTitle() {
		return "";
	}

	/**
	 * Is this fragment still part of an activity and usable from the UI-thread?
	 * 
	 * @return true if usable on the UI-thread, false otherwise
	 */
	protected boolean isUsable() {
		return getActivity() != null;
	}

	public abstract int layoutId();

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getLoaderOn() == BaseFragment.ONCREATE) {
			onStartLoading();
			getLoaderManager().initLoader(0, getArguments(), this);
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		setHasOptionsMenu(true);
		q = new CocoQuery(getActivity());
	}

	@Override
	public Loader<T> onCreateLoader(final int id, final Bundle args) {
		onStartLoading();
		return loader = new ThrowableLoader<T>(getActivity(), items) {
			@Override
			public T loadData() throws Exception {
				return pendingData(args);
			}
		};
	}

	protected void onStartLoading() {

	}

	protected void onStopLoading() {
		hideLoading();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {

		// super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(layoutId(), null);
		q.recycle(v);
		q.v(this + "----> onCreateView start");
		try {
    		setupUI(v, savedInstanceState);
		} catch (final Exception e) {
            ExceptionHandler.reportError(this, Log.getStackTraceString(e));
			e.printStackTrace();
		}
		q.v(this + "----> onCreateView end");
		return v;
	}

	@Override
	public void onDestroyView() {
		q.v(this + "----> onDestroyView");
		super.onDestroyView();
		v = null;
		q = null;
	}

	@Override
	public void onDialogResult(final int requestCode, final int resultCode,
			final Bundle arguments) {
		// Intentionally left blank
	}

	/**
	 * 完成数据载入后的接口
	 * 
	 * @param items
	 */
	@Override
	public void onLoaderDone(final T items) {

	}

	@Override
	public void onLoaderReset(final Loader<T> loader) {

	}

	@Override
	public void onLoadFinished(final Loader<T> loader, final T items) {
		final Exception exception = getException(loader);
		onStopLoading();
		if (exception != null) {
			showError(exception);
			return;
		}
		onLoaderDone(items);
	}

	@Override
	public T pendingData(final Bundle arg) throws Exception {
		return null;
	}

	/**
	 * 刷新数据，重新执行loader中的操作
	 */
	public void refresh() {
		refresh(getArguments());
	}

	/**
	 * 带参数的刷新
	 * 
	 * @param b
	 */
	protected void refresh(final Bundle b) {
		onStartLoading();
		getLoaderManager().restartLoader(0, b, this);
	}

	protected abstract void setupUI(View view, Bundle bundle) throws Exception;

	/**
	 * Show exception
	 * 
	 * @param e
	 */
	@Override
	public void showError(final Exception e) {
		try {
			ExceptionHandler.handle(e, getActivity());
		} catch (final CocoException e1) {
			showRefresh(e1);
		}
	}

	protected void showRefresh(final CocoException e) {
		UndoBarController.show(getActivity(), e.getMessage(),
				new UndoListener() {

					@Override
					public void onUndo(final Parcelable token) {
						refresh();
					}
				}, UndoBarController.RETRYSTYLE);
	}

	private Base<?> getBase() {
		try {
			return (Base<?>) getActivity();
		} catch (final Exception e) {
			return null;
		}
	}

	protected void showLoading(final String str) {
		getBase().showLoading(str);
	}

	protected void hideLoading() {
		getBase().hideLoading();
	}
}
