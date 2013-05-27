package com.cocosw.framework.core;

import android.support.v4.app.Fragment;

public interface Carousel {

	Pager<Carousel> getCurrentPager();

	void nav(Class<? extends Fragment> clz);

	Pager<Carousel> nextPager(final int id);

	Pager<Carousel> prevPager(final int id);
}
