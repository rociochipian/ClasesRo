package ar.edu.utn.frba.mobile.clases.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ar.edu.utn.frba.mobile.clases.R
import ar.edu.utn.frba.mobile.clases.adapters.ImagesAdapter
import ar.edu.utn.frba.mobile.clases.databinding.FragmentImagesBinding
import ar.edu.utn.frba.mobile.clases.utils.storage.fileSystem.ExternalStorage
import ar.edu.utn.frba.mobile.clases.utils.storage.fileSystem.InternalStorage
import ar.edu.utn.frba.mobile.clases.utils.storage.preferences.MyPreferences

class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val pickImageIntentValue: Int = 1

    private var columnCount = 3
    private var showGrid: Boolean = true
    private lateinit var editedPictures: List<Uri>
    private var listener: ImagesFragmentInteractionListener? = null
    private lateinit var actionToggleViewGrid: MenuItem
    private lateinit var actionToggleViewList: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showGrid = MyPreferences.isGridImagesListPreferredView(context!!)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addButton.setOnClickListener {
            launchImagePicker()
        }
    }

    override fun onStart() {
        super.onStart()
        reloadImages()
    }

    private fun reloadImages() {
        editedPictures = getEditedPictures()
        binding.imagesListTitle.visibility = if (editedPictures.isEmpty()) View.VISIBLE else View.GONE

        with(binding.recyclerView) {
            layoutManager = GridLayoutManager(context, if (showGrid) columnCount else 1)
            adapter = ImagesAdapter(editedPictures)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ImagesFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ImagesFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getEditedPictures(): List<Uri> {
        val files = ExternalStorage.getFiles(requireContext())
        return files?.map { file -> file.toUri() } ?: listOf()
    }

    private fun updateRecyclerViewColumns() {
        binding.recyclerView.post {
            androidx.transition.TransitionManager.beginDelayedTransition(binding.recyclerView)
            with (binding.recyclerView.layoutManager as GridLayoutManager) {
                spanCount = if (showGrid) columnCount else 1
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        actionToggleViewGrid = menu.findItem(R.id.action_toggle_view_grid)
        actionToggleViewList = menu.findItem(R.id.action_toggle_view_list)
        initMenuActions()
    }

    private fun initMenuActions() {
        actionToggleViewGrid.isVisible = !showGrid
        actionToggleViewList.isVisible = showGrid
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_view_list, R.id.action_toggle_view_grid -> {
                showGrid = !showGrid
                MyPreferences.setGridImagesListPreferredView(context!!, showGrid)
                updateRecyclerViewColumns()
                initMenuActions()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun launchImagePicker() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, pickImageIntentValue)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == pickImageIntentValue && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return

            launchImageEdition(imageUri)

            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun launchImageEdition(imageUri: Uri) {
        listener!!.showFragment(EditImageFragment.newInstance(imageUri))
    }

    interface ImagesFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ImagesFragment()
    }
}
