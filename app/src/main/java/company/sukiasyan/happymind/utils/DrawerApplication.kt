package company.sukiasyan.happymind.utils

import android.app.Application
import co.zsmb.materialdrawerkt.imageloader.drawerImageLoader
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import company.sukiasyan.happymind.R

class DrawerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        drawerImageLoader {
            set { imageView, uri, _, _ ->
                val reference = getStorage().reference.child(uri.toString().removePrefix("http://"))
                Glide.with(this@DrawerApplication)
                        .using(FirebaseImageLoader())
                        .load(reference)
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .into(imageView)
            }
            cancel {}
        }
    }

}
