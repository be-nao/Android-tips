
//Jetpack Navigationを使って各タブの状態を保持する時に作った

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.linecorp.linenovel.ui.tab.AbsTabContainerFragment

@Navigator.Name("keep_state_fragment") // `keep_state_fragment` is used in navigation xml
class KeepStateNavigator(
    private val context: Context,
    private val manager: FragmentManager, // Should pass childFragmentManager.
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    private var tabContainerShowedListener: AbsTabContainerFragment.OnTabContainerShowedListener? = null

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {

        val tag = destination.id.toString()
        var initialNavigate = false

        manager.beginTransaction().let { transaction ->
            var newFragment = manager.findFragmentByTag(tag)
            var needToAddFragment = false
            if (newFragment == null) {
                val className = destination.className
                newFragment = instantiateFragment(context, manager, className, args)
                if (newFragment is AbsTabContainerFragment) {
                    tabContainerShowedListener?.let { newFragment.setOnTabContainerShowedListener(it) }
                }

                needToAddFragment = true
            }

            val currentFragment = manager.primaryNavigationFragment
            if (currentFragment != null) {
                transaction.detach(currentFragment)
            } else {
                initialNavigate = true
            }

            if (needToAddFragment) {
                transaction.add(containerId, newFragment, tag)
            } else {
                transaction.attach(newFragment)
            }

            transaction.setPrimaryNavigationFragment(newFragment)
            transaction.setReorderingAllowed(true)
            transaction.commitNow()
        }

        return if (initialNavigate) {
            destination
        } else {
            null
        }
    }
}
