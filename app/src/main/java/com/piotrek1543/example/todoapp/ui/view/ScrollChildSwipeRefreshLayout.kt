package com.piotrek1543.example.todoapp.ui.view

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

/**
 * Extends [SwipeRefreshLayout] to support non-direct descendant scrolling views.
 *
 * [SwipeRefreshLayout] works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 */
class ScrollChildSwipeRefreshLayout : SwipeRefreshLayout {
  private var scrollUpChild: View? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun canChildScrollUp(): Boolean {
    return scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
  }

  fun setScrollUpChild(view: View) {
    scrollUpChild = view
  }
}