package com.note.notepad.common.delegate

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

fun <T : ViewBinding> Fragment.viewBinding(
    viewBindingFactory: (View) -> T
): ReadOnlyProperty<Fragment, T> =
    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
        private var binding: T? = null

        init {
            this@viewBinding.viewLifecycleOwnerLiveData.observe(this@viewBinding) { owner: LifecycleOwner? ->
                owner?.lifecycle?.addObserver(this)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
            owner.lifecycle.removeObserver(this)
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            val binding = binding
            if (binding != null) return binding

            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
            }
            return viewBindingFactory(thisRef.requireView()).also { this.binding = it }

        }
    }