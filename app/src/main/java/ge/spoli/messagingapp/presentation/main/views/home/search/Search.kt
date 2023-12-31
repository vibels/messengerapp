package ge.spoli.messagingapp.presentation.main.views.home.search

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import ge.spoli.messagingapp.common.Utils
import ge.spoli.messagingapp.databinding.SearchLayoutBinding
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@OptIn(FlowPreview::class)
class Search @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = SearchLayoutBinding.inflate(LayoutInflater.from(context), this)
    private var requestListener: RequestListener? = null
    var searchParam = ""

    init {
        val scope = MainScope()
        val length = binding.input.text?.length ?: 0
        searchParam = if (length > 2) {
            binding.input.text.toString()
        } else {
            ""
        }
        binding.input.setOnKeyListener(OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                Utils.hideSoftKeyboard(getActivity())
                binding.input.clearFocus()
                return@OnKeyListener true
            }
            false
        })
        binding
            .input
            .onChange()
            .debounce(1500)
            .onEach {
                requestListener?.onSearchRequest(it)
            }.launchIn(scope)

    }

    private fun AppCompatEditText.onChange(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length
                if (length == null) {
                    searchParam = ""
                } else if (length > 2 || length == 0) {
                    searchParam = s.toString()
                }
                trySend(searchParam).isSuccess
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(null) }
    }

    fun setRequestListener(listener: RequestListener) {
        requestListener = listener
    }

    //https://stackoverflow.com/questions/8276634/how-to-get-hosting-activity-from-a-view/32973351#32973351
    // Needed to remove focus from search keyboard on enter key press
    private fun getActivity(): Activity? {
        var context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

}