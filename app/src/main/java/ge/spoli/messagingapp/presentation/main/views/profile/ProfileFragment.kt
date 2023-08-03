package ge.spoli.messagingapp.presentation.main.views.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.activityViewModels
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.R
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.databinding.FragmentProfileBinding
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.main.viewmodel.MainViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        mainViewModel.mainViewError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        mainViewModel.loggedUser.observe(viewLifecycleOwner) {
            if (it.id.isNotEmpty()) {
                setLoading(false)
                loggedUserUpdated(it)
            }
        }
        setLoading(true)
        mainViewModel.fillLoggedUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        binding.profilePictureImage.setOnClickListener {
            openGallery()
        }
        binding.updateButton.setOnClickListener {
            setLoading(true)
            val username = binding.username.text.toString()
            val about = binding.jobInfo.text.toString()
            val profile = binding.profilePictureImage.tag.toString()

            mainViewModel.updateUserInfo(username, about, profile)
        }
        binding.signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            logout()
        }

    }

    private var gallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Uri? = result.data?.data
            if (data != null) {
                binding.profilePictureImage.visibility = View.GONE
                binding.profileProgressBar.visibility = View.VISIBLE

                val uid = Firebase.auth.currentUser?.uid ?: ""
                val url = "pictures/user-$uid/${data.lastPathSegment}"

                setProfilePicture(data, url)
            }
        }
    }

    private fun setProfilePicture(picture: Uri, url: String) {
        binding.profilePictureImage.visibility = View.VISIBLE
        binding.profileProgressBar.visibility = View.GONE
        binding.profilePictureImage.setImageURI(picture)
        binding.profilePictureImage.tag = url
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        gallery.launch(intent)
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.GONE
        }

        binding.signOutButton.isEnabled = !isLoading
        binding.updateButton.isEnabled = !isLoading
        binding.jobInfo.isEnabled = !isLoading
        binding.username.isEnabled = !isLoading
    }

    private fun showError(error: String) {
        if (binding.profileProgressBar.visibility == View.VISIBLE) {
            binding.profileProgressBar.visibility = View.GONE
        }
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        requireActivity().finish()
    }

    private fun loggedUserUpdated(user: UserEntity) {
        binding.username.setText(user.username)
        binding.jobInfo.setText(user.jobInfo)

        if (user.profile.isNotBlank()) {
            val storageReference = Firebase.storage.reference.child(user.profile)
            GlideApp.with(this)
                .load(storageReference)
                .placeholder(R.drawable.baseline_account_circle_24)
                .into(binding.profilePictureImage)
        }
    }

}