package ar.edu.utn.frba.mobile.clases.ui.main


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ar.edu.utn.frba.mobile.clases.R
import com.squareup.picasso.Picasso

class TweetsAdapter(private val listener: MainFragment.OnFragmentInteractionListener?): RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {
    var tweets: MutableList<Tweet> = mutableListOf<Tweet>();

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) R.layout.item_post
        else {
            val hasPicture = tweets[position].image != null
            if (hasPicture) R.layout.item_image
            else R.layout.item_simple
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        when (viewType) {
            R.layout.item_post -> {
                view.findViewById<Button>(R.id.postButton).setOnClickListener {
                    listener?.showFragment(StatusUpdateFragment())
                }
            }
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_simple, R.layout.item_image -> {
                val itemIndex = position // el primer item es el encabezado
                // todo hardcodeado, debería salir del item
                holder.itemView.findViewById<TextView>(R.id.nameText).text = tweets[itemIndex].name
                holder.itemView.findViewById<ImageView>(R.id.certifiedIcon).visibility = if (tweets[itemIndex].certified) View.VISIBLE else View.GONE
                holder.itemView.findViewById<TextView>(R.id.usernameText).text = tweets[itemIndex].username
                holder.itemView.findViewById<TextView>(R.id.tweetContent).text = tweets[itemIndex].content
                holder.itemView.findViewById<TextView>(R.id.commentCount).text = tweets[itemIndex].commentCount.toString()
                holder.itemView.findViewById<TextView>(R.id.retweetCount).text = tweets[itemIndex].retweetCount.toString()
                holder.itemView.findViewById<TextView>(R.id.likeCount).text = tweets[itemIndex].likeCount.toString()
                if (tweets[itemIndex].image != null) {
                    Picasso.get().load(Uri.parse(tweets[itemIndex].image))
                        .into(holder.itemView.findViewById<ImageView>(R.id.image))
                }
            }
            else -> {}
        }
    }

    override fun getItemCount(): Int = tweets.size + 1 // el primer item es el encabezado

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}