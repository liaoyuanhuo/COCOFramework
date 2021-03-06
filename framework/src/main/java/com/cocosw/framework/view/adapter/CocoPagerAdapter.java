package com.cocosw.framework.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for viewpager with view recycle
 * <p/>
 * Project: app-parent
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-12
 */
public abstract class CocoPagerAdapter<T> extends RecyclingPagerAdapter implements CocoAdapter<T> {

    private final LayoutInflater mInflater;
    private List<T> dataList;
    protected Context context;
    protected View.OnClickListener onViewClickInListListener;
    private boolean loading = true;


    public CocoPagerAdapter(final Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.dataList = new ArrayList<T>();
    }

    /**
     * @param dataList
     */
    public CocoPagerAdapter(final Context context, List<T> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        if (dataList == null) {
            dataList = new ArrayList<T>(0);
        }
        this.dataList = dataList;
    }

    protected boolean notEmptyView(final View view, final Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
            return false;
        } else {
            view.setVisibility(View.VISIBLE);
            return true;
        }
    }

    @Override
    public List<T> getItems() {
        return dataList;
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public T getItem(final int i) {
        if (i != AdapterView.INVALID_POSITION & i < dataList.size()) {
            return dataList.get(i);
        } else {
            return null;
        }
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = newView(position);
            holder.contentView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        fillView(holder, position, parent);
        return holder.contentView;
    }

    protected Context getContext() {
        return context;
    }

    public abstract ViewHolder newView(int position);

    public abstract void fillView(ViewHolder viewHolder, int position,
                                  ViewGroup parent);

    public View inflate(final int resourceId) {
        return this.mInflater.inflate(resourceId, null);
    }

    protected List<T> getDataList() {
        return dataList;
    }

    public void updateList(final List<T> values) {
        this.dataList = values;
    }


    @Override
    public void add(final List<T> values) {
        if (values != null) {
            this.dataList.addAll(values);
        }
    }


    @Override
    public void add(final T value) {
        if (value != null) {
            this.dataList.add(value);
        }
    }

    @Override
    public void append(final List<T> values) {
        this.dataList.addAll(0, values);
    }


    @Override
    public void append(final T values) {
        this.dataList.add(0, values);
    }


    @Override
    public void notifyDataChange() {
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        this.loading = false;
        super.notifyDataSetChanged();
    }

    @Override
    public void remove(int position) {
        this.dataList.remove(position);
    }

    /**
     * Check whether a {@link android.app.LauncherActivity.ListItem} is already in this adapter.
     *
     * @param item Item to be verified whether it is in the adapter.
     */
    public boolean contains(final T item) {
        return getDataList().contains(item);
    }

    @Override
    public void setListWatch(final View.OnClickListener listener) {
        this.onViewClickInListListener = listener;
    }

    @Override
    public void refresh() {
        getDataList().clear();
    }
}
