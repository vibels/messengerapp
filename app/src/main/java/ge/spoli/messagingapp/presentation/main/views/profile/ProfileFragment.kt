package ge.spoli.messagingapp.presentation.main.views.profile

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import com.bumptech.glide.request.target.Target
import ge.spoli.messagingapp.R
import ge.spoli.messagingapp.common.BitmapTransform
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.databinding.FragmentProfileBinding
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.main.viewmodel.MainViewModel


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var binding: FragmentProfileBinding

    //These are saved in activity, because we don't want to save them unless user presses update
    private var tempUri: Uri? = null
    private var tempUrl: String = Constants.DEFAULT_PROFILE

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
                tempUrl = it.profile
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

            mainViewModel.updateUserInfo(username, about, tempUrl, tempUri)
        }
        binding.signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            logout()
        }

    }

    private var gallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    binding.profilePictureImage.visibility = View.GONE
                    binding.profileProgressBar.visibility = View.VISIBLE
                    tempUri = uri

                    val uid = Firebase.auth.currentUser?.uid ?: ""
                    tempUrl = "pictures/user-$uid/${uri.lastPathSegment}"

                    setProfilePicture(uri)
                }
            }
        }

    //Needed to remove loading bar after resume on picture load
    override fun onResume() {
        super.onResume()
        binding.profileProgressBar.visibility = View.GONE
        binding.profilePictureImage.visibility = View.VISIBLE
    }
    private fun setProfilePicture(picture: Uri) {
        binding.profileProgressBar.visibility = View.GONE
        Picasso.with(context).load(picture)
            .transform(BitmapTransform(Constants.MAX_WIDTH, Constants.MAX_HEIGHT))
            .resize(150, 150)
            .centerInside()
            .into(binding.profilePictureImage)
        binding.profilePictureImage.visibility = View.GONE
        binding.profilePictureImage.visibility = View.VISIBLE
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
        if (binding.loading.visibility == View.VISIBLE) {
            binding.loading.visibility = View.GONE
        }
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        requireActivity().finish()
    }

    private fun loggedUserUpdated(user: UserEntity) {
        binding.profilePictureImage.visibility = View.GONE
        binding.profileProgressBar.visibility = View.VISIBLE
        binding.username.setText(user.username)
        binding.jobInfo.setText(user.jobInfo)
        if (user.profile.isNotBlank()) {
            val storageReference = Firebase.storage.reference.child(user.profile)
            GlideApp.with(this)
                .load(storageReference)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        binding.profileProgressBar.visibility = View.GONE
                        binding.profilePictureImage.visibility = View.VISIBLE
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.profileProgressBar.visibility = View.GONE
                        binding.profilePictureImage.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(binding.profilePictureImage)

        }
    }

}