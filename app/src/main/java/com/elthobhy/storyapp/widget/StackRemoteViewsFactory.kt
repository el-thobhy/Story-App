package com.elthobhy.storyapp.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.data.remote.network.ApiConfig
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.widget.StackAppWidget.Companion.EXTRA_ITEM
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

internal class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<ListStoryItem>()
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    override fun onCreate() {

    }

    override fun onDataSetChanged(): Unit = runBlocking {
        val pref = UserPreferences.getInstance(context.dataStore)

        try {
            mWidgetItems.clear()
            coroutineScope {
                ApiConfig.getApiService(pref.getUserToken().first())
                    .getStoriesForWidget().listStory.let {
                        mWidgetItems.addAll(
                            it
                        )
                    }
            }
        } catch (e: Exception) {
            Log.e("StackFactory", "onResponse: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item).apply {
            val image = Glide.with(context)
                .asBitmap()
                .load(mWidgetItems[p0].photoUrl)
                .submit()
                .get()

            setImageViewBitmap(R.id.imageView, image)
        }

        val extras = bundleOf(
            EXTRA_ITEM to mWidgetItems[p0].name
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}