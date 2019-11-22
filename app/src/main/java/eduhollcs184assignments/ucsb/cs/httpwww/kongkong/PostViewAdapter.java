package eduhollcs184assignments.ucsb.cs.httpwww.kongkong;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by scottzhu on 2017/12/3.
 * Adapter for RecyclerView.
 */

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.PostViewHolder> {

    static class Post {
        String email;
        String location;
        String title;
        String desc;
        String ID;
        int like_number;
        MainActivity.Category category;

        public Post(String email, String location, String title, String desc, MainActivity.Category category, String ID, int like_number) {
            this.email = email;
            this.location = location;
            this.title = title;
            this.desc = desc;
            this.category = category;
            this.ID = ID;
            this.like_number = like_number;
        }
    }

    List<Post> postList;

    Context context;
    PostViewAdapter(List<Post> list){
        postList = list;
        Log.d("pva", "Created Adapter.");
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_viewcard, parent, false);
        context = view.getContext();
        PostViewHolder holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final int pos = position;
        holder.postEmail.setText(postList.get(position).email);
        holder.postLocation.setText(postList.get(position).location);
        holder.postTitle.setText(postList.get(position).title);
        holder.postCategory.setText(MainActivity.Category.toString(postList.get(position).category));
        String num = postList.get(position).like_number-1+"";
        holder.postLike.setText(num);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = postList.get(pos).ID;
                Intent intent;
                intent = new Intent(context, PostShowActivity.class);
                intent.putExtra("ID", ID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView postCategory;
        TextView postEmail;
        TextView postLocation;
        TextView postTitle;
        TextView postLike;

        public PostViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            postCategory = itemView.findViewById(R.id.post_category);
            postEmail = itemView.findViewById(R.id.post_email);
            postLocation = itemView.findViewById(R.id.post_location);
            postTitle = itemView.findViewById(R.id.post_title);
            postLike = itemView.findViewById(R.id.card_like_num);
        }
    }
}
