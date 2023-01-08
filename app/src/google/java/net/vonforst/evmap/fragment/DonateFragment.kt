package net.vonforst.evmap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import net.vonforst.evmap.MapsActivity
import net.vonforst.evmap.R
import net.vonforst.evmap.adapter.DonationAdapter
import net.vonforst.evmap.databinding.FragmentDonateBinding
import net.vonforst.evmap.viewmodel.DonateViewModel

class DonateFragment : Fragment() {
    private lateinit var binding: FragmentDonateBinding
    private val vm: DonateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_donate, container, false)
        binding.lifecycleOwner = this
        binding.vm = vm

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        binding.toolbar.setupWithNavController(
            findNavController(),
            (requireActivity() as MapsActivity).appBarConfiguration
        )

        binding.productsList.apply {
            adapter = DonationAdapter().apply {
                onClickListener = {
                    vm.startPurchase(it, requireActivity())
                }
            }
            layoutManager = LinearLayoutManager(context)
        }

        vm.products.observe(viewLifecycleOwner) {
            print(it)
        }

        vm.purchaseSuccessful.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, R.string.donation_successful, Snackbar.LENGTH_LONG).show()
        })
        vm.purchaseFailed.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, R.string.donation_failed, Snackbar.LENGTH_LONG).show()
        })

        // Workaround for AndroidX bug: https://github.com/material-components/material-components-android/issues/1984
        view.setBackgroundColor(MaterialColors.getColor(view, android.R.attr.windowBackground))
    }
}