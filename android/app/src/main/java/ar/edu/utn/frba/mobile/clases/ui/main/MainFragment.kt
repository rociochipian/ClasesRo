package ar.edu.utn.frba.mobile.clases.ui.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.utn.frba.mobile.clases.R
import ar.edu.utn.frba.mobile.clases.databinding.MainFragmentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MainFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var tweetsAdapter: TweetsAdapter
    val service = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()) // Para parsear automágicamente el json
        .baseUrl("https://us-central1-clases-854bb.cloudfunctions.net/list/")
        .build()
        .create(TweetsService::class.java) // la interfaz que diseñaron antes

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tweetsAdapter = TweetsAdapter(listener)
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
        }
    }

    fun onButtonPressed() {
        listener?.showFragment(StatusUpdateFragment())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onStart() {
        super.onStart()
        // Simulamos un request
        binding.list.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.list.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }, 1000)
        service.getTweets().enqueue(object: Callback<TweetsWrappers> {
            override fun onResponse(call: Call<TweetsWrappers>, response: Response<TweetsWrappers>) {
                print(response.body()!!)
                tweetsAdapter.tweets = response.body()!!.tweets
            }
            override fun onFailure(call: Call<TweetsWrappers>, error: Throwable) {
                Toast.makeText(activity, "No tweets founds!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainFragment.
         */
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}