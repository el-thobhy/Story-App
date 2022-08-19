package com.elthobhy.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.elthobhy.storyapp.core.data.remote.ApiService

class StackWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}