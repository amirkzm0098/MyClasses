package com.example.myclasses.ui.teacher.details

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myclasses.convertTeacherToStringForShare
import com.example.myclasses.database.LessonsDatabase
import com.example.myclasses.databinding.FragmentTeacherDetailsBinding

class TeacherDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTeacherDetailsBinding
    private lateinit var viewModel: TeacherDetailsViewModel
    private lateinit var viewModelFactory: TeacherDetailsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherDetailsBinding.inflate(inflater, container, false)

        val args = TeacherDetailsFragmentArgs.fromBundle(requireArguments())
        val dataSource =
            activity?.application?.let { LessonsDatabase.getInstance(it).lessonsDatabaseDao }!!

        viewModelFactory = TeacherDetailsViewModelFactory(args.teacherId, dataSource)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(TeacherDetailsViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.teacher.observe(viewLifecycleOwner, {
            it?.let { teacher ->
                if (teacher.email != "") {
                    binding.email.text = teacher.email
                    binding.emailCardView.visibility = View.VISIBLE
                } else binding.emailCardView.visibility = View.GONE

                if (teacher.phoneNumber != "") {
                    binding.phone.text = teacher.phoneNumber
                    binding.phoneCardView.visibility = View.VISIBLE
                } else binding.phoneCardView.visibility = View.GONE

                if (teacher.address != "") {
                    binding.address.text = teacher.address
                    binding.addressCardView.visibility = View.VISIBLE
                } else binding.addressCardView.visibility = View.GONE

                if (teacher.websiteAddress != "") {
                    binding.website.text = teacher.websiteAddress
                    binding.websiteCardView.visibility = View.VISIBLE
                } else binding.websiteCardView.visibility = View.GONE
            }
        })

        binding.emailCardView.setOnClickListener {
            val email = viewModel.teacher.value?.email!!
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            val packageManager = activity?.packageManager!!
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }

        binding.phoneCardView.setOnClickListener {
            val phone = viewModel.teacher.value?.phoneNumber!!
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
            }
            val packageManager = activity?.packageManager!!
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }
        binding.addressCardView.setOnClickListener {
            val address = viewModel.teacher.value?.address!!
            val uri = Uri.parse("geo:0,0?q=$address")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val packageManager = activity?.packageManager!!
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }
        binding.websiteCardView.setOnClickListener {
            val website = viewModel.teacher.value?.websiteAddress!!
            val uri =
                if (website.startsWith("https://") || website.startsWith("http://"))
                    Uri.parse(website)
                else
                    Uri.parse("https://$website")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val packageManager = activity?.packageManager!!
            if (intent.resolveActivity(packageManager) != null)
                startActivity(intent)
        }

        binding.remove.setOnClickListener { onRemove() }

        binding.share.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                val teacher = viewModel.teacher.value!!
                putExtra(Intent.EXTRA_TEXT, convertTeacherToStringForShare(teacher))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        viewModel.navigateToEdit.observe(viewLifecycleOwner) {
            it?.let { teacher ->
                val action =
                    TeacherDetailsFragmentDirections
                        .actionTeacherDetailsFragmentToEditTeacherFragment(teacher.teacherId)
                findNavController().navigate(action)
                viewModel.onNavigateToEditDone()
            }
        }

        viewModel.navigateUp.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigateUp()
                viewModel.onNavigateUpDone()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onRemove() {
        val alertDialogBuilder = AlertDialog.Builder(context).apply {
            setMessage("Are you sure you want to Delete?")
            setPositiveButton("Yes") { _, _ ->
                viewModel.onRemove()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }
        alertDialogBuilder.create().show()
    }
}